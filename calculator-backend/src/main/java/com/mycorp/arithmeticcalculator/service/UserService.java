package com.mycorp.arithmeticcalculator.service;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mycorp.arithmeticcalculator.domain.User;
import com.mycorp.arithmeticcalculator.domain.VerificationToken;
import com.mycorp.arithmeticcalculator.dto.UserDto;
import com.mycorp.arithmeticcalculator.repository.VerificationTokenRepository;

@Service
public class UserService implements IUserService {
    @Autowired
    private UserRepository repository;

    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Override
    public User registerNewUserAccount(UserDto accountDto) throws EmailExistsException {

        if (emailExist(accountDto.getEmail())) {
            throw new EmailExistsException("There is an account with that email adress: " + accountDto.getEmail());
        }

        User user = new User();
        user.setFirstName(accountDto.getFirstName());
        user.setLastName(accountDto.getLastName());
        user.setPassword(accountDto.getPassword());
        user.setEmail(accountDto.getEmail());
        user.setRole(new Role(Integer.valueOf(1), user));
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
}