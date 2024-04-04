package com.mycorp.arithmeticcalculator.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mycorp.arithmeticcalculator.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

    @Override
    void delete(User user);

}
