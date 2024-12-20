package com.mycorp.arithmeticcalculator.controller;

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mycorp.arithmeticcalculator.domain.User;
import com.mycorp.arithmeticcalculator.error.ResourceNotFoundException;
import com.mycorp.arithmeticcalculator.security.ActiveUserStore;
import com.mycorp.arithmeticcalculator.service.IUserAuthService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api(value = "User")
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private ActiveUserStore activeUserStore;

    @Autowired
    private IUserAuthService userService;

	@Autowired
	private MessageSource messages; 
    
	@ApiOperation(value = "Retrieves all LoggedIn users")
	@ApiResponses(value = {
            @ApiResponse(code = 200, message = "Retrieves all LoggedIn users")
    })
    @GetMapping("/loggedUsers")
	public ResponseEntity<Iterable<String>> getLoggedUsers() {
		return new ResponseEntity<>(activeUserStore.getUsers(), HttpStatus.OK);
	}

    @ApiOperation(value = "View a list of users")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Retrieves all available users")
    })
    @GetMapping("/loggedUsersFromSessionRegistry")
    public ResponseEntity<List<User>> getAllUsers() {
        return new ResponseEntity<>(userService.getUsersFromSessionRegistry(), HttpStatus.OK);        
    }
    
    @ApiOperation(value = "Retrieves an existing User")
    @ApiResponses(value = { 
            @ApiResponse(code = 200, message = "Body: User data"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @GetMapping("{userId}")
    public ResponseEntity<?> getUserById(final Locale locale,
            @ApiParam(value = "User id from which user object will retrieve", required = true) @PathVariable Long userId) {
    	return new ResponseEntity<>(userService.getUserByID(userId)
    			.orElseThrow(() -> new ResourceNotFoundException(messages.getMessage("message.userNotFound", null, locale))), HttpStatus.OK);
    }
}