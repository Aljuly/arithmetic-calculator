package com.mycorp.arithmeticcalculator.service;

import java.util.List;
import java.util.Optional;
import java.io.UnsupportedEncodingException;

import com.mycorp.arithmeticcalculator.domain.PasswordResetToken;
import com.mycorp.arithmeticcalculator.domain.User;
import com.mycorp.arithmeticcalculator.domain.VerificationToken;
import com.mycorp.arithmeticcalculator.dto.UserDto;
import com.mycorp.arithmeticcalculator.error.UserAlreadyExistException;

public interface IUserAuthService {
	
    static final String TOKEN_INVALID = "invalidToken";
    static final String TOKEN_EXPIRED = "expired";
    static final String TOKEN_VALID = "valid";

    static String QR_PREFIX = "https://chart.googleapis.com/chart?chs=200x200&chld=M%%7C0&cht=qr&chl=";
    static String APP_NAME = "SpringRegistration";

    User registerNewUserAccount(UserDto accountDto) throws UserAlreadyExistException;

    User getUser(String verificationToken);

    void saveRegisteredUser(User user);

    void deleteUser(User user);

    void createVerificationTokenForUser(User user, String token);

    VerificationToken getVerificationToken(String VerificationToken);

    VerificationToken generateNewVerificationToken(String token);

    void createPasswordResetTokenForUser(User user, String token);

    User findUserByEmail(String email);

    PasswordResetToken getPasswordResetToken(String token);

    User getUserByPasswordResetToken(String token);

    Optional<User> getUserByID(long id);

    void changeUserPassword(User user, String password);

    boolean checkIfValidOldPassword(User user, String password);

    String validateVerificationToken(String token);

    String generateQRUrl(User user) throws UnsupportedEncodingException;

    User updateUser2FA(boolean use2FA);

    List<User> getUsersFromSessionRegistry();

}
