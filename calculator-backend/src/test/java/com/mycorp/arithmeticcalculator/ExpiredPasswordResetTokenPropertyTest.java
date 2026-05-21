package com.mycorp.arithmeticcalculator;

import static org.junit.Assert.assertEquals;
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
 * Property-based test for expired password reset token rejection.
 * 
 * **Validates: Requirements 4.2, 9.2, 9.3**
 * 
 * Feature: email-verification-password-management, Property 9: Expired password reset tokens are rejected
 * 
 * This test class validates that expired password reset tokens are properly rejected
 * and prevent password change operations across multiple scenarios.
 */
public class ExpiredPasswordResetTokenPropertyTest {

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
     * Property 9: Expired password reset tokens are rejected
     * 
     * Test 1: Token expired 1 minute ago should return "expired"
     * 
     * For any password reset token with an expiry time in the past, attempting to validate it
     * should return "expired" and prevent password change operations.
     */
    @Test
    public void tokenExpiredOneMinuteAgoShouldBeRejected() {
        // Arrange: Create a user
        User testUser = createTestUser(1L, "John", "Doe", "john.doe@test.com");
        
        // Create an expired password reset token (1 minute ago)
        String tokenString = UUID.randomUUID().toString();
        PasswordResetToken testToken = new PasswordResetToken(tokenString, testUser);
        
        // Set expiry date to 1 minute in the past
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, -1);
        testToken.setExpiryDate(cal.getTime());
        
        // Mock repository behavior
        when(passwordResetTokenRepository.findByToken(tokenString)).thenReturn(testToken);
        
        // Act: Validate the expired token
        String result = userSecurityService.validatePasswordResetToken(testUser.getId(), tokenString);
        
        // Assert: Expired token should return "expired"
        assertEquals("Password reset token expired 1 minute ago should return 'expired'", 
            "expired", result);
    }

    /**
     * Property 9: Expired password reset tokens are rejected
     * 
     * Test 2: Token expired 1 hour ago should return "expired"
     */
    @Test
    public void tokenExpiredOneHourAgoShouldBeRejected() {
        // Arrange
        User testUser = createTestUser(2L, "Jane", "Smith", "jane.smith@test.com");
        
        String tokenString = UUID.randomUUID().toString();
        PasswordResetToken testToken = new PasswordResetToken(tokenString, testUser);
        
        // Set expiry date to 1 hour in the past
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, -1);
        testToken.setExpiryDate(cal.getTime());
        
        // Mock repository behavior
        when(passwordResetTokenRepository.findByToken(tokenString)).thenReturn(testToken);
        
        // Act
        String result = userSecurityService.validatePasswordResetToken(testUser.getId(), tokenString);
        
        // Assert
        assertEquals("Password reset token expired 1 hour ago should return 'expired'", 
            "expired", result);
    }

    /**
     * Property 9: Expired password reset tokens are rejected
     * 
     * Test 3: Token expired 24 hours ago should return "expired"
     */
    @Test
    public void tokenExpiredOneDayAgoShouldBeRejected() {
        // Arrange
        User testUser = createTestUser(3L, "Alice", "Johnson", "alice.johnson@test.com");
        
        String tokenString = UUID.randomUUID().toString();
        PasswordResetToken testToken = new PasswordResetToken(tokenString, testUser);
        
        // Set expiry date to 24 hours in the past
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, -24);
        testToken.setExpiryDate(cal.getTime());
        
        // Mock repository behavior
        when(passwordResetTokenRepository.findByToken(tokenString)).thenReturn(testToken);
        
        // Act
        String result = userSecurityService.validatePasswordResetToken(testUser.getId(), tokenString);
        
        // Assert
        assertEquals("Password reset token expired 24 hours ago should return 'expired'", 
            "expired", result);
    }

    /**
     * Property 9: Expired password reset tokens are rejected
     * 
     * Test 4: Token expired 1 second ago should return "expired"
     * 
     * Tests the boundary case where the token just expired.
     */
    @Test
    public void tokenExpiredOneSecondAgoShouldBeRejected() {
        // Arrange
        User testUser = createTestUser(4L, "Bob", "Williams", "bob.williams@test.com");
        
        String tokenString = UUID.randomUUID().toString();
        PasswordResetToken testToken = new PasswordResetToken(tokenString, testUser);
        
        // Set expiry date to 1 second in the past
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, -1);
        testToken.setExpiryDate(cal.getTime());
        
        // Mock repository behavior
        when(passwordResetTokenRepository.findByToken(tokenString)).thenReturn(testToken);
        
        // Act
        String result = userSecurityService.validatePasswordResetToken(testUser.getId(), tokenString);
        
        // Assert
        assertEquals("Password reset token expired 1 second ago should return 'expired'", 
            "expired", result);
    }

    /**
     * Property 9: Expired password reset tokens are rejected
     * 
     * Test 5: Token expired 7 days ago should return "expired"
     */
    @Test
    public void tokenExpiredOneWeekAgoShouldBeRejected() {
        // Arrange
        User testUser = createTestUser(5L, "Charlie", "Brown", "charlie.brown@test.com");
        
        String tokenString = UUID.randomUUID().toString();
        PasswordResetToken testToken = new PasswordResetToken(tokenString, testUser);
        
        // Set expiry date to 7 days in the past
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -7);
        testToken.setExpiryDate(cal.getTime());
        
        // Mock repository behavior
        when(passwordResetTokenRepository.findByToken(tokenString)).thenReturn(testToken);
        
        // Act
        String result = userSecurityService.validatePasswordResetToken(testUser.getId(), tokenString);
        
        // Assert
        assertEquals("Password reset token expired 7 days ago should return 'expired'", 
            "expired", result);
    }

    /**
     * Property 9 (Multiple iterations): Test with multiple expired tokens at different expiry times
     * 
     * Test 6: Simulates property-based testing by running multiple iterations with different expiry times
     */
    @Test
    public void multipleExpiredTokensWithDifferentExpiryTimesShouldAllBeRejected() {
        // Test with 10 different expired tokens with varying expiry times
        for (int i = 1; i <= 10; i++) {
            // Arrange
            User testUser = createTestUser((long) (100 + i), "User" + i, "Test" + i, "user" + i + "@test.com");
            
            String tokenString = UUID.randomUUID().toString();
            PasswordResetToken testToken = new PasswordResetToken(tokenString, testUser);
            
            // Set expiry date to different times in the past (i minutes ago)
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MINUTE, -i);
            testToken.setExpiryDate(cal.getTime());
            
            // Mock repository behavior
            when(passwordResetTokenRepository.findByToken(tokenString)).thenReturn(testToken);
            
            // Act
            String result = userSecurityService.validatePasswordResetToken(testUser.getId(), tokenString);
            
            // Assert
            assertEquals("Expired password reset token for user " + i + " (expired " + i + " minutes ago) should return 'expired'", 
                "expired", result);
        }
    }

    /**
     * Property 9 (Multiple iterations): Test with multiple expired tokens at different hour intervals
     * 
     * Test 7: Tests expired tokens with hour-based expiry times
     */
    @Test
    public void multipleExpiredTokensWithHourIntervalsShouldAllBeRejected() {
        // Test with 10 different expired tokens with varying hour-based expiry times
        for (int i = 1; i <= 10; i++) {
            // Arrange
            User testUser = createTestUser((long) (200 + i), "HourUser" + i, "Test" + i, "houruser" + i + "@test.com");
            
            String tokenString = UUID.randomUUID().toString();
            PasswordResetToken testToken = new PasswordResetToken(tokenString, testUser);
            
            // Set expiry date to different times in the past (i hours ago)
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.HOUR, -i);
            testToken.setExpiryDate(cal.getTime());
            
            // Mock repository behavior
            when(passwordResetTokenRepository.findByToken(tokenString)).thenReturn(testToken);
            
            // Act
            String result = userSecurityService.validatePasswordResetToken(testUser.getId(), tokenString);
            
            // Assert
            assertEquals("Expired password reset token for user " + i + " (expired " + i + " hours ago) should return 'expired'", 
                "expired", result);
        }
    }

    /**
     * Property 9 (Edge Case): Token expired exactly at current time should be rejected
     * 
     * Test 8: Tests the boundary case where expiry time equals current time
     */
    @Test
    public void tokenExpiredAtCurrentTimeShouldBeRejected() {
        // Arrange
        User testUser = createTestUser(6L, "David", "Miller", "david.miller@test.com");
        
        String tokenString = UUID.randomUUID().toString();
        PasswordResetToken testToken = new PasswordResetToken(tokenString, testUser);
        
        // Set expiry date to current time (effectively expired)
        Calendar cal = Calendar.getInstance();
        testToken.setExpiryDate(cal.getTime());
        
        // Mock repository behavior
        when(passwordResetTokenRepository.findByToken(tokenString)).thenReturn(testToken);
        
        // Act
        String result = userSecurityService.validatePasswordResetToken(testUser.getId(), tokenString);
        
        // Assert
        assertEquals("Password reset token expired at current time should return 'expired'", 
            "expired", result);
    }

    /**
     * Property 9 (Multiple iterations): Test with multiple users and expired tokens
     * 
     * Test 9: Tests that expired tokens are rejected regardless of user
     */
    @Test
    public void expiredTokensShouldBeRejectedForAllUsers() {
        // Test with 15 different users with expired tokens
        for (int i = 1; i <= 15; i++) {
            // Arrange
            User testUser = createTestUser((long) (300 + i), "MultiUser" + i, "Test" + i, "multiuser" + i + "@test.com");
            
            String tokenString = UUID.randomUUID().toString();
            PasswordResetToken testToken = new PasswordResetToken(tokenString, testUser);
            
            // Set expiry date to past (varying between 1 and 15 minutes ago)
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MINUTE, -i);
            testToken.setExpiryDate(cal.getTime());
            
            // Mock repository behavior
            when(passwordResetTokenRepository.findByToken(tokenString)).thenReturn(testToken);
            
            // Act
            String result = userSecurityService.validatePasswordResetToken(testUser.getId(), tokenString);
            
            // Assert
            assertEquals("Expired password reset token should be rejected for user " + i, 
                "expired", result);
        }
    }

    /**
     * Property 9 (Edge Case): Token expired 30 days ago should return "expired"
     * 
     * Test 10: Tests long-expired tokens
     */
    @Test
    public void tokenExpiredThirtyDaysAgoShouldBeRejected() {
        // Arrange
        User testUser = createTestUser(7L, "Emma", "Davis", "emma.davis@test.com");
        
        String tokenString = UUID.randomUUID().toString();
        PasswordResetToken testToken = new PasswordResetToken(tokenString, testUser);
        
        // Set expiry date to 30 days in the past
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -30);
        testToken.setExpiryDate(cal.getTime());
        
        // Mock repository behavior
        when(passwordResetTokenRepository.findByToken(tokenString)).thenReturn(testToken);
        
        // Act
        String result = userSecurityService.validatePasswordResetToken(testUser.getId(), tokenString);
        
        // Assert
        assertEquals("Password reset token expired 30 days ago should return 'expired'", 
            "expired", result);
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
