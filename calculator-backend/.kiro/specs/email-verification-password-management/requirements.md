# Requirements Document: Email Verification and Password Management

## Introduction

This feature enhances user account security and management by implementing a complete email verification workflow during registration and providing comprehensive password management capabilities. Users will receive verification emails to confirm their identity, and authenticated users can manage their passwords through reset and update flows. The system ensures token expiry validation, secure password handling, and proper state management throughout the user lifecycle.

## Glossary

- **User**: An entity representing a registered account with email, password, and verification status
- **VerificationToken**: A time-limited token (60 minutes) sent to users for email confirmation during registration
- **PasswordResetToken**: A time-limited token used to validate password reset requests
- **verified**: A boolean field on the User entity indicating whether the user has confirmed their email address
- **Registration_System**: The component responsible for user account creation and email verification
- **Password_Manager**: The component responsible for password reset, update, and validation operations
- **Email_Service**: The component responsible for sending verification and password reset emails
- **Token_Validator**: The component responsible for validating token expiry and authenticity

## Requirements

### Requirement 1: User Registration with Email Verification

**User Story:** As a new user, I want to register an account and receive a verification email, so that I can confirm my identity and activate my account.

#### Acceptance Criteria

1. WHEN a user POSTs valid account details to the /register endpoint, THE Registration_System SHALL create a new User record with verified=false
2. WHEN a new User is created, THE Email_Service SHALL send a verification email containing a unique VerificationToken link
3. WHEN the user clicks the verification link with a valid token, THE Registration_System SHALL validate the token and set verified=true
4. IF the VerificationToken has expired (older than 60 minutes), THEN THE Token_Validator SHALL return an expiry error
5. WHEN a user attempts to access protected resources before verification, THE Registration_System SHALL deny access until verified=true

### Requirement 2: Resend Verification Email

**User Story:** As a user who did not receive the verification email, I want to request a new verification token, so that I can complete the registration process.

#### Acceptance Criteria

1. WHEN a user requests a new verification token via /resendRegistrationToken with an existing token, THE Registration_System SHALL generate a new VerificationToken
2. WHEN a new VerificationToken is generated, THE Email_Service SHALL send a new verification email with the updated token
3. THE new VerificationToken SHALL have a 60-minute expiry from generation time
4. WHEN a new token is generated, THE Registration_System SHALL invalidate the previous token

### Requirement 3: Password Reset Request

**User Story:** As a user who forgot their password, I want to request a password reset, so that I can regain access to my account.

#### Acceptance Criteria

1. WHEN a user POSTs their email to /resetPassword, THE Password_Manager SHALL verify the email exists in the system
2. IF the email does not exist, THEN THE Password_Manager SHALL return a UserNotFoundException
3. WHEN a valid email is provided, THE Password_Manager SHALL create a PasswordResetToken and associate it with the user
4. WHEN a PasswordResetToken is created, THE Email_Service SHALL send a password reset email containing the token and user ID
5. THE PasswordResetToken SHALL have a defined expiry time for security

### Requirement 4: Password Reset Token Validation

**User Story:** As a user resetting my password, I want to validate my reset token before changing my password, so that I can ensure the request is legitimate.

#### Acceptance Criteria

1. WHEN a user accesses /changePassword with a user ID and token, THE Token_Validator SHALL validate the token against the user ID
2. IF the token is invalid or expired, THEN THE Token_Validator SHALL return an error and redirect to login
3. IF the token is valid, THEN THE system SHALL allow the user to proceed to the password change form
4. THE validation SHALL check both token authenticity and expiry time

### Requirement 5: Save New Password After Reset

**User Story:** As a user with a valid password reset token, I want to save my new password, so that I can regain access to my account.

#### Acceptance Criteria

1. WHEN an authenticated user POSTs a new password to /savePassword, THE Password_Manager SHALL update the user's password
2. THE Password_Manager SHALL hash the new password before storing it
3. WHEN the password is successfully updated, THE Email_Service SHALL send a confirmation email to the user
4. WHEN the password is successfully updated, THE system SHALL return a success message

### Requirement 6: Authenticated User Password Update

**User Story:** As an authenticated user, I want to update my password by providing my current password, so that I can change my password while logged in.

#### Acceptance Criteria

1. WHEN an authenticated user POSTs to /updatePassword with old and new passwords, THE Password_Manager SHALL verify the old password matches the user's current password
2. IF the old password is incorrect, THEN THE Password_Manager SHALL throw InvalidOldPasswordException
3. IF the old password is correct, THE Password_Manager SHALL update the user's password to the new value
4. THE Password_Manager SHALL hash the new password before storing it
5. WHEN the password is successfully updated, THE system SHALL return a success message

### Requirement 7: Password Validation

**User Story:** As the system, I want to validate passwords during updates, so that I can ensure password security and consistency.

#### Acceptance Criteria

1. THE Password_Manager SHALL verify that old passwords match the stored hashed password using secure comparison
2. THE Password_Manager SHALL accept new passwords that meet security requirements
3. WHEN comparing passwords, THE Password_Manager SHALL use constant-time comparison to prevent timing attacks
4. THE Password_Manager SHALL not store plaintext passwords

### Requirement 8: Email Verification Endpoint

**User Story:** As a user with a verification token, I want to confirm my email address, so that I can complete the registration process.

#### Acceptance Criteria

1. WHEN a user accesses /registrationConfirm with a valid token, THE Registration_System SHALL validate the token
2. WHEN the token is valid, THE Registration_System SHALL set the user's verified field to true
3. IF the token is invalid or expired, THEN THE Registration_System SHALL return a validation error
4. WHEN verification is complete, THE system SHALL return a success response

### Requirement 9: Token Expiry Management

**User Story:** As the system, I want to enforce token expiry, so that I can ensure security and prevent unauthorized access.

#### Acceptance Criteria

1. THE Token_Validator SHALL check token creation timestamp against current time
2. IF a token is older than its defined expiry period, THEN THE Token_Validator SHALL mark it as expired
3. WHEN a token is expired, THE system SHALL reject any operations using that token
4. THE expiry validation SHALL apply to both VerificationToken (60 minutes) and PasswordResetToken

### Requirement 10: Secure Email Communication

**User Story:** As a user, I want to receive emails with secure verification links, so that I can trust the email communications.

#### Acceptance Criteria

1. WHEN an email is sent, THE Email_Service SHALL include the application URL and token in the verification link
2. THE Email_Service SHALL use the configured support email address as the sender
3. WHEN constructing verification links, THE Email_Service SHALL include all required parameters (token, user ID)
4. THE Email_Service SHALL send emails asynchronously to avoid blocking the registration process

