package com.mycorp.arithmeticcalculator.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mycorp.arithmeticcalculator.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {
	
	@Override
	Page<User> findAll(Pageable pageable);
	
	User findByLogin(String username);
	
	@Override
    void delete(User user);
		
    User findByEmail(String email);

    User findUserByEmail(String email);
    
    @Query(value = "SELECT * FROM user WHERE ID = ?1", nativeQuery = true)
	User findUserById(Long id);
	
	List<User> findAll();

}
