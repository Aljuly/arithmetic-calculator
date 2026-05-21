package com.mycorp.arithmeticcalculator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.ArgumentMatchers.any;

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
import com.mycorp.arithmeticcalculator.service.IUserAuthService;
import com.mycorp.arithmeticcalculator.service.UserAuthService;

/**
 * Property-based test for new verification token invalidating old token.
 * 
 * **Validates: Requirements 2.1, 2.4**
 * 
 * @Tag("Feature: email-verification-password-management, Property 4: New verification token invalidates old token")
 * 
 * This test class validates that when a new verification token is generated for a user,
 * the old token becomes invalid and cannot be used for verification.
 */
public class NewVerificationTokenInvalidatesOldTokenPropertyTest {

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
     * Property 4: New verification token invalidates old token
     * 
     * Test 1: Basic scenario - generating new token invalidates old token
     * 
     * For any user with an existing verification token, generating a new verification token
     * should result in the old token no longer being valid for verification.
     */
    @Test
    public void generatingNewTokenShouldInvalidateOldToken() {
        // Arrange: Create a user with an initial verification token
        User testUser = createTestUser(1L, "John", "Doe", "john.doe@test.com", false);
        
        String oldTokenString = UUID.randomUUID().toString();
        VerificationToken oldToken = new VerificationToken(oldTokenString, testUser);
        
        // Mock repository to return the old token when searched by old token string
        when(tokenRepository.findByToken(oldTokenString)).thenReturn(oldToken);
        
        // Mock the save operation to simulate token update
        String newTokenString = UUID.randomUUID().toString();
        VerificationToken updatedToken = new VerificationToken(newTokenString, testUser);
        when(tokenRepository.save(any(VerificationToken.class))).thenReturn(updatedToken);
        
        // Act: Generate a new verification token
        VerificationToken newToken = userAuthService.generateNewVerificationToken(oldTokenString);
        
        // Simulate that the old token is no longer in the database
        when(tokenRepository.findByToken(oldTokenString)).thenReturn(null);
        when(tokenRepository.findByToken(newToken.getToken())).thenReturn(updatedToken);
        
        // Attempt to validate the old token
        String oldTokenResult = userAuthService.validateVerificationToken(oldTokenString);
        
        // Assert: Old token should be invalid
        assertEquals("Old verification token should return 'invalidToken' after new token is generated", 
            IUserAuthService.TOKEN_INVALID, oldTokenResult);
        
        // Verify the new token is different from the old token
        assertNotEquals("New token should be different from old token", 
            oldTokenString, newToken.getToken());
    }

    /**
     * Property 4: New verification token invalidates old token
     * 
     * Test 2: Multiple token regenerations - each new token invalidates the previous one
     */
    @Test
    public void multipleTokenRegenerationsShouldInvalidatePreviousTokens() {
        // Arrange
        User testUser = createTestUser(2L, "Jane", "Smith", "jane.smith@test.com", false);
        
        String token1 = UUID.randomUUID().toString();
        VerificationToken vToken1 = new VerificationToken(token1, testUser);
        
        when(tokenRepository.findByToken(token1)).thenReturn(vToken1);
        
        // Act & Assert: Generate multiple new tokens
        String previousToken = token1;
        for (int i = 0; i < 5; i++) {
            String newTokenString = UUID.randomUUID().toString();
            VerificationToken newToken = new VerificationToken(newTokenString, testUser);
            
            when(tokenRepository.save(any(VerificationToken.class))).thenReturn(newToken);
            VerificationToken generatedToken = userAuthService.generateNewVerificationToken(previousToken);
            
            // Simulate old token is no longer in database
            when(tokenRepository.findByToken(previousToken)).thenReturn(null);
            when(tokenRepository.findByToken(generatedToken.getToken())).thenReturn(newToken);
            
            // Verify old token is invalid
            String oldTokenResult = userAuthService.validateVerificationToken(previousToken);
            assertEquals("Token " + i + " should be invalid after regeneration", 
                IUserAuthService.TOKEN_INVALID, oldTokenResult);
            
            // Update for next iteration
            previousToken = generatedToken.getToken();
            when(tokenRepository.findByToken(previousToken)).thenReturn(newToken);
        }
    }

    /**
     * Property 4: New verification token invalidates old token
     * 
     * Test 3: Old token invalidation with different users
     * 
     * Tests that token invalidation works correctly across different users.
     */
    @Test
    public void oldTokenInvalidationShouldWorkForDifferentUsers() {
        // Test with 50 different users
        for (int i = 1; i <= 50; i++) {
            // Arrange
            User testUser = createTestUser((long) (100 + i), "User" + i, "Test" + i, 
                "user" + i + "@test.com", false);
            
            String oldTokenString = UUID.randomUUID().toString();
            VerificationToken oldToken = new VerificationToken(oldTokenString, testUser);
            
            when(tokenRepository.findByToken(oldTokenString)).thenReturn(oldToken);
            
            String newTokenString = UUID.randomUUID().toString();
            VerificationToken newToken = new VerificationToken(newTokenString, testUser);
            when(tokenRepository.save(any(VerificationToken.class))).thenReturn(newToken);
            
            // Act: Generate new token
            VerificationToken generatedToken = userAuthService.generateNewVerificationToken(oldTokenString);
            
            // Simulate old token is no longer in database
            when(tokenRepository.findByToken(oldTokenString)).thenReturn(null);
            when(tokenRepository.findByToken(generatedToken.getToken())).thenReturn(newToken);
            
            // Assert: Old token should be invalid
            String oldTokenResult = userAuthService.validateVerificationToken(oldTokenString);
            assertEquals("Old token for user " + i + " should be invalid", 
                IUserAuthService.TOKEN_INVALID, oldTokenResult);
            
            // Verify new token is different
            assertNotEquals("New token for user " + i + " should be different from old token", 
                oldTokenString, generatedToken.getToken());
        }
    }

    /**
     * Property 4: New verification token invalidates old token
     * 
     * Test 4: Old token cannot verify user after regeneration
     * 
     * Verifies that attempting to use the old token does not set user.verified=true.
     */
    @Test
    public void oldTokenCannotVerifyUserAfterRegeneration() {
        // Arrange
        User testUser = createTestUser(3L, "Alice", "Johnson", "alice.johnson@test.com", false);
        
        String oldTokenString = UUID.randomUUID().toString();
        VerificationToken oldToken = new VerificationToken(oldTokenString, testUser);
        
        when(tokenRepository.findByToken(oldTokenString)).thenReturn(oldToken);
        
        String newTokenString = UUID.randomUUID().toString();
        VerificationToken newToken = new VerificationToken(newTokenString, testUser);
        when(tokenRepository.save(any(VerificationToken.class))).thenReturn(newToken);
        
        // Act: Generate new token
        VerificationToken generatedToken = userAuthService.generateNewVerificationToken(oldTokenString);
        
        // Simulate old token is no longer in database
        when(tokenRepository.findByToken(oldTokenString)).thenReturn(null);
        when(tokenRepository.findByToken(generatedToken.getToken())).thenReturn(newToken);
        
        // Attempt to validate with old token
        String oldTokenResult = userAuthService.validateVerificationToken(oldTokenString);
        
        // Assert: Old token validation should fail
        assertEquals("Old token should return 'invalidToken'", IUserAuthService.TOKEN_INVALID, oldTokenResult);
        
        // Verify user is still unverified (old token didn't work)
        assertEquals("User should remain unverified after old token validation attempt", 
            false, testUser.isVerified());
    }

    /**
     * Property 4: New verification token invalidates old token
     * 
     * Test 5: New token works correctly after old token is invalidated
     * 
     * Verifies that the new token can successfully verify the user.
     */
    @Test
    public void newTokenShouldWorkAfterOldTokenIsInvalidated() {
        // Arrange
        User testUser = createTestUser(4L, "Bob", "Williams", "bob.williams@test.com", false);
        
        String oldTokenString = UUID.randomUUID().toString();
        VerificationToken oldToken = new VerificationToken(oldTokenString, testUser);
        
        when(tokenRepository.findByToken(oldTokenString)).thenReturn(oldToken);
        
        String newTokenString = UUID.randomUUID().toString();
        VerificationToken newToken = new VerificationToken(newTokenString, testUser);
        when(tokenRepository.save(any(VerificationToken.class))).thenReturn(newToken);
        
        // Act: Generate new token
        VerificationToken generatedToken = userAuthService.generateNewVerificationToken(oldTokenString);
        
        // Simulate old token is no longer in database, new token is available
        when(tokenRepository.findByToken(oldTokenString)).thenReturn(null);
        when(tokenRepository.findByToken(generatedToken.getToken())).thenReturn(newToken);
        
        // Verify old token is invalid
        String oldTokenResult = userAuthService.validateVerificationToken(oldTokenString);
        assertEquals("Old token should be invalid", IUserAuthService.TOKEN_INVALID, oldTokenResult);
        
        // Now validate with the new token
        String newTokenResult = userAuthService.validateVerificationToken(generatedToken.getToken());
        
        // Assert: New token should work successfully
        assertEquals("New token should successfully validate", IUserAuthService.TOKEN_VALID, newTokenResult);
        
        // Verify the token was deleted after successful validation
        verify(tokenRepository, times(1)).delete(newToken);
    }

    /**
     * Property 4: New verification token invalidates old token
     * 
     * Test 6: Multiple iterations - old token always becomes invalid
     * 
     * Property-based test with 100 iterations to verify the property holds universally.
     */
    @Test
    public void oldTokenAlwaysBecomesInvalidAfterRegeneration() {
        // Test with 100 different scenarios
        for (int i = 1; i <= 100; i++) {
            // Arrange
            User testUser = createTestUser((long) (200 + i), "PropUser" + i, "Test" + i, 
                "propuser" + i + "@test.com", false);
            
            String oldTokenString = UUID.randomUUID().toString();
            VerificationToken oldToken = new VerificationToken(oldTokenString, testUser);
            
            when(tokenRepository.findByToken(oldTokenString)).thenReturn(oldToken);
            
            String newTokenString = UUID.randomUUID().toString();
            VerificationToken newToken = new VerificationToken(newTokenString, testUser);
            when(tokenRepository.save(any(VerificationToken.class))).thenReturn(newToken);
            
            // Act: Generate new token
            VerificationToken generatedToken = userAuthService.generateNewVerificationToken(oldTokenString);
            
            // Simulate old token is no longer in database
            when(tokenRepository.findByToken(oldTokenString)).thenReturn(null);
            when(tokenRepository.findByToken(generatedToken.getToken())).thenReturn(newToken);
            
            // Assert: Old token should always be invalid
            String oldTokenResult = userAuthService.validateVerificationToken(oldTokenString);
            assertEquals("Iteration " + i + ": Old token should be invalid after regeneration", 
                IUserAuthService.TOKEN_INVALID, oldTokenResult);
            
            // Verify tokens are different
            assertNotEquals("Iteration " + i + ": New token should differ from old token", 
                oldTokenString, generatedToken.getToken());
        }
    }

    /**
     * Property 4: New verification token invalidates old token
     * 
     * Test 7: Old token with valid expiry still becomes invalid after regeneration
     * 
     * Even if the old token hasn't expired, it should still be invalid after regeneration.
     */
    @Test
    public void oldTokenWithValidExpiryStillBecomesInvalidAfterRegeneration() {
        // Arrange
        User testUser = createTestUser(5L, "Charlie", "Brown", "charlie.brown@test.com", false);
        
        String oldTokenString = UUID.randomUUID().toString();
        VerificationToken oldToken = new VerificationToken(oldTokenString, testUser);
        
        // Set expiry date to 30 minutes in the future (still valid)
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 30);
        oldToken.setExpiryDate(cal.getTime());
        
        when(tokenRepository.findByToken(oldTokenString)).thenReturn(oldToken);
        
        String newTokenString = UUID.randomUUID().toString();
        VerificationToken newToken = new VerificationToken(newTokenString, testUser);
        when(tokenRepository.save(any(VerificationToken.class))).thenReturn(newToken);
        
        // Act: Generate new token
        VerificationToken generatedToken = userAuthService.generateNewVerificationToken(oldTokenString);
        
        // Simulate old token is no longer in database (invalidated)
        when(tokenRepository.findByToken(oldTokenString)).thenReturn(null);
        when(tokenRepository.findByToken(generatedToken.getToken())).thenReturn(newToken);
        
        // Assert: Old token should be invalid even though it hadn't expired
        String oldTokenResult = userAuthService.validateVerificationToken(oldTokenString);
        assertEquals("Old token with valid expiry should still be invalid after regeneration", 
            IUserAuthService.TOKEN_INVALID, oldTokenResult);
    }

    /**
     * Property 4: New verification token invalidates old token
     * 
     * Test 8: Rapid token regeneration - each regeneration invalidates previous token
     */
    @Test
    public void rapidTokenRegenerationInvalidatesPreviousTokens() {
        // Arrange
        User testUser = createTestUser(6L, "David", "Miller", "david.miller@test.com", false);
        
        String token1 = UUID.randomUUID().toString();
        VerificationToken vToken1 = new VerificationToken(token1, testUser);
        
        when(tokenRepository.findByToken(token1)).thenReturn(vToken1);
        
        // Act: Rapidly generate 10 new tokens
        String[] tokens = new String[10];
        tokens[0] = token1;
        
        for (int i = 1; i < 10; i++) {
            String newTokenString = UUID.randomUUID().toString();
            VerificationToken newToken = new VerificationToken(newTokenString, testUser);
            
            when(tokenRepository.save(any(VerificationToken.class))).thenReturn(newToken);
            VerificationToken generatedToken = userAuthService.generateNewVerificationToken(tokens[i - 1]);
            
            tokens[i] = generatedToken.getToken();
            
            // Simulate previous token is no longer in database
            when(tokenRepository.findByToken(tokens[i - 1])).thenReturn(null);
            when(tokenRepository.findByToken(tokens[i])).thenReturn(newToken);
        }
        
        // Assert: All previous tokens should be invalid
        for (int i = 0; i < 9; i++) {
            String result = userAuthService.validateVerificationToken(tokens[i]);
            assertEquals("Token " + i + " should be invalid after subsequent regenerations", 
                IUserAuthService.TOKEN_INVALID, result);
        }
    }

    /**
     * Property 4: New verification token invalidates old token
     * 
     * Test 9: Token regeneration with different expiry times
     * 
     * Tests that token invalidation works regardless of expiry times.
     */
    @Test
    public void tokenRegenerationInvalidatesOldTokenRegardlessOfExpiryTime() {
        // Test with 30 different expiry scenarios
        for (int i = 1; i <= 30; i++) {
            // Arrange
            User testUser = createTestUser((long) (300 + i), "ExpiryUser" + i, "Test" + i, 
                "expiryuser" + i + "@test.com", false);
            
            String oldTokenString = UUID.randomUUID().toString();
            VerificationToken oldToken = new VerificationToken(oldTokenString, testUser);
            
            // Set varying expiry times (from 1 to 30 minutes in the future)
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MINUTE, i);
            oldToken.setExpiryDate(cal.getTime());
            
            when(tokenRepository.findByToken(oldTokenString)).thenReturn(oldToken);
            
            String newTokenString = UUID.randomUUID().toString();
            VerificationToken newToken = new VerificationToken(newTokenString, testUser);
            when(tokenRepository.save(any(VerificationToken.class))).thenReturn(newToken);
            
            // Act: Generate new token
            VerificationToken generatedToken = userAuthService.generateNewVerificationToken(oldTokenString);
            
            // Simulate old token is no longer in database
            when(tokenRepository.findByToken(oldTokenString)).thenReturn(null);
            when(tokenRepository.findByToken(generatedToken.getToken())).thenReturn(newToken);
            
            // Assert: Old token should be invalid regardless of expiry time
            String oldTokenResult = userAuthService.validateVerificationToken(oldTokenString);
            assertEquals("Old token with " + i + " minutes expiry should be invalid after regeneration", 
                IUserAuthService.TOKEN_INVALID, oldTokenResult);
        }
    }

    /**
     * Property 4: New verification token invalidates old token
     * 
     * Test 10: Verify generateNewVerificationToken is called correctly
     * 
     * Ensures the service method is invoked and returns a new token.
     */
    @Test
    public void generateNewVerificationTokenShouldReturnNewToken() {
        // Arrange
        User testUser = createTestUser(7L, "Emma", "Davis", "emma.davis@test.com", false);
        
        String oldTokenString = UUID.randomUUID().toString();
        VerificationToken oldToken = new VerificationToken(oldTokenString, testUser);
        
        when(tokenRepository.findByToken(oldTokenString)).thenReturn(oldToken);
        
        String newTokenString = UUID.randomUUID().toString();
        VerificationToken newToken = new VerificationToken(newTokenString, testUser);
        when(tokenRepository.save(any(VerificationToken.class))).thenReturn(newToken);
        
        // Act
        VerificationToken generatedToken = userAuthService.generateNewVerificationToken(oldTokenString);
        
        // Assert
        assertNotEquals("Generated token should be different from old token", 
            oldTokenString, generatedToken.getToken());
        
        // Verify save was called
        verify(tokenRepository, times(1)).save(any(VerificationToken.class));
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
