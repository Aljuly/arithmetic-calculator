package com.mycorp.arithmeticcalculator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.mycorp.arithmeticcalculator.domain.Privilege;
import com.mycorp.arithmeticcalculator.domain.Role;
import com.mycorp.arithmeticcalculator.domain.User;
import com.mycorp.arithmeticcalculator.repository.PrivilegeRepository;
import com.mycorp.arithmeticcalculator.repository.RoleRepository;
import com.mycorp.arithmeticcalculator.repository.UserRepository;

@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

	private boolean alreadySetup = false;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private PrivilegeRepository privilegeRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	// API

	@Override
	@Transactional
	public void onApplicationEvent(final ContextRefreshedEvent event) {
		if (alreadySetup) {
			return;
		}

		// == create initial privileges
		final Privilege readPrivilege = createPrivilegeIfNotFound("READ_PRIVILEGE");
		final Privilege writePrivilege = createPrivilegeIfNotFound("WRITE_PRIVILEGE");
		final Privilege passwordPrivilege = createPrivilegeIfNotFound("CHANGE_PASSWORD_PRIVILEGE");

		// == create initial roles
		final List<Privilege> adminPrivileges = 
				Stream.of(readPrivilege, writePrivilege, passwordPrivilege).collect(Collectors.toCollection(ArrayList::new));
		final List<Privilege> userPrivileges = 
				Stream.of(readPrivilege, passwordPrivilege).collect(Collectors.toCollection(ArrayList::new));
		final Role adminRole = createRoleIfNotFound("ROLE_ADMIN", adminPrivileges);
		createRoleIfNotFound("ROLE_USER", userPrivileges);

		// == create initial user
		createUserIfNotFound("test@test.com", "Test", "Test", "Passw0rd!", Stream.of(adminRole)
				.collect(Collectors.toCollection(ArrayList::new)));
		alreadySetup = true;
	}

	@Transactional
	Privilege createPrivilegeIfNotFound(final String name) {
		Privilege privilege = privilegeRepository.findByName(name);
		if (privilege == null) {
			privilege = new Privilege(name);
			privilege = privilegeRepository.save(privilege);
		}
		return privilege;
	}

	@Transactional
	Role createRoleIfNotFound(final String name, final List<Privilege> privileges) {
		Role role = roleRepository.findByName(name);
		if (role == null) {
			role = new Role(name);
		}
		role.setPrivileges(privileges);
		role = roleRepository.save(role);
		return role;
	}

	@Transactional
	User createUserIfNotFound(final String email, final String firstName, final String lastName,
							  final String password, final Collection<Role> roles) {
		User user = userRepository.findByEmail(email);
		if (user == null) {
			user = new User();
			user.setFirstName(firstName);
			user.setLastName(lastName);
			user.setPassword(passwordEncoder.encode(password));
			user.setEmail(email);
			user.setEnabled(true);
		}
		user.setRoles(roles);
		user = userRepository.save(user);
		return user;
	}

}
