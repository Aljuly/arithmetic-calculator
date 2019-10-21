package com.mycorp.arithmeticcalculator.controller;

import java.util.Calendar;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import com.mycorp.arithmeticcalculator.domain.OnRegistrationCompleteEvent;
import com.mycorp.arithmeticcalculator.domain.User;
import com.mycorp.arithmeticcalculator.domain.VerificationToken;
import com.mycorp.arithmeticcalculator.dto.GenericResponse;
import com.mycorp.arithmeticcalculator.dto.UserDto;
import com.mycorp.arithmeticcalculator.service.IUserService;

public class RegistrationController {

	private final Logger LOGGER = LoggerFactory.getLogger(getClass());
	
	@Autowired
	ApplicationEventPublisher eventPublisher;

	@Autowired
	private IUserService service;

	@RequestMapping(value = "/regitrationConfirm", method = RequestMethod.GET)
	public String confirmRegistration(Locale locale, Model model, @RequestParam("token") String token) {
		VerificationToken verificationToken = userService.getVerificationToken(token);
		if (verificationToken == null) {
			String message = messages.getMessage("auth.message.invalidToken", null, locale);
			model.addAttribute("message", message);
			return "redirect:/badUser.html?lang=" + locale.getLanguage();
		}

		User user = verificationToken.getUser();
		Calendar cal = Calendar.getInstance();
		if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
			model.addAttribute("message", messages.getMessage("auth.message.expired", null, locale));
			model.addAttribute("expired", true);
			model.addAttribute("token", token);
			return "redirect:/badUser.html?lang=" + locale.getLanguage();
		}

		user.setEnabled(true);
		userService.saveRegisteredUser(user);
		model.addAttribute("message", messages.getMessage("message.accountVerified", null, locale));
		return "redirect:/login.html?lang=" + locale.getLanguage();
	}

	@RequestMapping(value = "/user/registration", method = RequestMethod.GET)
	public String showRegistrationForm(WebRequest request, Model model) {
		UserDto userDto = new UserDto();
		model.addAttribute("user", userDto);
		return "registration";
	}

	@RequestMapping(value = "/user/registration", method = RequestMethod.POST)
	@ResponseBody
	public GenericResponse registerUserAccount(
	      @Valid UserDto accountDto, HttpServletRequest request) {
	    LOGGER.debug("Registering user account with information: {}", accountDto);
	    User registered = createUserAccount(accountDto);
	    if (registered == null) {
	        throw new UserAlreadyExistException();
	    }
	    String appUrl = "http://" + request.getServerName() + ":" + 
	      request.getServerPort() + request.getContextPath();
	    
	    eventPublisher.publishEvent(
	      new OnRegistrationCompleteEvent(registered, request.getLocale(), appUrl));
	 
	    return new GenericResponse("success");
	}
	@RequestMapping(value = "/user/registration", method = RequestMethod.POST)
	@ResponseBody
	public GenericResponse registerUserAccount(
	      @Valid UserDto accountDto, HttpServletRequest request) {
	    logger.debug("Registering user account with information: {}", accountDto);
	    User registered = createUserAccount(accountDto);
	    if (registered == null) {
	        throw new UserAlreadyExistException();
	    }
	    String appUrl = "http://" + request.getServerName() + ":" + 
	      request.getServerPort() + request.getContextPath();
	    
	    eventPublisher.publishEvent(
	      new OnRegistrationCompleteEvent(registered, request.getLocale(), appUrl));
	 
	    return new GenericResponse("success");
	}

	@RequestMapping(value = "/user/resendRegistrationToken", method = RequestMethod.GET)
	@ResponseBody
	public GenericResponse resendRegistrationToken(HttpServletRequest request,
			@RequestParam("token") String existingToken) {
		VerificationToken newToken = userService.generateNewVerificationToken(existingToken);

		User user = userService.getUser(newToken.getToken());
		String appUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
		SimpleMailMessage email = constructResendVerificationTokenEmail(appUrl, request.getLocale(), newToken, user);
		mailSender.send(email);

		return new GenericResponse(messages.getMessage("message.resendToken", null, request.getLocale()));
	}

	private User createUserAccount(UserDto accountDto, BindingResult result) {
		User registered = null;
		try {
			registered = service.registerNewUserAccount(accountDto);
		} catch (EmailExistsException e) {
			return null;
		}
		return registered;
	}

	private SimpleMailMessage constructResendVerificationTokenEmail(String contextPath, Locale locale,
			VerificationToken newToken, User user) {
		String confirmationUrl = contextPath + "/regitrationConfirm.html?token=" + newToken.getToken();
		String message = messages.getMessage("message.resendToken", null, locale);
		SimpleMailMessage email = new SimpleMailMessage();
		email.setSubject("Resend Registration Token");
		email.setText(message + " rn" + confirmationUrl);
		email.setFrom(env.getProperty("support.email"));
		email.setTo(user.getEmail());
		return email;
	}

}
