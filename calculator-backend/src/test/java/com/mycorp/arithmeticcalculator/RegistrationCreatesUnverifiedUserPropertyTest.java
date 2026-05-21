package com.mycorp.arithmeticcalculator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.mycorp.arithmeticcalculator.domain.Role;
import com.mycorp.arithmeticcalculator.domain.User;
import com.mycorp.arithmeticcalculator.dto.UserDto;
import com.mycorp.arithmeticcalculator.repository.RoleRepository;
import com.mycorp.arithmeticcalculator.repository.UserRepository;
import com.mycorp.arithmeticcalculator.service.UserAuthService;

import javax.validation.Validator;

/**
 * Property-based test for registration creating unverified users.
 * 
 * **Validates: Requirements 1.1**
 * 
 * Feature: email-verification-password-management, Property 1: Registration creates unverified user
 * 
 * This test class validates that user registration always creates users with verified=false
 * across multiple scenarios using mocked dependencies.
 */
public class RegistrationCreatesUnverifiedUserPropertyTest {

    private UserAuthService userAuthService;
    
    @Mock
    private UserRepository userRepository;
    
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
        setField(userAuthService, "roleRepository", roleRepository);
        setField(userAuthService, "passwordEncoder", passwordEncoder);
        setField(userAuthService, "validator", validator);
    }
    
    /**
     * Property 1: Registration creates unverified user
     * 
     * Test 1: Basic user registration should create unverified user
     * 
     * For any valid UserDto, registering a new user account should result in a User entity
     * with verified=false.
     */
    @Test
    public void basicUserRegistrationShouldCreateUnverifiedUser() {
        // Arrange: Create a basic UserDto
        UserDto userDto = createUserDto("John", "Doe", "john.doe@test.com", "password123");
        
        // Mock dependencies
        setupMocksForRegistration();
        
        // Act: Register the user
        User registeredUser = userAuthService.registerNewUserAccount(userDto);
        
        // Assert: User should be created with verified=false
        assertNotNull("Registered user should not be null", registeredUser);
        assertFalse("Newly registered user should have verified=false", registeredUser.isVerified());
    }

    /**
     * Property 1: Registration creates unverified user
     * 
     * Test 2: User with different email should create unverified user
     */
    @Test
    public void userWithDifferentEmailShouldCreateUnverifiedUser() {
        // Arrange
        UserDto userDto = createUserDto("Jane", "Smith", "jane.smith@example.com", "securePass456");
        
        // Mock dependencies
        setupMocksForRegistration();
        
        // Act
        User registeredUser = userAuthService.registerNewUserAccount(userDto);
        
        // Assert
        assertNotNull("Registered user should not be null", registeredUser);
        assertFalse("User with different email should have verified=false", registeredUser.isVerified());
    }

    /**
     * Property 1: Registration creates unverified user
     * 
     * Test 3: User with complex name should create unverified user
     */
    @Test
    public void userWithComplexNameShouldCreateUnverifiedUser() {
        // Arrange
        UserDto userDto = createUserDto("María José", "García-López", "maria.garcia@test.com", "myPassword789");
        
        // Mock dependencies
        setupMocksForRegistration();
        
        // Act
        User registeredUser = userAuthService.registerNewUserAccount(userDto);
        
        // Assert
        assertNotNull("Registered user should not be null", registeredUser);
        assertFalse("User with complex name should have verified=false", registeredUser.isVerified());
    }

    /**
     * Property 1: Registration creates unverified user
     * 
     * Test 4: User with long password should create unverified user
     */
    @Test
    public void userWithLongPasswordShouldCreateUnverifiedUser() {
        // Arrange
        UserDto userDto = createUserDto("Alice", "Johnson", "alice.johnson@test.com", 
            "ThisIsAVeryLongPasswordWithManyCharacters123456789!");
        
        // Mock dependencies
        setupMocksForRegistration();
        
        // Act
        User registeredUser = userAuthService.registerNewUserAccount(userDto);
        
        // Assert
        assertNotNull("Registered user should not be null", registeredUser);
        assertFalse("User with long password should have verified=false", registeredUser.isVerified());
    }

    /**
     * Property 1: Registration creates unverified user
     * 
     * Test 5: User with short name should create unverified user
     */
    @Test
    public void userWithShortNameShouldCreateUnverifiedUser() {
        // Arrange
        UserDto userDto = createUserDto("Bo", "Li", "bo.li@test.com", "pass123");
        
        // Mock dependencies
        setupMocksForRegistration();
        
        // Act
        User registeredUser = userAuthService.registerNewUserAccount(userDto);
        
        // Assert
        assertNotNull("Registered user should not be null", registeredUser);
        assertFalse("User with short name should have verified=false", registeredUser.isVerified());
    }

    /**
     * Property 1 (Multiple iterations): Test with multiple different users
     * 
     * Test 6: Simulates property-based testing by running multiple iterations with different data
     */
    @Test
    public void multipleUserRegistrationsShouldAllCreateUnverifiedUsers() {
        // Test with 10 different users
        for (int i = 0; i < 10; i++) {
            // Arrange
            UserDto userDto = createUserDto(
                "FirstName" + i, 
                "LastName" + i, 
                "user" + i + "@test.com", 
                "password" + i
            );
            
            // Mock dependencies
            setupMocksForRegistration();
            
            // Act
            User registeredUser = userAuthService.registerNewUserAccount(userDto);
            
            // Assert
            assertNotNull("Registered user " + i + " should not be null", registeredUser);
            assertFalse("User " + i + " should have verified=false", registeredUser.isVerified());
        }
    }

    /**
     * Property 1 (Multiple iterations): Test with various email formats
     * 
     * Test 7: Tests that users with different email formats are created as unverified
     */
    @Test
    public void usersWithVariousEmailFormatsShouldCreateUnverifiedUsers() {
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
            UserDto userDto = createUserDto("User" + i, "Test" + i, emails[i], "password" + i);
            
            // Mock dependencies
            setupMocksForRegistration();
            
            // Act
            User registeredUser = userAuthService.registerNewUserAccount(userDto);
            
            // Assert
            assertNotNull("Registered user with email " + emails[i] + " should not be null", registeredUser);
            assertFalse("User with email " + emails[i] + " should have verified=false", 
                registeredUser.isVerified());
        }
    }

    /**
     * Property 1 (Multiple iterations): Test with various password formats
     * 
     * Test 8: Tests that users with different password formats are created as unverified
     */
    @Test
    public void usersWithVariousPasswordFormatsShouldCreateUnverifiedUsers() {
        String[] passwords = {
            "simple123",
            "Complex!Pass123",
            "with spaces 456",
            "special@#$%chars",
            "VeryLongPasswordWithManyCharacters123456789",
            "short",
            "12345678",
            "MixedCase123",
            "under_score_pass",
            "dash-pass-word"
        };
        
        for (int i = 0; i < passwords.length; i++) {
            // Arrange
            UserDto userDto = createUserDto("User" + i, "Test" + i, "user" + i + "@test.com", passwords[i]);
            
            // Mock dependencies
            setupMocksForRegistration();
            
            // Act
            User registeredUser = userAuthService.registerNewUserAccount(userDto);
            
            // Assert
            assertNotNull("Registered user with password format " + i + " should not be null", registeredUser);
            assertFalse("User with password format " + i + " should have verified=false", 
                registeredUser.isVerified());
        }
    }

    /**
     * Property 1 (Multiple iterations): Test with various name formats
     * 
     * Test 9: Tests that users with different name formats are created as unverified
     */
    @Test
    public void usersWithVariousNameFormatsShouldCreateUnverifiedUsers() {
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
            UserDto userDto = createUserDto(names[i][0], names[i][1], "user" + i + "@test.com", "password" + i);
            
            // Mock dependencies
            setupMocksForRegistration();
            
            // Act
            User registeredUser = userAuthService.registerNewUserAccount(userDto);
            
            // Assert
            assertNotNull("Registered user with name " + names[i][0] + " " + names[i][1] + " should not be null", 
                registeredUser);
            assertFalse("User with name " + names[i][0] + " " + names[i][1] + " should have verified=false", 
                registeredUser.isVerified());
        }
    }

    /**
     * Property 1 (Multiple iterations): Test with 15 random user combinations
     * 
     * Test 10: Comprehensive test with multiple user registrations
     */
    @Test
    public void fifteenRandomUsersShouldAllBeCreatedAsUnverified() {
        // Test with 15 different user combinations
        for (int i = 1; i <= 15; i++) {
            // Arrange
            UserDto userDto = createUserDto(
                "RandomFirst" + i, 
                "RandomLast" + i, 
                "random" + i + "@example" + (i % 3) + ".com", 
                "randomPass" + i + "!"
            );
            
            // Mock dependencies
            setupMocksForRegistration();
            
            // Act
            User registeredUser = userAuthService.registerNewUserAccount(userDto);
            
            // Assert
            assertNotNull("Random user " + i + " should not be null", registeredUser);
            assertFalse("Random user " + i + " should have verified=false", registeredUser.isVerified());
        }
    }

    // Helper methods

    private UserDto createUserDto(String firstName, String lastName, String email, String password) {
        UserDto userDto = new UserDto();
        userDto.setFirstName(firstName);
        userDto.setLastName(lastName);
        userDto.setEmail(email);
        userDto.setPassword(password);
        userDto.setMatchingPassword(password);
        return userDto;
    }
    
    private void setupMocksForRegistration() {
        // Mock: Email does not exist (no duplicate)
        when(userRepository.findByEmail(any(String.class))).thenReturn(null);
        
        // Mock: Password encoder returns hashed password
        when(passwordEncoder.encode(any(String.class))).thenReturn("hashedPassword");
        
        // Mock: Role repository returns USER role
        Role userRole = new Role();
        userRole.setName("USER");
        when(roleRepository.findByName("USER")).thenReturn(userRole);
        
        // Mock: Validator returns no violations
        when(validator.validate(any(UserDto.class))).thenReturn(java.util.Collections.emptySet());
        
        // Mock: User repository save returns the user with verified=false
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L); // Set an ID to simulate database save
            return user;
        });
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
