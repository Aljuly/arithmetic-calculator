package com.mycorp.arithmeticcalculator;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.runner.RunWith;

@RunWith(JUnitPlatform.class)
@SelectClasses({ // @formatter:off 
    ChangePasswordIntegrationTest.class, 
    TokenExpirationIntegrationTest.class,
    RegistrationControllerIntegrationTest.class,
    GetLoggedUsersIntegrationTest.class,
    UserServiceIntegrationTest.class,
    UserIntegrationTest.class,
    SpringSecurityRolesIntegrationTest.class,
})// @formatter:on
public class IntegrationSuite {
    // https://howtodoinjava.com/junit5/junit5-test-suites-examples/
}