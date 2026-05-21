package com.mycorp.arithmeticcalculator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.mycorp.arithmeticcalculator.domain.User;
import com.mycorp.arithmeticcalculator.domain.VerificationToken;

/**
 * Property-based test for verification token expiry validation.
 * 
 * **Validates: Requirements 2.3, 9.1, 9.4**
 * 
 * @Tag("Feature: email-verification-password-management, Property 5: Verification tokens expire after 60 minutes")
 * 
 * This test class validates that newly generated verification tokens have an expiry date
 * set to exactly 60 minutes from the creation timestamp.
 */
public class VerificationTokenExpiryPropertyTest {

    private static final int EXPECTED_EXPIRY_MINUTES = 60;
    private static final long TOLERANCE_MILLISECONDS = 1000; // 1 second tolerance for timing variations

    /**
     * Property 5: Verification tokens expire after 60 minutes
     * 
     * Test 1: Basic scenario - new token has 60-minute expiry
     * 
     * For any newly generated verification token, the expiry date should be set to
     * exactly 60 minutes from the creation timestamp.
     */
    @Test
    public void newVerificationTokenShouldHave60MinuteExpiry() {
        // Arrange
        User testUser = createTestUser(1L, "John", "Doe", "john.doe@test.com");
        String tokenString = UUID.randomUUID().toString();
        
        // Act: Create a new verification token
        Date beforeCreation = new Date();
        VerificationToken token = new VerificationToken(tokenString, testUser);
        Date afterCreation = new Date();
        
        // Assert: Verify expiry is set to 60 minutes from creation
        assertNotNull("Token expiry date should not be null", token.getExpiryDate());
        
        // Calculate expected expiry time (60 minutes from creation)
        Calendar expectedExpiry = Calendar.getInstance();
        expectedExpiry.setTime(beforeCreation);
        expectedExpiry.add(Calendar.MINUTE, EXPECTED_EXPIRY_MINUTES);
        
        Calendar actualExpiry = Calendar.getInstance();
        actualExpiry.setTime(token.getExpiryDate());
        
        // Verify the expiry is approximately 60 minutes from creation
        long expectedExpiryMillis = expectedExpiry.getTimeInMillis();
        long actualExpiryMillis = actualExpiry.getTimeInMillis();
        long difference = Math.abs(actualExpiryMillis - expectedExpiryMillis);
        
        assertTrue("Token expiry should be approximately 60 minutes from creation (within 1 second tolerance). " +
                   "Expected: " + expectedExpiry.getTime() + ", Actual: " + actualExpiry.getTime() + 
                   ", Difference: " + difference + "ms",
                   difference <= TOLERANCE_MILLISECONDS);
    }

    /**
     * Property 5: Verification tokens expire after 60 minutes
     * 
     * Test 2: Token created with token string only should have 60-minute expiry
     */
    @Test
    public void tokenCreatedWithTokenStringOnlyShouldHave60MinuteExpiry() {
        // Arrange
        String tokenString = UUID.randomUUID().toString();
        
        // Act: Create token with token string only
        Date beforeCreation = new Date();
        VerificationToken token = new VerificationToken(tokenString);
        Date afterCreation = new Date();
        
        // Assert: Verify expiry is set to 60 minutes from creation
        assertNotNull("Token expiry date should not be null", token.getExpiryDate());
        
        Calendar expectedExpiry = Calendar.getInstance();
        expectedExpiry.setTime(beforeCreation);
        expectedExpiry.add(Calendar.MINUTE, EXPECTED_EXPIRY_MINUTES);
        
        Calendar actualExpiry = Calendar.getInstance();
        actualExpiry.setTime(token.getExpiryDate());
        
        long expectedExpiryMillis = expectedExpiry.getTimeInMillis();
        long actualExpiryMillis = actualExpiry.getTimeInMillis();
        long difference = Math.abs(actualExpiryMillis - expectedExpiryMillis);
        
        assertTrue("Token expiry should be approximately 60 minutes from creation (within 1 second tolerance). " +
                   "Expected: " + expectedExpiry.getTime() + ", Actual: " + actualExpiry.getTime() + 
                   ", Difference: " + difference + "ms",
                   difference <= TOLERANCE_MILLISECONDS);
    }

    /**
     * Property 5: Verification tokens expire after 60 minutes
     * 
     * Test 3: Multiple tokens created at different times should all have 60-minute expiry
     * 
     * Property-based test with 100 iterations to verify the property holds universally.
     */
    @Test
    public void multipleTokensShouldAllHave60MinuteExpiry() throws InterruptedException {
        // Test with 100 different token creations
        for (int i = 1; i <= 100; i++) {
            // Arrange
            User testUser = createTestUser((long) (100 + i), "User" + i, "Test" + i, 
                "user" + i + "@test.com");
            String tokenString = UUID.randomUUID().toString();
            
            // Act: Create token
            Date beforeCreation = new Date();
            VerificationToken token = new VerificationToken(tokenString, testUser);
            Date afterCreation = new Date();
            
            // Assert: Verify expiry is 60 minutes from creation
            assertNotNull("Iteration " + i + ": Token expiry date should not be null", 
                token.getExpiryDate());
            
            Calendar expectedExpiry = Calendar.getInstance();
            expectedExpiry.setTime(beforeCreation);
            expectedExpiry.add(Calendar.MINUTE, EXPECTED_EXPIRY_MINUTES);
            
            Calendar actualExpiry = Calendar.getInstance();
            actualExpiry.setTime(token.getExpiryDate());
            
            long expectedExpiryMillis = expectedExpiry.getTimeInMillis();
            long actualExpiryMillis = actualExpiry.getTimeInMillis();
            long difference = Math.abs(actualExpiryMillis - expectedExpiryMillis);
            
            assertTrue("Iteration " + i + ": Token expiry should be approximately 60 minutes from creation. " +
                       "Expected: " + expectedExpiry.getTime() + ", Actual: " + actualExpiry.getTime() + 
                       ", Difference: " + difference + "ms",
                       difference <= TOLERANCE_MILLISECONDS);
            
            // Small delay to ensure different creation times (every 10 iterations)
            if (i % 10 == 0) {
                Thread.sleep(10);
            }
        }
    }

    /**
     * Property 5: Verification tokens expire after 60 minutes
     * 
     * Test 4: Token expiry should be in the future relative to creation time
     */
    @Test
    public void tokenExpiryShouldBeInFuture() {
        // Test with 50 different scenarios
        for (int i = 1; i <= 50; i++) {
            // Arrange
            User testUser = createTestUser((long) (200 + i), "FutureUser" + i, "Test" + i, 
                "futureuser" + i + "@test.com");
            String tokenString = UUID.randomUUID().toString();
            
            // Act: Create token
            Date creationTime = new Date();
            VerificationToken token = new VerificationToken(tokenString, testUser);
            
            // Assert: Expiry should be in the future
            assertTrue("Iteration " + i + ": Token expiry should be after creation time",
                token.getExpiryDate().after(creationTime));
            
            // Verify it's approximately 60 minutes in the future
            long timeDifferenceMinutes = TimeUnit.MILLISECONDS.toMinutes(
                token.getExpiryDate().getTime() - creationTime.getTime());
            
            assertTrue("Iteration " + i + ": Token should expire approximately 60 minutes from creation. " +
                       "Actual: " + timeDifferenceMinutes + " minutes",
                       timeDifferenceMinutes >= 59 && timeDifferenceMinutes <= 61);
        }
    }

    /**
     * Property 5: Verification tokens expire after 60 minutes
     * 
     * Test 5: Token update should reset expiry to 60 minutes from update time
     */
    @Test
    public void tokenUpdateShouldResetExpiryTo60Minutes() {
        // Test with 30 different scenarios
        for (int i = 1; i <= 30; i++) {
            // Arrange
            User testUser = createTestUser((long) (300 + i), "UpdateUser" + i, "Test" + i, 
                "updateuser" + i + "@test.com");
            String originalToken = UUID.randomUUID().toString();
            VerificationToken token = new VerificationToken(originalToken, testUser);
            
            Date originalExpiryDate = token.getExpiryDate();
            
            // Act: Update the token
            String newTokenString = UUID.randomUUID().toString();
            Date beforeUpdate = new Date();
            token.updateToken(newTokenString);
            Date afterUpdate = new Date();
            
            // Assert: New expiry should be 60 minutes from update time
            assertNotNull("Iteration " + i + ": Updated token expiry should not be null", 
                token.getExpiryDate());
            
            // Verify the expiry date changed
            assertTrue("Iteration " + i + ": Expiry date should change after token update",
                !token.getExpiryDate().equals(originalExpiryDate));
            
            // Verify new expiry is approximately 60 minutes from update time
            Calendar expectedExpiry = Calendar.getInstance();
            expectedExpiry.setTime(beforeUpdate);
            expectedExpiry.add(Calendar.MINUTE, EXPECTED_EXPIRY_MINUTES);
            
            Calendar actualExpiry = Calendar.getInstance();
            actualExpiry.setTime(token.getExpiryDate());
            
            long expectedExpiryMillis = expectedExpiry.getTimeInMillis();
            long actualExpiryMillis = actualExpiry.getTimeInMillis();
            long difference = Math.abs(actualExpiryMillis - expectedExpiryMillis);
            
            assertTrue("Iteration " + i + ": Updated token expiry should be approximately 60 minutes from update time. " +
                       "Expected: " + expectedExpiry.getTime() + ", Actual: " + actualExpiry.getTime() + 
                       ", Difference: " + difference + "ms",
                       difference <= TOLERANCE_MILLISECONDS);
        }
    }

    /**
     * Property 5: Verification tokens expire after 60 minutes
     * 
     * Test 6: Expiry calculation should be consistent across different users
     */
    @Test
    public void expiryCalculationShouldBeConsistentAcrossDifferentUsers() {
        // Create multiple tokens at approximately the same time for different users
        Date baseTime = new Date();
        VerificationToken[] tokens = new VerificationToken[20];
        
        for (int i = 0; i < 20; i++) {
            User testUser = createTestUser((long) (400 + i), "ConsistentUser" + i, "Test" + i, 
                "consistentuser" + i + "@test.com");
            String tokenString = UUID.randomUUID().toString();
            tokens[i] = new VerificationToken(tokenString, testUser);
        }
        
        // All tokens should have similar expiry times (within a few seconds of each other)
        Date firstExpiry = tokens[0].getExpiryDate();
        
        for (int i = 1; i < 20; i++) {
            Date currentExpiry = tokens[i].getExpiryDate();
            long difference = Math.abs(currentExpiry.getTime() - firstExpiry.getTime());
            
            assertTrue("Token " + i + " expiry should be within 2 seconds of first token expiry. " +
                       "Difference: " + difference + "ms",
                       difference <= 2000); // 2 second tolerance
        }
        
        // Verify all tokens expire approximately 60 minutes from base time
        Calendar expectedExpiry = Calendar.getInstance();
        expectedExpiry.setTime(baseTime);
        expectedExpiry.add(Calendar.MINUTE, EXPECTED_EXPIRY_MINUTES);
        
        for (int i = 0; i < 20; i++) {
            Calendar actualExpiry = Calendar.getInstance();
            actualExpiry.setTime(tokens[i].getExpiryDate());
            
            long expectedExpiryMillis = expectedExpiry.getTimeInMillis();
            long actualExpiryMillis = actualExpiry.getTimeInMillis();
            long difference = Math.abs(actualExpiryMillis - expectedExpiryMillis);
            
            assertTrue("Token " + i + " expiry should be approximately 60 minutes from base time. " +
                       "Expected: " + expectedExpiry.getTime() + ", Actual: " + actualExpiry.getTime() + 
                       ", Difference: " + difference + "ms",
                       difference <= TOLERANCE_MILLISECONDS);
        }
    }

    /**
     * Property 5: Verification tokens expire after 60 minutes
     * 
     * Test 7: Expiry time should be exactly 60 minutes, not more or less
     */
    @Test
    public void expiryTimeShouldBeExactly60Minutes() {
        // Test with 100 iterations to ensure consistency
        for (int i = 1; i <= 100; i++) {
            // Arrange
            User testUser = createTestUser((long) (500 + i), "ExactUser" + i, "Test" + i, 
                "exactuser" + i + "@test.com");
            String tokenString = UUID.randomUUID().toString();
            
            // Act: Create token and measure time difference
            Date creationTime = new Date();
            VerificationToken token = new VerificationToken(tokenString, testUser);
            
            // Calculate the difference in minutes
            long differenceMillis = token.getExpiryDate().getTime() - creationTime.getTime();
            long differenceMinutes = TimeUnit.MILLISECONDS.toMinutes(differenceMillis);
            
            // Assert: Should be exactly 60 minutes (allowing for rounding)
            assertEquals("Iteration " + i + ": Token expiry should be exactly 60 minutes from creation",
                EXPECTED_EXPIRY_MINUTES, differenceMinutes);
            
            // Also verify in seconds for more precision (should be 3600 seconds ± 1 second)
            long differenceSeconds = TimeUnit.MILLISECONDS.toSeconds(differenceMillis);
            long expectedSeconds = EXPECTED_EXPIRY_MINUTES * 60;
            
            assertTrue("Iteration " + i + ": Token expiry should be approximately 3600 seconds (60 minutes). " +
                       "Expected: " + expectedSeconds + " seconds, Actual: " + differenceSeconds + " seconds",
                       Math.abs(differenceSeconds - expectedSeconds) <= 1);
        }
    }

    /**
     * Property 5: Verification tokens expire after 60 minutes
     * 
     * Test 8: Tokens created with different constructors should have same expiry behavior
     */
    @Test
    public void tokensCreatedWithDifferentConstructorsShouldHaveSameExpiryBehavior() {
        // Test with 50 pairs of tokens
        for (int i = 1; i <= 50; i++) {
            User testUser = createTestUser((long) (600 + i), "ConstructorUser" + i, "Test" + i, 
                "constructoruser" + i + "@test.com");
            String tokenString1 = UUID.randomUUID().toString();
            String tokenString2 = UUID.randomUUID().toString();
            
            // Create tokens using different constructors at approximately the same time
            Date baseTime = new Date();
            VerificationToken token1 = new VerificationToken(tokenString1, testUser);
            VerificationToken token2 = new VerificationToken(tokenString2);
            
            // Both should have expiry approximately 60 minutes from creation
            long diff1 = token1.getExpiryDate().getTime() - baseTime.getTime();
            long diff2 = token2.getExpiryDate().getTime() - baseTime.getTime();
            
            long minutes1 = TimeUnit.MILLISECONDS.toMinutes(diff1);
            long minutes2 = TimeUnit.MILLISECONDS.toMinutes(diff2);
            
            assertEquals("Iteration " + i + ": Token with user should expire in 60 minutes",
                EXPECTED_EXPIRY_MINUTES, minutes1);
            assertEquals("Iteration " + i + ": Token without user should expire in 60 minutes",
                EXPECTED_EXPIRY_MINUTES, minutes2);
            
            // Both tokens should have similar expiry times (within 1 second)
            long expiryDifference = Math.abs(token1.getExpiryDate().getTime() - token2.getExpiryDate().getTime());
            assertTrue("Iteration " + i + ": Both constructors should produce similar expiry times. " +
                       "Difference: " + expiryDifference + "ms",
                       expiryDifference <= 1000);
        }
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
        user.setVerified(false);
        return user;
    }
}
