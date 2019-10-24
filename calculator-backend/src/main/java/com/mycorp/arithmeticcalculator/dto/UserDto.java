package com.mycorp.arithmeticcalculator.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.mycorp.arithmeticcalculator.validators.PasswordMatches;
import com.mycorp.arithmeticcalculator.validators.ValidEmail;
import com.mycorp.arithmeticcalculator.validators.ValidPassword;

import lombok.Data;

@Data
@PasswordMatches
public class UserDto {
	@NotNull
	@NotEmpty
	private String firstName;

	@NotNull
	@NotEmpty
	private String lastName;

	@NotNull
	@NotEmpty
	@ValidPassword
	private String password;
	private String matchingPassword;

	@ValidEmail
	@NotNull
	@NotEmpty
	private String email;

}