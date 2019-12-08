package com.mycorp.arithmeticcalculator.controller;

import java.util.Locale;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.mycorp.arithmeticcalculator.security.ActiveUserStore;
import com.mycorp.arithmeticcalculator.service.IUserService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import ua.com.foxminded.hotelbooking.domain.User;

@Controller
public class UserController {

    @Autowired
    ActiveUserStore activeUserStore;

    @Autowired
    IUserService userService;

    @RequestMapping(value = "/loggedUsers", method = RequestMethod.GET)
    public String getLoggedUsers(final Locale locale, final Model model) {
        model.addAttribute("users", activeUserStore.getUsers());
        return "users";
    }

    @RequestMapping(value = "/loggedUsersFromSessionRegistry", method = RequestMethod.GET)
    public String getLoggedUsersFromSessionRegistry(final Locale locale, final Model model) {
        model.addAttribute("users", userService.getUsersFromSessionRegistry());
        return "users";
    }
    
    @ApiOperation(value = "Retrieves an existing User")
    @ApiResponses(value = { 
            @ApiResponse(code = 200, message = "Body: User data"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Server Error")
    })
    @GetMapping("{userId}")
    public ResponseEntity<?> getUser(
            @ApiParam(value = "User id from which user object will retrieve", required = true) @PathVariable Long userId) {
        verifyUser(userId); 
        Optional<User> user = userRepository.findById(userId);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}