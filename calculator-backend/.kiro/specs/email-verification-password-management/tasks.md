# Implementation Plan: Email Verification and Password Management

## Overview

This implementation plan breaks down the email verification and password management feature into discrete, sequential coding tasks. The feature includes user registration with email verification, password reset workflows, and authenticated password updates. Each task builds on previous work, with property-based tests validating correctness properties and unit tests covering edge cases.

## Tasks

- [x] 1. Set up project structure and core interfaces
  - Create or verify User entity with verified field
  - Create VerificationToken entity with token and expiryDate fields
  - Create PasswordResetToken entity with token and expiryDate fields
  - Create UserDto, PasswordDto, and GenericResponse DTOs
  - Create custom exception classes (UserNotFoundException, InvalidOldPasswordException, UserAlreadyExistsException)
  - _Requirements: 1.1, 3.1, 5.1, 6.1, 7.1_

- [x] 2. Implement IUserAuthService interface and core registration logic
  - [x] 2.1 Define IUserAuthService interface with all required methods
    - registerNewUserAccount(), validateVerificationToken(), generateNewVerificationToken()
    - getUser(), findUserByEmail(), createPasswordResetTokenForUser()
    - changeUserPassword(), checkIfValidOldPassword()
    - _Requirements: 1.1, 2.1, 3.1, 5.1, 6.1_

  - [x] 2.2 Implement UserAuthService.registerNewUserAccount()
    - Validate UserDto input
    - Check for duplicate email and throw UserAlreadyExistsException if exists
    - Hash password using PasswordEncoder
    - Create User entity with verified=false
    - Save to database and return User
    - _Requirements: 1.1_

  - [x] 2.3 Write property test for registration creates unverified user
    - **Property 1: Registration creates unverified user**
    - **Validates: Requirements 1.1**

  - [x] 2.4 Implement UserAuthService.validateVerificationToken()
    - Find VerificationToken by token string
    - Check if token exists; return "invalid" if not
    - Check token expiry using LocalDateTime comparison
    - If expired: delete token and return "expired"
    - If valid: set User.verified=true, delete token, return "success"
    - _Requirements: 1.3, 8.1, 8.2, 8.3, 8.4_

  - [x] 2.5 Write property test for verification token round-trip
    - **Property 2: Verification token round-trip**
    - **Validates: Requirements 1.3, 8.1, 8.2, 8.4**

  - [x] 2.6 Write property test for expired verification tokens are rejected
    - **Property 3: Expired verification tokens are rejected**
    - **Validates: Requirements 1.4, 8.3, 9.2, 9.3**

  - [x] 2.7 Implement UserAuthService.generateNewVerificationToken()
    - Find User by existing token
    - Invalidate old token by deleting it
    - Generate new UUID token
    - Create new VerificationToken with 60-minute expiry
    - Save to database and return token
    - _Requirements: 2.1, 2.3, 2.4_

  - [x] 2.8 Write property test for new verification token invalidates old token
    - **Property 4: New verification token invalidates old token**
    - **Validates: Requirements 2.1, 2.4**

  - [x] 2.9 Write property test for verification tokens expire after 60 minutes
    - **Property 5: Verification tokens expire after 60 minutes**
    - **Validates: Requirements 2.3, 9.1, 9.4**

  - [x] 2.10 Implement UserAuthService.getUser()
    - Find User associated with VerificationToken
    - Return User entity
    - _Requirements: 2.2_

- [x] 3. Implement password reset service methods
  - [x] 3.1 Implement UserAuthService.findUserByEmail()
    - Query database for User by email
    - Return User or null
    - _Requirements: 3.1_

  - [x] 3.2 Implement UserAuthService.createPasswordResetTokenForUser()
    - Create PasswordResetToken with provided token
    - Associate with User
    - Set expiry time (configurable, typically 24 hours)
    - Save to database
    - _Requirements: 3.3, 3.5_

  - [ ]* 3.3 Write property test for password reset token creation and association
    - **Property 7: Password reset token creation and association**
    - **Validates: Requirements 3.1, 3.3**

  - [ ]* 3.4 Write property test for password reset tokens have defined expiry
    - **Property 10: Password reset tokens have defined expiry**
    - **Validates: Requirements 3.5, 9.1, 9.4**

- [x] 4. Implement ISecurityUserService interface and token validation
  - [x] 4.1 Define ISecurityUserService interface with validatePasswordResetToken()
    - _Requirements: 4.1, 4.2, 4.3, 4.4_

  - [x] 4.2 Implement SecurityUserService.validatePasswordResetToken()
    - Find PasswordResetToken by userId and token
    - Check if token exists; return "invalid" if not
    - Check token expiry
    - If expired: return "expired"
    - If valid: return null (allow operation)
    - _Requirements: 4.1, 4.2, 4.3, 4.4_

  - [x] 4.3 Write property test for password reset token validation
    - **Property 8: Password reset token validation**
    - **Validates: Requirements 4.1, 4.3, 4.4**

  - [x] 4.4 Write property test for expired password reset tokens are rejected
    - **Property 9: Expired password reset tokens are rejected**
    - **Validates: Requirements 4.2, 9.2, 9.3**

- [ ] 5. Implement password management service methods
  - [ ] 5.1 Implement UserAuthService.changeUserPassword()
    - Hash new password using PasswordEncoder
    - Update User.password field
    - Delete associated PasswordResetToken if exists
    - Save User to database
    - _Requirements: 5.1, 5.2, 6.3, 6.4_

  - [ ]* 5.2 Write property test for password update with correct old password succeeds
    - **Property 11: Password update with correct old password succeeds**
    - **Validates: Requirements 6.1, 6.3, 5.1**

  - [ ] 5.3 Implement UserAuthService.checkIfValidOldPassword()
    - Retrieve current User's password hash
    - Use PasswordEncoder.matches() for constant-time comparison
    - Return true if match, false otherwise
    - _Requirements: 6.1, 7.1, 7.3_

  - [ ]* 5.4 Write property test for password update with incorrect old password fails
    - **Property 12: Password update with incorrect old password fails**
    - **Validates: Requirements 6.2, 7.1**

  - [ ]* 5.5 Write property test for passwords are hashed before storage
    - **Property 13: Passwords are hashed before storage**
    - **Validates: Requirements 5.2, 6.4, 7.2, 7.4**

  - [ ]* 5.6 Write property test for constant-time password comparison
    - **Property 14: Constant-time password comparison**
    - **Validates: Requirements 7.1, 7.3**

- [ ] 6. Implement email service methods
  - [ ] 6.1 Create EmailService class with email construction methods
    - Implement constructResendVerificationTokenEmail()
    - Implement constructResetTokenEmail()
    - Implement constructEmail()
    - _Requirements: 1.2, 2.2, 3.4, 10.1, 10.2, 10.3_

  - [ ]* 6.2 Write property test for verification emails contain token and link
    - **Property 15: Verification emails contain token and link**
    - **Validates: Requirements 1.2, 2.2, 10.1, 10.3**

  - [ ]* 6.3 Write property test for password reset emails contain token and user ID
    - **Property 16: Password reset emails contain token and user ID**
    - **Validates: Requirements 3.4, 10.1, 10.3**

  - [ ]* 6.4 Write property test for emails use configured sender address
    - **Property 17: Emails use configured sender address**
    - **Validates: Requirements 10.2**

  - [ ] 6.5 Implement asynchronous email sending with @Async annotation
    - Create sendEmail() method with @Async annotation
    - Add error handling and logging for failed sends
    - _Requirements: 10.4_

  - [ ] 6.6 Create RegistrationListener to handle OnRegistrationCompleteEvent
    - Listen for OnRegistrationCompleteEvent
    - Generate VerificationToken
    - Construct verification email
    - Send email asynchronously
    - _Requirements: 1.2_

- [ ] 7. Verify and enhance RegistrationController endpoints
  - [ ] 7.1 Verify POST /register endpoint
    - Ensure it calls userService.registerNewUserAccount()
    - Ensure it publishes OnRegistrationCompleteEvent
    - Ensure it returns GenericResponse("success")
    - _Requirements: 1.1_

  - [ ] 7.2 Verify GET /registrationConfirm endpoint
    - Ensure it calls userService.validateVerificationToken()
    - Ensure it returns GenericResponse with result
    - _Requirements: 1.3, 8.1, 8.2, 8.3, 8.4_

  - [ ] 7.3 Verify GET /resendRegistrationToken endpoint
    - Ensure it calls userService.generateNewVerificationToken()
    - Ensure it sends verification email
    - Ensure it returns GenericResponse with message
    - _Requirements: 2.1, 2.2, 2.3, 2.4_

  - [ ] 7.4 Verify POST /resetPassword endpoint
    - Ensure it calls userService.findUserByEmail()
    - Ensure it throws UserNotFoundException if email not found
    - Ensure it calls userService.createPasswordResetTokenForUser()
    - Ensure it sends password reset email
    - Ensure it returns GenericResponse with message
    - _Requirements: 3.1, 3.2, 3.3, 3.4, 3.5_

  - [ ] 7.5 Verify GET /changePassword endpoint
    - Ensure it calls securityUserService.validatePasswordResetToken()
    - Ensure it redirects to login if token invalid/expired
    - Ensure it redirects to password update form if token valid
    - _Requirements: 4.1, 4.2, 4.3, 4.4_

  - [ ] 7.6 Verify POST /savePassword endpoint
    - Ensure @PreAuthorize("hasRole('READ_PRIVILEGE')") is present
    - Ensure it calls userService.changeUserPassword()
    - Ensure it returns GenericResponse with success message
    - _Requirements: 5.1, 5.2_

  - [ ] 7.7 Verify POST /updatePassword endpoint
    - Ensure @PreAuthorize("hasRole('READ_PRIVILEGE')") is present
    - Ensure it calls userService.checkIfValidOldPassword()
    - Ensure it throws InvalidOldPasswordException if old password incorrect
    - Ensure it calls userService.changeUserPassword()
    - Ensure it returns GenericResponse with success message
    - _Requirements: 6.1, 6.2, 6.3, 6.4_

- [ ] 8. Create repository interfaces and query methods
  - [ ] 8.1 Create VerificationTokenRepository interface
    - Add findByToken(String token) method
    - Add findByUser(User user) method
    - _Requirements: 1.3, 2.1_

  - [ ] 8.2 Create PasswordResetTokenRepository interface
    - Add findByToken(String token) method
    - Add findByUserIdAndToken(Long userId, String token) method
    - Add findByUser(User user) method
    - _Requirements: 4.1, 5.1_

  - [ ] 8.3 Verify UserRepository has findByEmail(String email) method
    - _Requirements: 3.1, 6.1_

- [ ] 9. Implement Spring Security configuration for protected resources
  - [ ] 9.1 Configure Spring Security to deny unverified users access to protected resources
    - Add security filter or interceptor to check User.verified field
    - Ensure unverified users cannot access endpoints with @PreAuthorize("hasRole('READ_PRIVILEGE')")
    - _Requirements: 1.5_

  - [ ]* 9.2 Write property test for unverified users cannot access protected resources
    - **Property 6: Unverified users cannot access protected resources**
    - **Validates: Requirements 1.5**

  - [ ]* 9.3 Write property test for unverified users are denied access
    - **Property 18: Unverified users are denied access**
    - **Validates: Requirements 1.5**

- [ ] 10. Add configuration properties
  - [ ] 10.1 Add support.email property to application.properties
    - Set sender email address for verification and password reset emails
    - _Requirements: 10.2_

  - [ ] 10.2 Add password reset token expiry configuration
    - Add property for PasswordResetToken expiry time (e.g., 24 hours)
    - Use in createPasswordResetTokenForUser()
    - _Requirements: 3.5, 9.1, 9.4_

- [ ] 11. Checkpoint - Ensure all tests pass
  - Ensure all unit tests pass
  - Ensure all property-based tests pass
  - Ensure no compilation errors
  - Ask the user if questions arise

- [ ] 12. Write integration tests for complete workflows
  - [ ] 12.1 Write integration test for registration and email verification workflow
    - Register new user
    - Verify user is created with verified=false
    - Verify verification email is sent
    - Validate verification token
    - Verify user is marked as verified
    - _Requirements: 1.1, 1.2, 1.3, 8.1, 8.2, 8.4_

  - [ ] 12.2 Write integration test for resend verification token workflow
    - Register new user
    - Request new verification token
    - Verify old token is invalidated
    - Verify new token is sent via email
    - Validate new token successfully
    - _Requirements: 2.1, 2.2, 2.3, 2.4_

  - [ ] 12.3 Write integration test for password reset workflow
    - Request password reset with valid email
    - Verify password reset email is sent
    - Validate password reset token
    - Save new password
    - Verify user can login with new password
    - _Requirements: 3.1, 3.2, 3.3, 3.4, 3.5, 4.1, 4.3, 4.4, 5.1, 5.2_

  - [ ] 12.4 Write integration test for authenticated password update workflow
    - Authenticate user
    - Update password with correct old password
    - Verify password is updated
    - Verify user can login with new password
    - _Requirements: 6.1, 6.3, 6.4, 7.1, 7.3_

  - [ ] 12.5 Write integration test for invalid old password rejection
    - Authenticate user
    - Attempt to update password with incorrect old password
    - Verify InvalidOldPasswordException is thrown
    - Verify password is not updated
    - _Requirements: 6.2, 7.1_

  - [ ] 12.6 Write integration test for expired token rejection
    - Create verification token with past expiry date
    - Attempt to validate token
    - Verify "expired" result is returned
    - Verify user is not marked as verified
    - _Requirements: 1.4, 9.2, 9.3_

- [ ] 13. Final checkpoint - Ensure all tests pass
  - Ensure all unit tests pass
  - Ensure all property-based tests pass
  - Ensure all integration tests pass
  - Ensure no compilation errors
  - Ask the user if questions arise

## Notes

- Tasks marked with `*` are optional and can be skipped for faster MVP
- Each task references specific requirements for traceability
- Property-based tests validate universal correctness properties across randomized inputs
- Unit tests validate specific examples and edge cases
- Integration tests verify complete workflows end-to-end
- All password operations use Spring Security's PasswordEncoder for secure hashing and constant-time comparison
- Email sending is asynchronous to prevent blocking the main request thread
- Token expiry is validated using LocalDateTime comparison
