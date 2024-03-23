package com.mycorp.arithmeticcalculator.validators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class RoleConstraintValidator implements ConstraintValidator<ValidRoleName, String> {

	private Pattern pattern;
	private Matcher matcher;
	private static final String ROLE_NAME_PATTERN = "^[A-Z]+(?:_[A-Z]+)*$";

	
    @Override
    public void initialize(ValidRoleName arg0) {
    }
	
	@Override
	public boolean isValid(final String value, ConstraintValidatorContext context) {
		pattern = Pattern.compile(ROLE_NAME_PATTERN);
		matcher = pattern.matcher(value);
		return matcher.matches();
	}
}
