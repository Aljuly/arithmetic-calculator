package com.mycorp.arithmeticcalculator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.mycorp.arithmeticcalculator.domain.User;
import com.mycorp.arithmeticcalculator.domain.VerificationToken;
import com.mycorp.arithmeticcalculator.repository.UserRepository;
import com.mycorp.arithmeticcalculator.repository.VerificationTokenRepository;
import com.mycorp.arithmeticcalculator.service.UserAuthService;

/**
 * Property-based test for expired verification token rejection.
 * 
 * **Validates: Requirements 1.4, 8.3, 9.2, 9.3**
 * 
 * Feature: email-verification-password-management, Property 3: Expired verification tokens are rejected
 * 
 * This test class validates that expired verification tokens are properly rejected,
 * return an expiry error, and do not modify the user's verified status across multiple scenarios.
 */
public class ExpiredVerificationTokenPropertyTest {

    private UserAuthService userAuthService;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private VerificationTokenRepository tokenRepository;
    
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        userAuthService = new UserAuthService();
        // Inject mocked dependencies using reflection
        setField(userAuthService, "repository", userRepository);
        setField(userAuthService, "tokenRepository", tokenRepository);
    }
    
    /**
     * Property 3: Expired verification tokens are rejected
     * 
     * Test 1: Token expired 1 minute ago should return "expired"
     * 
     * For any verification token with an expiry time in the past, attempting to validate it
     * should return "expired" and not modify the user's verified status.
     */
    @Test
    public void tokenExpiredOneMinuteAgoShouldBeRejected() {
        // Arrange: Create a user with verified=false
        User testUser = createTestUser(1L, "John", "Doe", "john.doe@test.com", false);
        
        // Create an expired verification token (1 minute ago)
        String tokenString = UUID.randomUUID().toString();
        VerificationToken testToken = new VerificationToken(tokenString, testUser);
        
        // Set expiry date to 1 minute in the past
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, -1);
        testToken.setExpiryDate(cal.getTime());
        
        // Mock repository behavior
        when(tokenRepository.findByToken(tokenString)).thenReturn(testToken);
        
        // Act: Validate the expired token
        String result = userAuthService.validateVerificationToken(tokenString);
        
        // Assert: Expired token should return "expired"
        assertEquals("Verification token expired 1 minute ago should return 'expired'", 
            "expired", result);
        
        // Verify user's verified status was not modified
        assertFalse("User should remain unverified after expired token validation", 
            testUser.isVerified());
        
        // Verify token was deleted
        verify(tokenRepository, times(1)).delete(testToken);
    }

    /**
     * Property 3: Expired verification tokens are rejected
     * 
     * Test 2: Token expired 1 hour ago should return "expired"
     */
    @Test
    public void tokenExpiredOneHourAgoShouldBeRejected() {
        // Arrange
        User testUser = createTestUser(2L, "Jane", "Smith", "jane.smith@test.com", false);
        
        String tokenString = UUID.randomUUID().toString();
        VerificationToken testToken = new VerificationToken(tokenString, testUser);
        
        // Set expiry date to 1 hour in the past
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, -1);
        testToken.setExpiryDate(cal.getTime());
        
        // Mock repository behavior
        when(tokenRepository.findByToken(tokenString)).thenReturn(testToken);
        
        // Act
        String result = userAuthService.validateVerificationToken(tokenString);
        
        // Assert
        assertEquals("Verification token expired 1 hour ago should return 'expired'", 
            "expired", result);
        assertFalse("User should remain unverified", testUser.isVerified());
        verify(tokenRepository, times(1)).delete(testToken);
    }

    /**
     * Property 3: Expired verification tokens are rejected
     * 
     * Test 3: Token expired 24 hours ago should return "expired"
     */
    @Test
    public void tokenExpiredOneDayAgoShouldBeRejected() {
        // Arrange
        User testUser = createTestUser(3L, "Alice", "Johnson", "alice.johnson@test.com", false);
        
        String tokenString = UUID.randomUUID().toString();
        VerificationToken testToken = new VerificationToken(tokenString, testUser);
        
        // Set expiry date to 24 hours in the past
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, -24);
        testToken.setExpiryDate(cal.getTime());
        
        // Mock repository behavior
        when(tokenRepository.findByToken(tokenString)).thenReturn(testToken);
        
        // Act
        String result = userAuthService.validateVerificationToken(tokenString);
        
        // Assert
        assertEquals("Verification token expired 24 hours ago should return 'expired'", 
            "expired", result);
        assertFalse("User should remain unverified", testUser.isVerified());
        verify(tokenRepository, times(1)).delete(testToken);
    }

    /**
     * Property 3: Expired verification tokens are rejected
     * 
     * Test 4: Token expired 1 second ago should return "expired"
     * 
     * Tests the boundary case where the token just expired.
     */
    @Test
    public void tokenExpiredOneSecondAgoShouldBeRejected() {
        // Arrange
        User testUser = createTestUser(4L, "Bob", "Williams", "bob.williams@test.com", false);
        
        String tokenString = UUID.randomUUID().toString();
        VerificationToken testToken = new VerificationToken(tokenString, testUser);
        
        // Set expiry date to 1 second in the past
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, -1);
        testToken.setExpiryDate(cal.getTime());
        
        // Mock repository behavior
        when(tokenRepository.findByToken(tokenString)).thenReturn(testToken);
        
        // Act
        String result = userAuthService.validateVerificationToken(tokenString);
        
        // Assert
        assertEquals("Verification token expired 1 second ago should return 'expired'", 
            "expired", result);
        assertFalse("User should remain unverified", testUser.isVerified());
        verify(tokenRepository, times(1)).delete(testToken);
    }

    /**
     * Property 3: Expired verification tokens are rejected
     * 
     * Test 5: Token expired 7 days ago should return "expired"
     */
    @Test
    public void tokenExpiredOneWeekAgoShouldBeRejected() {
        // Arrange
        User testUser = createTestUser(5L, "Charlie", "Brown", "charlie.brown@test.com", false);
        
        String tokenString = UUID.randomUUID().toString();
        VerificationToken testToken = new VerificationToken(tokenString, testUser);
        
        // Set expiry date to 7 days in the past
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -7);
        testToken.setExpiryDate(cal.getTime());
        
        // Mock repository behavior
        when(tokenRepository.findByToken(tokenString)).thenReturn(testToken);
        
        // Act
        String result = userAuthService.validateVerificationToken(tokenString);
        
        // Assert
        assertEquals("Verification token expired 7 days ago should return 'expired'", 
            "expired", result);
        assertFalse("User should remain unverified", testUser.isVerified());
        verify(tokenRepository, times(1)).delete(testToken);
    }

    /**
     * Property 3 (Multiple iterations): Test with multiple expired tokens at different expiry times
     * 
     * Test 6: Simulates property-based testing by running 100+ iterations with different expiry times
     */
    @Test
    public void multipleExpiredTokensWithDifferentExpiryTimesShouldAllBeRejected() {
        // Test with 100 different expired tokens with varying expiry times
        for (int i = 1; i <= 100; i++) {
            // Arrange
            User testUser = createTestUser((long) (100 + i), "User" + i, "Test" + i, "user" + i + "@test.com", false);
            
            String tokenString = UUID.randomUUID().toString();
            VerificationToken testToken = new VerificationToken(tokenString, testUser);
            
            // Set expiry date to different times in the past (i minutes ago)
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MINUTE, -i);
            testToken.setExpiryDate(cal.getTime());
            
            // Mock repository behavior
            when(tokenRepository.findByToken(tokenString)).thenReturn(testToken);
            
            // Act
            String result = userAuthService.validateVerificationToken(tokenString);
            
            // Assert
            assertEquals("Expired verification token for user " + i + " (expired " + i + " minutes ago) should return 'expired'", 
                "expired", result);
            assertFalse("User " + i + " should remain unverified", testUser.isVerified());
            verify(tokenRepository, times(1)).delete(testToken);
        }
    }

    /**
     * Property 3 (Multiple iterations): Test with multiple expired tokens at different hour intervals
     * 
     * Test 7: Tests expired tokens with hour-based expiry times
     */
    @Test
    public void multipleExpiredTokensWithHourIntervalsShouldAllBeRejected() {
        // Test with 50 different expired tokens with varying hour-based expiry times
        for (int i = 1; i <= 50; i++) {
            // Arrange
            User testUser = createTestUser((long) (200 + i), "HourUser" + i, "Test" + i, "houruser" + i + "@test.com", false);
            
            String tokenString = UUID.randomUUID().toString();
            VerificationToken testToken = new VerificationToken(tokenString, testUser);
            
            // Set expiry date to different times in the past (i hours ago)
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.HOUR, -i);
            testToken.setExpiryDate(cal.getTime());
            
            // Mock repository behavior
            when(tokenRepository.findByToken(tokenString)).thenReturn(testToken);
            
            // Act
            String result = userAuthService.validateVerificationToken(tokenString);
            
            // Assert
            assertEquals("Expired verification token for user " + i + " (expired " + i + " hours ago) should return 'expired'", 
                "expired", result);
            assertFalse("User " + i + " should remain unverified", testUser.isVerified());
            verify(tokenRepository, times(1)).delete(testToken);
        }
    }

    /**
     * Property 3 (Edge Case): Token expired exactly at current time should be rejected
     * 
     * Test 8: Tests the boundary case where expiry time equals current time
     */
    @Test
    public void tokenExpiredAtCurrentTimeShouldBeRejected() {
        // Arrange
        User testUser = createTestUser(6L, "David", "Miller", "david.miller@test.com", false);
        
        String tokenString = UUID.randomUUID().toString();
        VerificationToken testToken = new VerificationToken(tokenString, testUser);
        
        // Set expiry date to current time (effectively expired)
        Calendar cal = Calendar.getInstance();
        testToken.setExpiryDate(cal.getTime());
        
        // Mock repository behavior
        when(tokenRepository.findByToken(tokenString)).thenReturn(testToken);
        
        // Act
        String result = userAuthService.validateVerificationToken(tokenString);
        
        // Assert
        assertEquals("Verification token expired at current time should return 'expired'", 
            "expired", result);
        assertFalse("User should remain unverified", testUser.isVerified());
        verify(tokenRepository, times(1)).delete(testToken);
    }

    /**
     * Property 3 (Multiple iterations): Test with multiple users and expired tokens
     * 
     * Test 9: Tests that expired tokens are rejected regardless of user
     */
    @Test
    public void expiredTokensShouldBeRejectedForAllUsers() {
        // Test with 100 different users with expired tokens
        for (int i = 1; i <= 100; i++) {
            // Arrange
            User testUser = createTestUser((long) (300 + i), "MultiUser" + i, "Test" + i, "multiuser" + i + "@test.com", false);
            
            String tokenString = UUID.randomUUID().toString();
            VerificationToken testToken = new VerificationToken(tokenString, testUser);
            
            // Set expiry date to past (varying between 1 and 100 minutes ago)
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MINUTE, -i);
            testToken.setExpiryDate(cal.getTime());
            
            // Mock repository behavior
            when(tokenRepository.findByToken(tokenString)).thenReturn(testToken);
            
            // Act
            String result = userAuthService.validateVerificationToken(tokenString);
            
            // Assert
            assertEquals("Expired verification token should be rejected for user " + i, 
                "expired", result);
            assertFalse("User " + i + " should remain unverified", testUser.isVerified());
            verify(tokenRepository, times(1)).delete(testToken);
        }
    }

    /**
     * Property 3 (Edge Case): Token expired 30 days ago should return "expired"
     * 
     * Test 10: Tests long-expired tokens
     */
    @Test
    public void tokenExpiredThirtyDaysAgoShouldBeRejected() {
        // Arrange
        User testUser = createTestUser(7L, "Emma", "Davis", "emma.davis@test.com", false);
        
        String tokenString = UUID.randomUUID().toString();
        VerificationToken testToken = new VerificationToken(tokenString, testUser);
        
        // Set expiry date to 30 days in the past
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -30);
        testToken.setExpiryDate(cal.getTime());
        
        // Mock repository behavior
        when(tokenRepository.findByToken(tokenString)).thenReturn(testToken);
        
        // Act
        String result = userAuthService.validateVerificationToken(tokenString);
        
        // Assert
        assertEquals("Verification token expired 30 days ago should return 'expired'", 
            "expired", result);
        assertFalse("User should remain unverified", testUser.isVerified());
        verify(tokenRepository, times(1)).delete(testToken);
    }

    /**
     * Property 3 (Edge Case): Token expired with millisecond precision
     * 
     * Test 11: Tests that even millisecond-level expiry is detected
     */
    @Test
    public void tokenExpiredByMillisecondsShouldBeRejected() {
        // Arrange
        User testUser = createTestUser(8L, "Frank", "Wilson", "frank.wilson@test.com", false);
        
        String tokenString = UUID.randomUUID().toString();
        VerificationToken testToken = new VerificationToken(tokenString, testUser);
        
        // Set expiry date to 100 milliseconds in the past
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MILLISECOND, -100);
        testToken.setExpiryDate(cal.getTime());
        
        // Mock repository behavior
        when(tokenRepository.findByToken(tokenString)).thenReturn(testToken);
        
        // Act
        String result = userAuthService.validateVerificationToken(tokenString);
        
        // Assert
        assertEquals("Verification token expired by milliseconds should return 'expired'", 
            "expired", result);
        assertFalse("User should remain unverified", testUser.isVerified());
        verify(tokenRepository, times(1)).delete(testToken);
    }

    /**
     * Property 3 (Multiple iterations): Test with various day-based expiry intervals
     * 
     * Test 12: Tests expired tokens with day-based expiry times
     */
    @Test
    public void multipleExpiredTokensWithDayIntervalsShouldAllBeRejected() {
        // Test with 30 different expired tokens with varying day-based expiry times
        for (int i = 1; i <= 30; i++) {
            // Arrange
            User testUser = createTestUser((long) (400 + i), "DayUser" + i, "Test" + i, "dayuser" + i + "@test.com", false);
            
            String tokenString = UUID.randomUUID().toString();
            VerificationToken testToken = new VerificationToken(tokenString, testUser);
            
            // Set expiry date to different times in the past (i days ago)
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, -i);
            testToken.setExpiryDate(cal.getTime());
            
            // Mock repository behavior
            when(tokenRepository.findByToken(tokenString)).thenReturn(testToken);
            
            // Act
            String result = userAuthService.validateVerificationToken(tokenString);
            
            // Assert
            assertEquals("Expired verification token for user " + i + " (expired " + i + " days ago) should return 'expired'", 
                "expired", result);
            assertFalse("User " + i + " should remain unverified", testUser.isVerified());
            verify(tokenRepository, times(1)).delete(testToken);
        }
    }

    // Helper methods

    private User createTestUser(Long id, String firstName, String lastName, String email, boolean verified) {
        User user = new User();
        user.setId(id);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword("hashedPassword123");
        user.setEnabled(true);
        user.setVerified(verified);
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
