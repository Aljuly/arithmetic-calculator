package com.mycorp.arithmeticcalculator.controller;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.mycorp.arithmeticcalculator.domain.User;
import com.mycorp.arithmeticcalculator.error.ResourceNotFoundException;
import com.mycorp.arithmeticcalculator.security.ActiveUserStore;
import com.mycorp.arithmeticcalculator.service.IUserService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Controller
public class UserController {

    @Autowired
    private ActiveUserStore activeUserStore;

    @Autowired
    private IUserService userService;

	@Autowired
	private MessageSource messages;
    
    @RequestMapping(value = "/loggedUsers", method = RequestMethod.GET)
    public String getLoggedUsers(final Locale locale, final Model model) {
        model.addAttribute("users", activeUserStore.getUsers());
        return "users";
    }

    @ApiOperation(value = "View a list of users")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Retrieves all available users"),
            @ApiResponse(code = 500, message = "Server Error")
    })
    @GetMapping("/loggedUsersFromSessionRegistry")
    public ResponseEntity<Iterable<User>> getAllUsers() {
        return new ResponseEntity<>(userService.getUsersFromSessionRegistry(), HttpStatus.OK);        
    }
    
    @ApiOperation(value = "Retrieves an existing User")
    @ApiResponses(value = { 
            @ApiResponse(code = 200, message = "Body: User data"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Server Error")
    })
    @GetMapping("{userId}")
    public ResponseEntity<?> getUserById(final Locale locale,
            @ApiParam(value = "User id from which user object will retrieve", required = true) @PathVariable Long userId) {
    	return new ResponseEntity<>(userService.getUserByID(userId)
    			.orElseThrow(() -> new ResourceNotFoundException(messages.getMessage("message.userNotFound", null, locale))), HttpStatus.OK);
    }
}