package com.mycorp.arithmeticcalculator.controller;

import java.util.Locale;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.mycorp.arithmeticcalculator.domain.OnRegistrationCompleteEvent;
import com.mycorp.arithmeticcalculator.domain.User;
import com.mycorp.arithmeticcalculator.domain.VerificationToken;
import com.mycorp.arithmeticcalculator.dto.GenericResponse;
import com.mycorp.arithmeticcalculator.dto.PasswordDto;
import com.mycorp.arithmeticcalculator.dto.UserDto;
import com.mycorp.arithmeticcalculator.error.InvalidOldPasswordException;
import com.mycorp.arithmeticcalculator.error.UserNotFoundException;
import com.mycorp.arithmeticcalculator.security.ISecurityUserService;
import com.mycorp.arithmeticcalculator.service.IUserAuthService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api(value = "Registration")
@RestController
public class RegistrationController {
	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	private final IUserAuthService userService;

	private final ISecurityUserService securityUserService;
	/*
	 * @Autowired private ICaptchaService captchaService;
	 */
	private final MessageSource messages;

	private final JavaMailSender mailSender;

	private final ApplicationEventPublisher eventPublisher;

	private final Environment env;

	public RegistrationController(IUserAuthService userService,
								  ISecurityUserService securityUserService,
								  MessageSource messages,
								  JavaMailSender mailSender,
								  ApplicationEventPublisher eventPublisher,
								  Environment env) {
		super();
		this.userService = userService;
		this.securityUserService = securityUserService;
		this.messages = messages;
		this.mailSender = mailSender;
		this.eventPublisher = eventPublisher;
		this.env = env;
	}

	// Registration

	@ApiOperation(value = "Performing user registration")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Success"),
			@ApiResponse(code = 400, message = "Bad Request")
	})
	@PostMapping(value = "/user/registration")
	@ResponseBody
	public GenericResponse registerUserAccount(@Valid final UserDto accountDto, final HttpServletRequest request) {
		LOGGER.debug("Registering user account with information: {}", accountDto);
		final User registered = userService.registerNewUserAccount(accountDto);
		eventPublisher
				.publishEvent(new OnRegistrationCompleteEvent(registered, request.getLocale(), getAppUrl(request)));
		return new GenericResponse("success");
	}

	@GetMapping(value = "/registrationConfirm")
	public String confirmRegistration(final Locale locale, final Model model, @RequestParam("token") final String token)
			throws UnsupportedEncodingException {
		final String result = userService.validateVerificationToken(token);
		if (result.equals("valid")) {
			final User user = userService.getUser(token);
			// logger
			System.out.println(user);
			if (user.isUsing2FA()) {
				model.addAttribute("qr", userService.generateQRUrl(user));
				return "redirect:/qrcode.html?lang=" + locale.getLanguage();
			}
			model.addAttribute("message", messages.getMessage("message.accountVerified", null, locale));
			return "redirect:/login?lang=" + locale.getLanguage();
		}

		model.addAttribute("message", messages.getMessage("auth.message." + result, null, locale));
		model.addAttribute("expired", "expired".equals(result));
		model.addAttribute("token", token);
		return "redirect:/badUser.html?lang=" + locale.getLanguage();
	}

	// user activation - verification
	@ApiOperation(value = "user activation - verification. \r\n"
	+ "Sends Email to user with registration conformation")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Success"),
			@ApiResponse(code = 500, message = "Server Error")
	})
	@GetMapping(value = "/user/resendRegistrationToken")
	@ResponseBody
	public GenericResponse resendRegistrationToken(final HttpServletRequest request,
			@RequestParam("token") final String existingToken) {
		final VerificationToken newToken = userService.generateNewVerificationToken(existingToken);
		final User user = userService.getUser(newToken.getToken());
		mailSender.send(constructResendVerificationTokenEmail(getAppUrl(request), request.getLocale(), newToken, user));
		return new GenericResponse(messages.getMessage("message.resendToken", null, request.getLocale()));
	}

	// Reset password
	@PostMapping(value = "/user/resetPassword")
	@ResponseBody
	public GenericResponse resetPassword(final HttpServletRequest request,
			@RequestParam("email") final String userEmail) {
		final User user = userService.findUserByEmail(userEmail);
		if (user == null) {
			throw new UserNotFoundException();
		}
		final String token = UUID.randomUUID().toString();
		userService.createPasswordResetTokenForUser(user, token);
		mailSender.send(constructResetTokenEmail(getAppUrl(request), request.getLocale(), token, user));
		return new GenericResponse(messages.getMessage("message.resetPasswordEmail", null, request.getLocale()));
	}

	@GetMapping(value = "/user/changePassword")
	public String showChangePasswordPage(final Locale locale, final Model model, @RequestParam("id") final long id,
			@RequestParam("token") final String token) {
		final String result = securityUserService.validatePasswordResetToken(id, token);
		if (result != null) {
			model.addAttribute("message", messages.getMessage("auth.message." + result, null, locale));
			return "redirect:/login?lang=" + locale.getLanguage();
		}
		return "redirect:/updatePassword.html?lang=" + locale.getLanguage();
	}

	@PostMapping(value = "/user/savePassword")
	@PreAuthorize("hasRole('READ_PRIVILEGE')")
	@ResponseBody
	public GenericResponse savePassword(final Locale locale, @Valid PasswordDto passwordDto) {
		final User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		userService.changeUserPassword(user, passwordDto.getNewPassword());
		return new GenericResponse(messages.getMessage("message.resetPasswordSuc", null, locale));
	}

	// change user password
	@PostMapping(value = "/user/updatePassword")
	@PreAuthorize("hasRole('READ_PRIVILEGE')")
	@ResponseBody
	public GenericResponse changeUserPassword(final Locale locale, @Valid PasswordDto passwordDto) {
		final User user = userService.findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
		if (!userService.checkIfValidOldPassword(user, passwordDto.getOldPassword())) {
			throw new InvalidOldPasswordException();
		}
		userService.changeUserPassword(user, passwordDto.getNewPassword());
		return new GenericResponse(messages.getMessage("message.updatePasswordSuc", null, locale));
	}

	@PostMapping(value = "/user/update/2fa")
	@ResponseBody
	public GenericResponse modifyUser2FA(@RequestParam("use2FA") final boolean use2FA)
			throws UnsupportedEncodingException {
		final User user = userService.updateUser2FA(use2FA);
		if (use2FA) {
			return new GenericResponse(userService.generateQRUrl(user));
		}
		return new GenericResponse("Success");
	}

	// ============== NON-API ============

	private SimpleMailMessage constructResendVerificationTokenEmail(final String contextPath, final Locale locale,
			final VerificationToken newToken, final User user) {
		final String confirmationUrl = contextPath + "/registrationConfirm.html?token=" + newToken.getToken();
		final String message = messages.getMessage("message.resendToken", null, locale);
		return constructEmail("Resend Registration Token", message + " \r\n" + confirmationUrl, user);
	}

	private SimpleMailMessage constructResetTokenEmail(final String contextPath, final Locale locale,
			final String token, final User user) {
		final String url = contextPath + "/user/changePassword?id=" + user.getId() + "&token=" + token;
		final String message = messages.getMessage("message.resetPassword", null, locale);
		return constructEmail("Reset Password", message + " \r\n" + url, user);
	}

	private SimpleMailMessage constructEmail(String subject, String body, User user) {
		final SimpleMailMessage email = new SimpleMailMessage();
		email.setSubject(subject);
		email.setText(body);
		email.setTo(user.getEmail());
		email.setFrom(env.getProperty("support.email"));
		return email;
	}

	private String getAppUrl(HttpServletRequest request) {
		return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
	}

}
