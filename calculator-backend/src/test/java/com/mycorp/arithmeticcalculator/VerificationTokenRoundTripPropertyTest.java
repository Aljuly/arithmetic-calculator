package com.mycorp.arithmeticcalculator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.mycorp.arithmeticcalculator.domain.User;
import com.mycorp.arithmeticcalculator.domain.VerificationToken;
import com.mycorp.arithmeticcalculator.repository.PasswordResetTokenRepository;
import com.mycorp.arithmeticcalculator.repository.RoleRepository;
import com.mycorp.arithmeticcalculator.repository.UserRepository;
import com.mycorp.arithmeticcalculator.repository.VerificationTokenRepository;
import com.mycorp.arithmeticcalculator.service.IUserAuthService;
import com.mycorp.arithmeticcalculator.service.UserAuthService;

import javax.validation.Validator;

/**
 * Property-based test for verification token round-trip.
 * 
 * **Validates: Requirements 1.3, 8.1, 8.2, 8.4**
 * 
 * Feature: email-verification-password-management, Property 2: Verification token round-trip
 * 
 * This test class validates that for any newly registered user, generating a verification token,
 * then validating it with the correct token string should set the user's verified field to true
 * and return success.
 */
public class VerificationTokenRoundTripPropertyTest {

    private UserAuthService userAuthService;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private VerificationTokenRepository tokenRepository;
    
    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;
    
    @Mock
    private RoleRepository roleRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Mock
    private Validator validator;
    
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        userAuthService = new UserAuthService();
        // Inject mocked dependencies using reflection
        setField(userAuthService, "repository", userRepository);
        setField(userAuthService, "tokenRepository", tokenRepository);
        setField(userAuthService, "passwordTokenRepository", passwordResetTokenRepository);
        setField(userAuthService, "roleRepository", roleRepository);
        setField(userAuthService, "passwordEncoder", passwordEncoder);
        setField(userAuthService, "validator", validator);
    }
    
    /**
     * Property 2: Verification token round-trip
     * 
     * Test 1: Basic verification token round-trip should succeed
     * 
     * For any newly registered user, generating a verification token, then validating it with
     * the correct token string should set the user's verified field to true and return success.
     */
    @Test
    public void basicVerificationTokenRoundTripShouldSucceed() {
        // Arrange: Create a user with verified=false
        User testUser = createTestUser(1L, "John", "Doe", "john.doe@test.com", false);
        
        // Create a verification token for the user
        String tokenString = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(tokenString, testUser);
        
        // Mock repository behavior
        when(tokenRepository.findByToken(tokenString)).thenReturn(verificationToken);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // Act: Validate the verification token
        String result = userAuthService.validateVerificationToken(tokenString);
        
        // Assert: Token validation should return "valid" and user should be verified
        assertEquals("Verification token validation should return 'valid'", 
            IUserAuthService.TOKEN_VALID, result);
        assertTrue("User should be verified after token validation", testUser.isVerified());
    }

    /**
     * Property 2: Verification token round-trip
     * 
     * Test 2: Verification token round-trip with different user should succeed
     */
    @Test
    public void verificationTokenRoundTripWithDifferentUserShouldSucceed() {
        // Arrange
        User testUser = createTestUser(2L, "Jane", "Smith", "jane.smith@test.com", false);
        
        String tokenString = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(tokenString, testUser);
        
        // Mock repository behavior
        when(tokenRepository.findByToken(tokenString)).thenReturn(verificationToken);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // Act
        String result = userAuthService.validateVerificationToken(tokenString);
        
        // Assert
        assertEquals("Verification token validation should return 'valid'", 
            IUserAuthService.TOKEN_VALID, result);
        assertTrue("User should be verified after token validation", testUser.isVerified());
    }

    /**
     * Property 2: Verification token round-trip
     * 
     * Test 3: Verification token round-trip with complex email should succeed
     */
    @Test
    public void verificationTokenRoundTripWithComplexEmailShouldSucceed() {
        // Arrange
        User testUser = createTestUser(3L, "Alice", "Johnson", "alice.johnson+test@example.co.uk", false);
        
        String tokenString = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(tokenString, testUser);
        
        // Mock repository behavior
        when(tokenRepository.findByToken(tokenString)).thenReturn(verificationToken);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // Act
        String result = userAuthService.validateVerificationToken(tokenString);
        
        // Assert
        assertEquals("Verification token validation should return 'valid'", 
            IUserAuthService.TOKEN_VALID, result);
        assertTrue("User with complex email should be verified after token validation", 
            testUser.isVerified());
    }

    /**
     * Property 2: Verification token round-trip
     * 
     * Test 4: Verification token round-trip with token about to expire should succeed
     * 
     * Tests that a token that is still valid (not yet expired) works correctly.
     */
    @Test
    public void verificationTokenRoundTripWithTokenAboutToExpireShouldSucceed() {
        // Arrange
        User testUser = createTestUser(4L, "Bob", "Williams", "bob.williams@test.com", false);
        
        String tokenString = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(tokenString, testUser);
        
        // Set expiry date to 1 minute in the future (still valid)
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 1);
        verificationToken.setExpiryDate(cal.getTime());
        
        // Mock repository behavior
        when(tokenRepository.findByToken(tokenString)).thenReturn(verificationToken);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // Act
        String result = userAuthService.validateVerificationToken(tokenString);
        
        // Assert
        assertEquals("Verification token about to expire should still return 'valid'", 
            IUserAuthService.TOKEN_VALID, result);
        assertTrue("User should be verified even with token about to expire", testUser.isVerified());
    }

    /**
     * Property 2: Verification token round-trip
     * 
     * Test 5: Verification token round-trip with freshly created token should succeed
     */
    @Test
    public void verificationTokenRoundTripWithFreshTokenShouldSucceed() {
        // Arrange
        User testUser = createTestUser(5L, "Charlie", "Brown", "charlie.brown@test.com", false);
        
        String tokenString = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(tokenString, testUser);
        
        // Set expiry date to 60 minutes in the future (fresh token)
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 60);
        verificationToken.setExpiryDate(cal.getTime());
        
        // Mock repository behavior
        when(tokenRepository.findByToken(tokenString)).thenReturn(verificationToken);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // Act
        String result = userAuthService.validateVerificationToken(tokenString);
        
        // Assert
        assertEquals("Fresh verification token should return 'valid'", 
            IUserAuthService.TOKEN_VALID, result);
        assertTrue("User should be verified with fresh token", testUser.isVerified());
    }

    /**
     * Property 2 (Multiple iterations): Test with multiple different users and tokens
     * 
     * Test 6: Simulates property-based testing by running multiple iterations with different data
     */
    @Test
    public void multipleVerificationTokenRoundTripsShouldAllSucceed() {
        // Test with 10 different users and tokens
        for (int i = 0; i < 10; i++) {
            // Arrange
            User testUser = createTestUser((long) (100 + i), "User" + i, "Test" + i, 
                "user" + i + "@test.com", false);
            
            String tokenString = UUID.randomUUID().toString();
            VerificationToken verificationToken = new VerificationToken(tokenString, testUser);
            
            // Mock repository behavior
            when(tokenRepository.findByToken(tokenString)).thenReturn(verificationToken);
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
            
            // Act
            String result = userAuthService.validateVerificationToken(tokenString);
            
            // Assert
            assertEquals("Verification token validation for user " + i + " should return 'valid'", 
                IUserAuthService.TOKEN_VALID, result);
            assertTrue("User " + i + " should be verified after token validation", testUser.isVerified());
        }
    }

    /**
     * Property 2 (Multiple iterations): Test with various email formats
     * 
     * Test 7: Tests that verification token round-trip works for users with different email formats
     */
    @Test
    public void verificationTokenRoundTripWithVariousEmailFormatsShouldSucceed() {
        String[] emails = {
            "simple@test.com",
            "with.dots@test.com",
            "with+plus@test.com",
            "with_underscore@test.com",
            "with-dash@test.com",
            "numeric123@test.com",
            "subdomain@mail.test.com",
            "long.email.address@example.test.com",
            "a@b.co",
            "test.user@company.org"
        };
        
        for (int i = 0; i < emails.length; i++) {
            // Arrange
            User testUser = createTestUser((long) (200 + i), "User" + i, "Test" + i, emails[i], false);
            
            String tokenString = UUID.randomUUID().toString();
            VerificationToken verificationToken = new VerificationToken(tokenString, testUser);
            
            // Mock repository behavior
            when(tokenRepository.findByToken(tokenString)).thenReturn(verificationToken);
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
            
            // Act
            String result = userAuthService.validateVerificationToken(tokenString);
            
            // Assert
            assertEquals("Verification token validation for email " + emails[i] + " should return 'valid'", 
                IUserAuthService.TOKEN_VALID, result);
            assertTrue("User with email " + emails[i] + " should be verified after token validation", 
                testUser.isVerified());
        }
    }

    /**
     * Property 2 (Multiple iterations): Test with various name formats
     * 
     * Test 8: Tests that verification token round-trip works for users with different name formats
     */
    @Test
    public void verificationTokenRoundTripWithVariousNameFormatsShouldSucceed() {
        String[][] names = {
            {"John", "Doe"},
            {"Mary-Jane", "Watson"},
            {"O'Brien", "Smith"},
            {"Jean-Luc", "Picard"},
            {"José", "García"},
            {"李", "明"},
            {"A", "B"},
            {"VeryLongFirstName", "VeryLongLastName"},
            {"First Middle", "Last"},
            {"Anne-Marie", "De La Cruz"}
        };
        
        for (int i = 0; i < names.length; i++) {
            // Arrange
            User testUser = createTestUser((long) (300 + i), names[i][0], names[i][1], 
                "user" + i + "@test.com", false);
            
            String tokenString = UUID.randomUUID().toString();
            VerificationToken verificationToken = new VerificationToken(tokenString, testUser);
            
            // Mock repository behavior
            when(tokenRepository.findByToken(tokenString)).thenReturn(verificationToken);
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
            
            // Act
            String result = userAuthService.validateVerificationToken(tokenString);
            
            // Assert
            assertEquals("Verification token validation for user " + names[i][0] + " " + names[i][1] + 
                " should return 'valid'", IUserAuthService.TOKEN_VALID, result);
            assertTrue("User " + names[i][0] + " " + names[i][1] + " should be verified after token validation", 
                testUser.isVerified());
        }
    }

    /**
     * Property 2 (Multiple iterations): Test with 15 random user combinations
     * 
     * Test 9: Comprehensive test with multiple verification token round-trips
     */
    @Test
    public void fifteenRandomVerificationTokenRoundTripsShouldAllSucceed() {
        // Test with 15 different user combinations
        for (int i = 1; i <= 15; i++) {
            // Arrange
            User testUser = createTestUser((long) (400 + i), "RandomFirst" + i, "RandomLast" + i, 
                "random" + i + "@example" + (i % 3) + ".com", false);
            
            String tokenString = UUID.randomUUID().toString();
            VerificationToken verificationToken = new VerificationToken(tokenString, testUser);
            
            // Mock repository behavior
            when(tokenRepository.findByToken(tokenString)).thenReturn(verificationToken);
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
            
            // Act
            String result = userAuthService.validateVerificationToken(tokenString);
            
            // Assert
            assertEquals("Verification token validation for random user " + i + " should return 'valid'", 
                IUserAuthService.TOKEN_VALID, result);
            assertTrue("Random user " + i + " should be verified after token validation", 
                testUser.isVerified());
        }
    }

    /**
     * Property 2 (Edge Case): Verification token round-trip with token at different expiry times
     * 
     * Test 10: Tests that tokens with various valid expiry times all work correctly
     */
    @Test
    public void verificationTokenRoundTripWithVariousExpiryTimesShouldSucceed() {
        // Test with tokens at different valid expiry times (1 to 10 minutes in the future)
        for (int i = 1; i <= 10; i++) {
            // Arrange
            User testUser = createTestUser((long) (500 + i), "ExpiryUser" + i, "Test" + i, 
                "expiryuser" + i + "@test.com", false);
            
            String tokenString = UUID.randomUUID().toString();
            VerificationToken verificationToken = new VerificationToken(tokenString, testUser);
            
            // Set expiry date to i minutes in the future
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MINUTE, i);
            verificationToken.setExpiryDate(cal.getTime());
            
            // Mock repository behavior
            when(tokenRepository.findByToken(tokenString)).thenReturn(verificationToken);
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
            
            // Act
            String result = userAuthService.validateVerificationToken(tokenString);
            
            // Assert
            assertEquals("Verification token with " + i + " minutes until expiry should return 'valid'", 
                IUserAuthService.TOKEN_VALID, result);
            assertTrue("User with token expiring in " + i + " minutes should be verified", 
                testUser.isVerified());
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
