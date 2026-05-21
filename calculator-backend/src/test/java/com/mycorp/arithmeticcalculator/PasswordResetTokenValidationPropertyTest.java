package com.mycorp.arithmeticcalculator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.Calendar;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.mycorp.arithmeticcalculator.domain.PasswordResetToken;
import com.mycorp.arithmeticcalculator.domain.User;
import com.mycorp.arithmeticcalculator.repository.PasswordResetTokenRepository;
import com.mycorp.arithmeticcalculator.security.UserSecurityService;

/**
 * Property-based test for password reset token validation.
 * 
 * **Validates: Requirements 4.1, 4.3, 4.4**
 * 
 * Feature: email-verification-password-management, Property 8: Password reset token validation
 * 
 * This test class validates that password reset token validation works correctly across
 * multiple scenarios using mocked dependencies.
 */
public class PasswordResetTokenValidationPropertyTest {

    private UserSecurityService userSecurityService;
    
    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;
    
    @Mock
    private UserDetailsService userDetailsService;
    
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        userSecurityService = new UserSecurityService();
        // Inject mocked dependencies using reflection
        setField(userSecurityService, "passwordTokenRepository", passwordResetTokenRepository);
        setField(userSecurityService, "userDetailsService", userDetailsService);
    }
    
    /**
     * Property 8: Password reset token validation
     * 
     * Test 1: Valid password reset token should return success (null)
     * 
     * For any valid PasswordResetToken, validating it with the correct user ID and token string
     * should return success (null) and allow password change operations.
     */
    @Test
    public void validPasswordResetTokenShouldReturnSuccess() {
        // Arrange: Create a user
        User testUser = createTestUser(1L, "John", "Doe", "john.doe@test.com");
        
        // Create a valid password reset token (not expired)
        String tokenString = UUID.randomUUID().toString();
        PasswordResetToken testToken = new PasswordResetToken(tokenString, testUser);
        
        // Set expiry date to future (valid token)
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, 1); // 1 hour in the future
        testToken.setExpiryDate(cal.getTime());
        
        // Mock repository behavior
        when(passwordResetTokenRepository.findByToken(tokenString)).thenReturn(testToken);
        
        // Mock UserDetailsService to avoid NullPointerException
        org.springframework.security.core.userdetails.User userDetails = 
            new org.springframework.security.core.userdetails.User(
                testUser.getEmail(), 
                testUser.getPassword(), 
                java.util.Collections.emptyList()
            );
        when(userDetailsService.loadUserByUsername(testUser.getEmail())).thenReturn(userDetails);
        
        // Act: Validate the token with correct user ID and token string
        String result = userSecurityService.validatePasswordResetToken(testUser.getId(), tokenString);
        
        // Assert: Valid token should return null (success)
        assertNull("Valid password reset token should return null (success), but got: " + result, result);
    }

    /**
     * Property 8 (Edge Case): Invalid token string should fail validation
     * 
     * Test 2: For any user with a valid token, attempting to validate with a different token string
     * should return "invalidToken".
     */
    @Test
    public void invalidTokenStringShouldFailValidation() {
        // Arrange: Create a user with a token
        User testUser = createTestUser(2L, "Jane", "Smith", "jane.smith@test.com");
        
        String correctToken = UUID.randomUUID().toString();
        String wrongToken = UUID.randomUUID().toString();
        
        // Mock repository to return null for wrong token
        when(passwordResetTokenRepository.findByToken(wrongToken)).thenReturn(null);
        
        // Act: Validate with wrong token string
        String result = userSecurityService.validatePasswordResetToken(testUser.getId(), wrongToken);
        
        // Assert: Should return "invalidToken"
        assertEquals("Validation with wrong token string should return 'invalidToken'", 
            "invalidToken", result);
    }

    /**
     * Property 8 (Edge Case): Wrong user ID should fail validation
     * 
     * Test 3: For any valid token, attempting to validate with a different user ID
     * should return "invalidToken".
     */
    @Test
    public void wrongUserIdShouldFailValidation() {
        // Arrange: Create two users
        User testUser = createTestUser(3L, "Alice", "Johnson", "alice.johnson@test.com");
        User user2 = createTestUser(4L, "Bob", "Williams", "bob.williams@test.com");
        
        // Create token for testUser
        String tokenString = UUID.randomUUID().toString();
        PasswordResetToken testToken = new PasswordResetToken(tokenString, testUser);
        
        // Set expiry date to future
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, 1);
        testToken.setExpiryDate(cal.getTime());
        
        // Mock repository behavior
        when(passwordResetTokenRepository.findByToken(tokenString)).thenReturn(testToken);
        
        // Act: Validate with user2's ID (wrong user)
        String result = userSecurityService.validatePasswordResetToken(user2.getId(), tokenString);
        
        // Assert: Should return "invalidToken"
        assertEquals("Validation with wrong user ID should return 'invalidToken'", 
            "invalidToken", result);
    }

    /**
     * Property 8 (Edge Case): Expired token should fail validation
     * 
     * Test 4: For any expired PasswordResetToken, validation should return "expired".
     */
    @Test
    public void expiredTokenShouldFailValidation() {
        // Arrange: Create a user
        User testUser = createTestUser(5L, "Charlie", "Brown", "charlie.brown@test.com");
        
        // Create an expired token (expiry date in the past)
        String tokenString = UUID.randomUUID().toString();
        PasswordResetToken testToken = new PasswordResetToken(tokenString, testUser);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, -10); // 10 minutes ago
        testToken.setExpiryDate(cal.getTime());
        
        // Mock repository behavior
        when(passwordResetTokenRepository.findByToken(tokenString)).thenReturn(testToken);
        
        // Act: Validate the expired token
        String result = userSecurityService.validatePasswordResetToken(testUser.getId(), tokenString);
        
        // Assert: Should return "expired"
        assertEquals("Expired password reset token should return 'expired'", 
            "expired", result);
    }

    /**
     * Property 8 (Multiple iterations): Test with multiple different users and tokens
     * 
     * Test 5: Simulates property-based testing by running multiple iterations with different data
     */
    @Test
    public void multipleValidTokensShouldAllReturnSuccess() {
        // Test with 10 different users and tokens
        for (int i = 0; i < 10; i++) {
            // Arrange
            User testUser = createTestUser((long) (100 + i), "User" + i, "Test" + i, "user" + i + "@test.com");
            
            String tokenString = UUID.randomUUID().toString();
            PasswordResetToken testToken = new PasswordResetToken(tokenString, testUser);
            
            // Set expiry date to future
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.HOUR, 1);
            testToken.setExpiryDate(cal.getTime());
            
            // Mock repository behavior
            when(passwordResetTokenRepository.findByToken(tokenString)).thenReturn(testToken);
            
            // Mock UserDetailsService
            org.springframework.security.core.userdetails.User userDetails = 
                new org.springframework.security.core.userdetails.User(
                    testUser.getEmail(), 
                    testUser.getPassword(), 
                    java.util.Collections.emptyList()
                );
            when(userDetailsService.loadUserByUsername(testUser.getEmail())).thenReturn(userDetails);
            
            // Act
            String result = userSecurityService.validatePasswordResetToken(testUser.getId(), tokenString);
            
            // Assert
            assertNull("Valid password reset token for user " + i + " should return null (success)", result);
        }
    }

    /**
     * Property 8 (Multiple iterations): Test with multiple expired tokens
     * 
     * Test 6: Simulates property-based testing for expired tokens
     */
    @Test
    public void multipleExpiredTokensShouldAllReturnExpired() {
        // Test with 10 different expired tokens
        for (int i = 0; i < 10; i++) {
            // Arrange
            User testUser = createTestUser((long) (200 + i), "ExpUser" + i, "Test" + i, "expuser" + i + "@test.com");
            
            String tokenString = UUID.randomUUID().toString();
            PasswordResetToken testToken = new PasswordResetToken(tokenString, testUser);
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MINUTE, -(i + 1)); // Different expiry times
            testToken.setExpiryDate(cal.getTime());
            
            // Mock repository behavior
            when(passwordResetTokenRepository.findByToken(tokenString)).thenReturn(testToken);
            
            // Act
            String result = userSecurityService.validatePasswordResetToken(testUser.getId(), tokenString);
            
            // Assert
            assertEquals("Expired password reset token for user " + i + " should return 'expired'", 
                "expired", result);
        }
    }

    /**
     * Property 8 (Edge Case): Null token should fail validation
     * 
     * Test 7: For any null token, validation should return "invalidToken".
     */
    @Test
    public void nullTokenShouldFailValidation() {
        // Arrange
        User testUser = createTestUser(6L, "David", "Miller", "david.miller@test.com");
        String nullToken = null;
        
        // Mock repository to return null
        when(passwordResetTokenRepository.findByToken(nullToken)).thenReturn(null);
        
        // Act
        String result = userSecurityService.validatePasswordResetToken(testUser.getId(), nullToken);
        
        // Assert
        assertEquals("Null token should return 'invalidToken'", 
            "invalidToken", result);
    }

    // Helper methods

    private User createTestUser(Long id, String firstName, String lastName, String email) {
        User user = new User();
        user.setId(id);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword("hashedPassword123");
        user.setEnabled(true);
        user.setVerified(true);
        return user;
    }
    
    /**
     * Helper method to set private fields using reflection
     */
    private void setField(Object target, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set field " + fieldName, e);
        }
    }
}
