package com.mycorp.arithmeticcalculator.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mycorp.arithmeticcalculator.domain.PasswordResetToken;
import com.mycorp.arithmeticcalculator.domain.Role;
import com.mycorp.arithmeticcalculator.domain.User;
import com.mycorp.arithmeticcalculator.domain.VerificationToken;
import com.mycorp.arithmeticcalculator.dto.UserDto;
import com.mycorp.arithmeticcalculator.error.EmailExistsException;
import com.mycorp.arithmeticcalculator.repository.UserRepository;
import com.mycorp.arithmeticcalculator.repository.VerificationTokenRepository;

@Service
public class UserService implements IUserService {
    @Autowired
    private UserRepository repository;

    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
     
    @Override
    public User registerNewUserAccount(UserDto accountDto) throws EmailExistsException {
        if (emailExist(accountDto.getEmail())) {
            throw new EmailExistsException(
              "There is an account with that email adress:" + accountDto.getEmail());
        }
        User user = new User();
        user.setFirstName(accountDto.getFirstName());
        user.setLastName(accountDto.getLastName());
         
        user.setPassword(passwordEncoder.encode(accountDto.getPassword()));
         
        user.setEmail(accountDto.getEmail());
        user.setRoles(new Role(Integer.valueOf(1), user));
        return repository.save(user);
    }

    private boolean emailExist(String email) {
        User user = repository.findByEmail(email);
        if (user != null) {
            return true;
        }
        return false;
    }

    @Override
    public User getUser(String verificationToken) {
        User user = tokenRepository.findByToken(verificationToken).getUser();
        return user;
    }

    @Override
    public VerificationToken getVerificationToken(String VerificationToken) {
        return tokenRepository.findByToken(VerificationToken);
    }

    @Override
    public void saveRegisteredUser(User user) {
        repository.save(user);
    }

    @Override
    public void createVerificationToken(User user, String token) {
        VerificationToken myToken = new VerificationToken(token, user);
        tokenRepository.save(myToken);
    }
    
    public void createPasswordResetTokenForUser(User user, String token) {
        PasswordResetToken myToken = new PasswordResetToken(token, user);
        passwordTokenRepository.save(myToken);
    }
    
    public void changeUserPassword(User user, String password) {
        user.setPassword(passwordEncoder.encode(password));
        repository.save(user);
    }
    
}