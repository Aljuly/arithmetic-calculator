package com.mycorp.arithmeticcalculator.service;

import java.util.List;

import com.mycorp.arithmeticcalculator.domain.Privilege;
import com.mycorp.arithmeticcalculator.dto.RoleDto;
import com.mycorp.arithmeticcalculator.error.RoleProcessException;

public interface IRoleService {
	List<RoleDto> getAllRoles();
	List<Privilege> getAllPrivileges();
	void deleteRole(Long roleId) throws RoleProcessException;
	RoleDto createRole(RoleDto role) throws RoleProcessException;
}
