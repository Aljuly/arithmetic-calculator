package com.mycorp.arithmeticcalculator.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mycorp.arithmeticcalculator.domain.Privilege;
import com.mycorp.arithmeticcalculator.domain.Role;
import com.mycorp.arithmeticcalculator.dto.RoleDto;
import com.mycorp.arithmeticcalculator.error.RoleNotFoundException;
import com.mycorp.arithmeticcalculator.error.RoleProcessException;
import com.mycorp.arithmeticcalculator.repository.PrivilegeRepository;
import com.mycorp.arithmeticcalculator.repository.RoleRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RoleService implements IRoleService {

	RoleRepository roleRepository;

	PrivilegeRepository privilegeRepository;

	public RoleService(RoleRepository roleRepository, PrivilegeRepository privilegeRepository) {
		this.roleRepository = roleRepository;
		this.privilegeRepository = privilegeRepository;
	}

	@Override
	public List<RoleDto> getAllRoles() {
		List<RoleDto> roles = new ArrayList<RoleDto>();
		for(Role role: roleRepository.findAll()) {
			roles.add(new RoleDto(role));
		}
		log.debug("Roles returned: {}", roles.size());
		return roles;
	}

	@Override
	public List<Privilege> getAllPrivileges() {
		return privilegeRepository.findAll();
	}

	@Override
	public void deleteRole(Long roleId) throws RoleProcessException {
		// TODO implement check Users have this role
		try {
			log.debug("About to delete role: {}", roleId);
			roleRepository.deleteById(roleId);
		} catch (Exception e) {
			throw new RoleNotFoundException("Role to delete not found", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public RoleDto createRole(RoleDto roleDto) throws RoleProcessException {
		Long roleId = Optional.ofNullable(roleDto).orElseThrow(() -> new RoleProcessException("Role not set")).getId();
		log.debug("Got role with Id: {}", roleId);
		Role role;
		if (Objects.nonNull(roleId)) {
			Optional<Role> retrievedRole = roleRepository.findById(roleId);
			role = retrievedRole.equals(Optional.empty()) ? new Role() : retrievedRole.get();
		} else { 
			role = new Role();
		}
		role.setName(roleDto.getRole());
		role.setDescription(roleDto.getDescription());
		role.getPrivileges().clear();
		role.getPrivileges().addAll(roleDto.getPrivileges()
				.stream()
				.map(p -> privilegeRepository.findByName(p))
				.filter(Objects::nonNull)
				.collect(Collectors.toList()));
		roleRepository.saveAndFlush(role);
		log.debug("Saved Role with Id: {}", role.getId());
		return new RoleDto(role);
	}

	@Override
	public List<Role> getRolesByNames(List<String> names) {
		return roleRepository.findByNameIn(names);
	}
	
	

}
