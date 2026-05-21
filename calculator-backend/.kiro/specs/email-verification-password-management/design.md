# Design Document: Email Verification and Password Management

## Overview

This design implements a secure email verification and password management system for user account lifecycle management. The system handles three primary workflows:

1. **Registration & Email Verification**: New users register, receive verification emails with time-limited tokens, and confirm their email address
2. **Password Reset**: Users who forget their password request a reset, receive a token via email, and set a new password
3. **Password Update**: Authenticated users change their password by providing their current password

The design prioritizes security through token expiry validation, secure password hashing, constant-time comparison, and asynchronous email delivery to prevent blocking operations.

## Architecture

### High-Level Components

```
┌─────────────────────────────────────────────────────────────┐
│                    RegistrationController                    │
│  (HTTP endpoints for registration, verification, password)   │
└────────────────────┬────────────────────────────────────────┘
                     │
        ┌────────────┼────────────┐
        │            │            │
        ▼            ▼            ▼
┌──────────────┐ ┌──────────────┐ ┌──────────────┐
│ IUserAuth    │ │ ISecurity    │ │ Email        │
│ Service      │ │ UserService  │ │ Service      │
│ (Business    │ │ (Auth checks)│ │ (Async       │
│  Logic)      │ │              │ │  delivery)   │
└──────────────┘ └──────────────┘ └──────────────┘
        │
        ▼
┌──────────────────────────────────────────┐
│         User Repository Layer            │
│  (User, VerificationToken, Password      │
│   ResetToken persistence)                │
└──────────────────────────────────────────┘
        │
        ▼
┌──────────────────────────────────────────┐
│         Database                         │
│  (User, VerificationToken, Password      │
│   ResetToken tables)                     │
└──────────────────────────────────────────┘
```

### Data Flow Diagrams

#### Registration & Email Verification Flow

```
User Registration Request
    │
    ▼
POST /register (UserDto)
    │
    ▼
IUserAuthService.registerNewUserAccount()
    ├─ Validate UserDto
    ├─ Hash password
    ├─ Create User (verified=false)
    ├─ Save to database
    └─ Return User
    │
    ▼
OnRegistrationCompleteEvent published
    │
    ▼
Email Service receives event
    ├─ Generate VerificationToken
    ├─ Save token to database
    ├─ Construct email with verification link
    └─ Send email asynchronously
    │
    ▼
User receives email with link
    │
    ▼
GET /registrationConfirm?token=XXX
    │
    ▼
IUserAuthService.validateVerificationToken()
    ├─ Find token in database
    ├─ Check expiry (60 minutes)
    ├─ If valid: set User.verified=true, delete token
    └─ Return result
    │
    ▼
User account activated
```

#### Password Reset Flow

```
User Forgot Password
    │
    ▼
POST /resetPassword?email=user@example.com
    │
    ▼
IUserAuthService.findUserByEmail()
    ├─ If not found: throw UserNotFoundException
    └─ If found: continue
    │
    ▼
IUserAuthService.createPasswordResetTokenForUser()
    ├─ Generate PasswordResetToken
    ├─ Associate with User
    ├─ Save to database
    └─ Return token
    │
    ▼
Email Service sends reset email
    ├─ Construct email with reset link (includes user ID and token)
    └─ Send asynchronously
    │
    ▼
User receives email and clicks link
    │
    ▼
GET /changePassword?id=123&token=XXX
    │
    ▼
ISecurityUserService.validatePasswordResetToken()
    ├─ Find token by user ID
    ├─ Check expiry
    ├─ If valid: return null (allow form display)
    └─ If invalid: return error message
    │
    ▼
POST /savePassword (PasswordDto with new password)
    │
    ▼
IUserAuthService.changeUserPassword()
    ├─ Hash new password
    ├─ Update User.password
    ├─ Delete PasswordResetToken
    └─ Save to database
    │
    ▼
Email Service sends confirmation email
    │
    ▼
Password reset complete
```

#### Authenticated Password Update Flow

```
Authenticated User
    │
    ▼
POST /updatePassword (PasswordDto with old and new)
    │
    ▼
IUserAuthService.checkIfValidOldPassword()
    ├─ Retrieve current User from SecurityContext
    ├─ Hash provided old password
    ├─ Compare with stored hash (constant-time)
    ├─ If mismatch: throw InvalidOldPasswordException
    └─ If match: continue
    │
    ▼
IUserAuthService.changeUserPassword()
    ├─ Hash new password
    ├─ Update User.password
    └─ Save to database
    │
    ▼
Return success message
```

## Components and Interfaces

### RegistrationController

**Responsibility**: HTTP endpoint handling for registration, verification, and password management

**Key Methods**:

```java
// Registration
POST /register
- Input: UserDto (email, password, firstName, lastName)
- Output: GenericResponse("success")
- Publishes: OnRegistrationCompleteEvent
- Exceptions: UserAlreadyExistsException, ValidationException

// Email Verification
GET /registrationConfirm?token=XXX
- Input: token (String)
- Output: GenericResponse(result) where result is "success" or error message
- Calls: IUserAuthService.validateVerificationToken()

// Resend Verification Token
GET /resendRegistrationToken?token=XXX
- Input: existingToken (String)
- Output: GenericResponse(message)
- Calls: IUserAuthService.generateNewVerificationToken()
- Sends: Verification email

// Password Reset Request
POST /resetPassword?email=user@example.com
- Input: email (String)
- Output: GenericResponse(message)
- Calls: IUserAuthService.findUserByEmail(), createPasswordResetTokenForUser()
- Exceptions: UserNotFoundException
- Sends: Password reset email

// Password Reset Token Validation
GET /changePassword?id=123&token=XXX
- Input: id (long), token (String)
- Output: Redirect to form or login
- Calls: ISecurityUserService.validatePasswordResetToken()

// Save New Password After Reset
POST /savePassword
- Input: PasswordDto (newPassword)
- Output: GenericResponse(message)
- Requires: @PreAuthorize("hasRole('READ_PRIVILEGE')")
- Calls: IUserAuthService.changeUserPassword()

// Update Password (Authenticated User)
POST /updatePassword
- Input: PasswordDto (oldPassword, newPassword)
- Output: GenericResponse(message)
- Requires: @PreAuthorize("hasRole('READ_PRIVILEGE')")
- Calls: IUserAuthService.checkIfValidOldPassword(), changeUserPassword()
- Exceptions: InvalidOldPasswordException
```

### IUserAuthService Interface

**Responsibility**: Business logic for user authentication, registration, and password management

**Key Methods**:

```java
// Registration
User registerNewUserAccount(UserDto accountDto)
- Creates new User with verified=false
- Hashes password
- Saves to database
- Returns User entity

// Email Verification
String validateVerificationToken(String token)
- Finds VerificationToken by token string
- Checks expiry (60 minutes)
- If valid: sets User.verified=true, deletes token, returns "success"
- If expired: returns "expired"
- If invalid: returns "invalid"

// Verification Token Management
VerificationToken generateNewVerificationToken(String existingToken)
- Finds User by existing token
- Invalidates old token
- Creates new VerificationToken with 60-minute expiry
- Saves to database
- Returns new token

User getUser(String token)
- Finds User associated with VerificationToken
- Returns User entity

// Password Reset
User findUserByEmail(String email)
- Queries database for User by email
- Returns User or null

void createPasswordResetTokenForUser(User user, String token)
- Creates PasswordResetToken
- Associates with User
- Sets expiry time
- Saves to database

// Password Management
void changeUserPassword(User user, String newPassword)
- Hashes new password
- Updates User.password
- Saves to database
- Deletes associated PasswordResetToken if exists

boolean checkIfValidOldPassword(User user, String oldPassword)
- Retrieves stored password hash
- Hashes provided oldPassword
- Performs constant-time comparison
- Returns true if match, false otherwise
```

### ISecurityUserService Interface

**Responsibility**: Security-specific operations including password reset token validation

**Key Methods**:

```java
String validatePasswordResetToken(long userId, String token)
- Finds PasswordResetToken by userId and token
- Checks expiry
- If valid: returns null (allow operation)
- If expired: returns "expired"
- If invalid: returns "invalid"
```

### Email Service Components

**Responsibility**: Constructing and sending verification and password reset emails

**Key Methods**:

```java
SimpleMailMessage constructResendVerificationTokenEmail(
    String contextPath, 
    Locale locale, 
    VerificationToken token, 
    User user)
- Constructs verification link: {contextPath}/registrationConfirm.html?token={token}
- Retrieves localized message
- Creates SimpleMailMessage with subject, body, recipient
- Returns email object

SimpleMailMessage constructResetTokenEmail(
    String contextPath, 
    Locale locale, 
    String token, 
    User user)
- Constructs reset link: {contextPath}/user/changePassword?id={userId}&token={token}
- Retrieves localized message
- Creates SimpleMailMessage with subject, body, recipient
- Returns email object

SimpleMailMessage constructEmail(
    String subject, 
    String body, 
    User user)
- Creates SimpleMailMessage
- Sets subject, body, recipient email
- Sets from address from environment property "support.email"
- Returns email object
```

## Data Models

### User Entity

```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String password;  // Hashed password
    
    @Column(nullable = false)
    private String firstName;
    
    @Column(nullable = false)
    private String lastName;
    
    @Column(nullable = false)
    private boolean verified = false;  // Email verification status
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    // Getters and setters
}
```

### VerificationToken Entity

```java
@Entity
@Table(name = "verification_tokens")
public class VerificationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String token;
    
    @ManyToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;
    
    @Column(nullable = false)
    private LocalDateTime expiryDate;  // 60 minutes from creation
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    // Getters and setters
}
```

### PasswordResetToken Entity

```java
@Entity
@Table(name = "password_reset_tokens")
public class PasswordResetToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String token;
    
    @ManyToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;
    
    @Column(nullable = false)
    private LocalDateTime expiryDate;  // Configurable expiry (typically 24 hours)
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    // Getters and setters
}
```

### DTOs

```java
public class UserDto {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    // Getters and setters
}

public class PasswordDto {
    private String oldPassword;      // For authenticated password update
    private String newPassword;
    // Getters and setters
}

public class GenericResponse {
    private String message;
    
    public GenericResponse(String message) {
        this.message = message;
    }
    // Getters and setters
}
```

## Token Generation and Validation Logic

### Token Generation

```java
// VerificationToken generation
String token = UUID.randomUUID().toString();
VerificationToken verificationToken = new VerificationToken();
verificationToken.setToken(token);
verificationToken.setUser(user);
verificationToken.setExpiryDate(LocalDateTime.now().plusMinutes(60));
verificationToken.setCreatedAt(LocalDateTime.now());
repository.save(verificationToken);
```

### Token Validation

```java
// Expiry check
public boolean isTokenExpired(VerificationToken token) {
    return LocalDateTime.now().isAfter(token.getExpiryDate());
}

// Token lookup and validation
public String validateVerificationToken(String tokenString) {
    VerificationToken token = repository.findByToken(tokenString);
    
    if (token == null) {
        return "invalid";
    }
    
    if (isTokenExpired(token)) {
        repository.delete(token);
        return "expired";
    }
    
    User user = token.getUser();
    user.setVerified(true);
    userRepository.save(user);
    repository.delete(token);
    
    return "success";
}
```

## Password Hashing and Secure Comparison

### Password Hashing

```java
// Using Spring Security's PasswordEncoder (BCrypt)
@Autowired
private PasswordEncoder passwordEncoder;

public void changeUserPassword(User user, String newPassword) {
    String hashedPassword = passwordEncoder.encode(newPassword);
    user.setPassword(hashedPassword);
    userRepository.save(user);
}
```

### Secure Password Comparison

```java
// Using PasswordEncoder's matches() method (constant-time comparison)
public boolean checkIfValidOldPassword(User user, String oldPassword) {
    return passwordEncoder.matches(oldPassword, user.getPassword());
}
```

The `PasswordEncoder.matches()` method performs constant-time comparison to prevent timing attacks.

## Error Handling

### Custom Exceptions

```java
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {
        super("User not found");
    }
}

public class InvalidOldPasswordException extends RuntimeException {
    public InvalidOldPasswordException() {
        super("Invalid old password");
    }
}

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String email) {
        super("User with email " + email + " already exists");
    }
}

public class TokenExpiredException extends RuntimeException {
    public TokenExpiredException() {
        super("Token has expired");
    }
}

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException() {
        super("Invalid token");
    }
}
```

### Exception Handling in Controller

```java
@ExceptionHandler(UserNotFoundException.class)
public ResponseEntity<GenericResponse> handleUserNotFound(UserNotFoundException e) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new GenericResponse(e.getMessage()));
}

@ExceptionHandler(InvalidOldPasswordException.class)
public ResponseEntity<GenericResponse> handleInvalidPassword(InvalidOldPasswordException e) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new GenericResponse(e.getMessage()));
}

@ExceptionHandler(UserAlreadyExistsException.class)
public ResponseEntity<GenericResponse> handleUserExists(UserAlreadyExistsException e) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(new GenericResponse(e.getMessage()));
}
```

## Email Delivery Strategy

### Asynchronous Email Sending

To prevent blocking the main request thread, email sending should be asynchronous:

```java
@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;
    
    @Async
    public void sendEmail(SimpleMailMessage email) {
        try {
            mailSender.send(email);
        } catch (MailException e) {
            LOGGER.error("Failed to send email to {}", email.getTo(), e);
            // Implement retry logic or alerting as needed
        }
    }
}
```

### Event-Driven Email Sending

```java
@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {
    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {
        User user = event.getUser();
        String token = generateVerificationToken(user);
        SimpleMailMessage email = constructVerificationEmail(
            event.getAppUrl(), 
            event.getLocale(), 
            token, 
            user
        );
        emailService.sendEmail(email);
    }
}
```

## Correctness Properties

*A property is a characteristic or behavior that should hold true across all valid executions of a system—essentially, a formal statement about what the system should do. Properties serve as the bridge between human-readable specifications and machine-verifiable correctness guarantees.*

### Property 1: Registration creates unverified user

For any valid UserDto, registering a new user account should result in a User entity with verified=false.

**Validates: Requirements 1.1**

### Property 2: Verification token round-trip

For any newly registered user, generating a verification token, then validating it with the correct token string should set the user's verified field to true and return success.

**Validates: Requirements 1.3, 8.1, 8.2, 8.4**

### Property 3: Expired verification tokens are rejected

For any verification token with an expiry time in the past, attempting to validate it should return an expiry error and not modify the user's verified status.

**Validates: Requirements 1.4, 8.3, 9.2, 9.3**

### Property 4: New verification token invalidates old token

For any user with an existing verification token, generating a new verification token should result in the old token no longer being valid for verification.

**Validates: Requirements 2.1, 2.4**

### Property 5: Verification tokens expire after 60 minutes

For any newly generated verification token, the expiry date should be set to exactly 60 minutes from the creation timestamp.

**Validates: Requirements 2.3, 9.1, 9.4**

### Property 6: Unverified users cannot access protected resources

For any user with verified=false, attempting to access a protected resource should result in access denial.

**Validates: Requirements 1.5**

### Property 7: Password reset token creation and association

For any valid email address in the system, requesting a password reset should create a PasswordResetToken associated with the correct user.

**Validates: Requirements 3.1, 3.3**

### Property 8: Password reset token validation

For any valid PasswordResetToken, validating it with the correct user ID and token string should return success and allow password change operations.

**Validates: Requirements 4.1, 4.3, 4.4**

### Property 9: Expired password reset tokens are rejected

For any password reset token with an expiry time in the past, attempting to validate it should return an expiry error and prevent password change operations.

**Validates: Requirements 4.2, 9.2, 9.3**

### Property 10: Password reset tokens have defined expiry

For any newly created PasswordResetToken, the expiry date should be set to a future time from the creation timestamp.

**Validates: Requirements 3.5, 9.1, 9.4**

### Property 11: Password update with correct old password succeeds

For any authenticated user with a known password, providing the correct old password and a new password should successfully update the user's password.

**Validates: Requirements 6.1, 6.3, 5.1**

### Property 12: Password update with incorrect old password fails

For any authenticated user, providing an incorrect old password should throw InvalidOldPasswordException and not modify the user's password.

**Validates: Requirements 6.2, 7.1**

### Property 13: Passwords are hashed before storage

For any password update operation (registration, reset, or authenticated update), the stored password should not be plaintext and should be verifiable using secure comparison.

**Validates: Requirements 5.2, 6.4, 7.2, 7.4**

### Property 14: Constant-time password comparison

For any password comparison operation, the comparison should use constant-time comparison to prevent timing attacks, regardless of whether the password matches.

**Validates: Requirements 7.1, 7.3**

### Property 15: Verification emails contain token and link

For any verification email sent during registration or token resend, the email body should contain both the application URL and the verification token.

**Validates: Requirements 1.2, 2.2, 10.1, 10.3**

### Property 16: Password reset emails contain token and user ID

For any password reset email sent, the email body should contain both the password reset token and the user ID in the reset link.

**Validates: Requirements 3.4, 10.1, 10.3**

### Property 17: Emails use configured sender address

For any email sent by the Email_Service, the from address should match the configured support email address from the environment properties.

**Validates: Requirements 10.2**

### Property 18: Unverified users are denied access

For any unverified user attempting to access protected resources, the system should deny access until the user's verified field is set to true.

**Validates: Requirements 1.5**

## Testing Strategy

### Unit Testing Approach

Unit tests will verify specific examples, edge cases, and error conditions:

- **Registration examples**: Valid registration with various UserDto inputs, duplicate email handling
- **Token validation edge cases**: Expired tokens, invalid tokens, missing tokens
- **Password operations**: Correct/incorrect old password, password hashing verification
- **Email construction**: Verify email content includes required fields and links
- **Error handling**: UserNotFoundException, InvalidOldPasswordException, token expiry scenarios

### Property-Based Testing Approach

Property-based tests will verify universal properties across randomized inputs using a PBT library (e.g., QuickCheck for Java, or Hypothesis-style testing):

**Configuration**:
- Minimum 100 iterations per property test
- Each test references its corresponding design property
- Tag format: `Feature: email-verification-password-management, Property {number}: {property_text}`

**Property Test Examples**:

```java
// Property 1: Registration creates unverified user
@Property
@Tag("Feature: email-verification-password-management, Property 1: Registration creates unverified user")
void registrationCreatesUnverifiedUser(@ForAll UserDto userDto) {
    User registered = userService.registerNewUserAccount(userDto);
    assertThat(registered.isVerified()).isFalse();
}

// Property 2: Verification token round-trip
@Property
@Tag("Feature: email-verification-password-management, Property 2: Verification token round-trip")
void verificationTokenRoundTrip(@ForAll User user) {
    VerificationToken token = userService.generateNewVerificationToken(user);
    String result = userService.validateVerificationToken(token.getToken());
    assertThat(result).isEqualTo("success");
    assertThat(user.isVerified()).isTrue();
}

// Property 3: Expired verification tokens are rejected
@Property
@Tag("Feature: email-verification-password-management, Property 3: Expired verification tokens are rejected")
void expiredVerificationTokensRejected(@ForAll VerificationToken token) {
    token.setExpiryDate(LocalDateTime.now().minusMinutes(1));
    String result = userService.validateVerificationToken(token.getToken());
    assertThat(result).isEqualTo("expired");
}

// Property 11: Password update with correct old password succeeds
@Property
@Tag("Feature: email-verification-password-management, Property 11: Password update with correct old password succeeds")
void passwordUpdateWithCorrectOldPasswordSucceeds(
    @ForAll User user, 
    @ForAll String newPassword) {
    String oldPassword = user.getPassword();
    boolean isValid = userService.checkIfValidOldPassword(user, oldPassword);
    assertThat(isValid).isTrue();
    userService.changeUserPassword(user, newPassword);
    assertThat(userService.checkIfValidOldPassword(user, newPassword)).isTrue();
}

// Property 13: Passwords are hashed before storage
@Property
@Tag("Feature: email-verification-password-management, Property 13: Passwords are hashed before storage")
void passwordsAreHashedBeforeStorage(@ForAll String password) {
    User user = new User();
    userService.changeUserPassword(user, password);
    assertThat(user.getPassword()).isNotEqualTo(password);
    assertThat(passwordEncoder.matches(password, user.getPassword())).isTrue();
}
```

### Dual Testing Coverage

- **Unit tests** handle specific examples and edge cases (e.g., expired tokens, invalid emails)
- **Property tests** verify universal correctness across all valid inputs (e.g., all registrations create unverified users)
- Together, they provide comprehensive coverage of both concrete bugs and general correctness

### Test Organization

- Create `RegistrationServicePropertyTests` for registration and verification properties
- Create `PasswordManagementPropertyTests` for password operation properties
- Create `TokenValidationPropertyTests` for token expiry and validation properties
- Create `EmailServicePropertyTests` for email construction properties
- Each test class focuses on a specific component or workflow

