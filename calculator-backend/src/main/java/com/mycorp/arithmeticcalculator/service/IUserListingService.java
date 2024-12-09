package com.mycorp.arithmeticcalculator.service;

import org.springframework.data.domain.Page;

import com.mycorp.arithmeticcalculator.dto.UserResponce;

public interface IUserListingService {
	
	Page<UserResponce> findAll(Integer pageNumber, int pageSize, String sortBy, String sortDirection);
	
	void deleteUser(Long userId);
	
	UserResponce getByNme(String userName);
	
	UserResponce createUserRecord(UserResponce userData);
	
	void updateUserRecord(UserResponce userData);
	
	String checkUserEmail(String userEmail);
	
	String checkUserName(String userName);

}
