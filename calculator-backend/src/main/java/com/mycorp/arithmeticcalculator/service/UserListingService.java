package com.mycorp.arithmeticcalculator.service;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycorp.arithmeticcalculator.domain.User;
import com.mycorp.arithmeticcalculator.dto.RoleDto;
import com.mycorp.arithmeticcalculator.dto.UserResponce;
import com.mycorp.arithmeticcalculator.error.UserNotFoundException;
import com.mycorp.arithmeticcalculator.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserListingService implements IUserListingService {
	
	private UserRepository userRepository;
	
	private IRoleService roleService;
	
	UserListingService(
			UserRepository userRepository,
			IRoleService roleService) {
		this.userRepository = userRepository;
		this.roleService = roleService;
	}
	
	@Override
	public Page<UserResponce> findAll(Integer pageNumber, int pageSize, String sortBy, String sortDirection) {
		Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
		Pageable pagable = PageRequest.of(pageNumber, pageSize, sort);
		return userRepository.findAll(pagable).map(u -> new UserResponce(u));
	}

	@Override
	public UserResponce getByNme(String userName) {
		User user = userRepository.findByLogin(userName);
		return new UserResponce(user);
	}

	@Override
	public void deleteUser(Long userId) throws IllegalArgumentException {
		try {
			log.debug("About to delete User record: {}", userId);
			userRepository.deleteById(userId);
		} catch (Exception e) {
			throw new UserNotFoundException("User to delete not found", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public UserResponce createUserRecord(UserResponce userData) {
		User user = new User(userData);
		user.setRoles(
				roleService.getRolesByNames(
				userData.getUserRoles().stream()
				.filter(Objects::nonNull)
				.map(RoleDto::getRole)
				.toList()));
		userRepository.saveAndFlush(user);
		log.debug("User Record saved successfully with Id: {}", user.getId());
		return new UserResponce(user);
	}

	@Override
	// ?!!!!!!!!
	//@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void updateUserRecord(UserResponce userData) throws UserNotFoundException {
		Long userId = Optional.ofNullable(userData)
				.orElseThrow(() -> new UserNotFoundException("User Not Set")).getId();
		log.debug("Getting user with Id: {}", userId);
		if (userId != null) {
			User user = userRepository.findUserById(userId);
			log.debug("Got user: {}", user);
			try {
				user.updateUser(userData);
				user.setRoles(
						roleService.getRolesByNames(
						userData.getUserRoles().stream()
						.filter(Objects::nonNull)
						.map(RoleDto::getRole)
						.toList()));
				userRepository.saveAndFlush(user);
				log.debug("Updated user record with Id: {}", userId);
			} catch (NoSuchElementException e) {
				throw new UserNotFoundException(String.format("User with Id: {} not found in DB", userId));
			}
		}
	}

	@Override
	public String checkUserEmail(String userEmail) {
		User retrievedUser = userRepository.findByEmail(userEmail);
		return (new ObjectMapper()).createObjectNode().put("isUniqueEmail", retrievedUser == null).toString();
	}

	@Override
	public String checkUserName(String userName) {
		User retrievedUser = userRepository.findByLogin(userName);
		return (new ObjectMapper()).createObjectNode().put("isUniqueUsername", retrievedUser == null).toString();
	}

}
