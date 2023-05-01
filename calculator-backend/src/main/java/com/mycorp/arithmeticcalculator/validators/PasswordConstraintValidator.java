package com.mycorp.arithmeticcalculator.validators;

import java.util.Arrays;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.EnglishSequenceData;
import org.passay.IllegalSequenceRule;
import org.passay.LengthRule;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.RuleResult;
import org.passay.WhitespaceRule;
import com.google.common.base.Joiner;

public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {
	 
    @Override
    public void initialize(ValidPassword arg0) {
    }
 
    @Override
    public boolean isValid(final String password, final ConstraintValidatorContext context) {
        final PasswordValidator validator = new PasswordValidator(Arrays.asList(
        		new LengthRule(7, 30), 
        		new CharacterRule(EnglishCharacterData.UpperCase, 1),
        		new CharacterRule(EnglishCharacterData.Digit, 1),
        		new CharacterRule(EnglishCharacterData.Special, 1),
        		new IllegalSequenceRule(EnglishSequenceData.Alphabetical, 5, false),
        		new IllegalSequenceRule(EnglishSequenceData.USQwerty, 5, false),
        		new IllegalSequenceRule(EnglishSequenceData.Numerical, 3, false),
        		new WhitespaceRule()));
        final RuleResult result = validator.validate(new PasswordData(password));
        if (result.isValid()) {
            return true;
        }
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(Joiner.on("\n").join(validator.getMessages(result))).addConstraintViolation();
        return false;
    }
}
