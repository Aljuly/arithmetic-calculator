package com.mycorp.arithmeticcalculator.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mycorp.arithmeticcalculator.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

    User findUserByEmail(User user);

    @Override
    void delete(User user);

}
