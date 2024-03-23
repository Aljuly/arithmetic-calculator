package com.mycorp.arithmeticcalculator.dto;

import java.util.List;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mycorp.arithmeticcalculator.domain.Privilege;
import com.mycorp.arithmeticcalculator.domain.Role;
import com.mycorp.arithmeticcalculator.validators.ValidRoleName;

public class RoleDto {
	
	private Long id;
	
	@JsonProperty("name")
	@NotNull
	@ValidRoleName
	private String role;
	
	private String description;
	
	@JsonProperty("operations")
	@NotNull
	@NotEmpty
	private List<String> privileges;
	
	public RoleDto(Long id, String role, String description, List<String> privileges) {
		this.id = id;
		this.role = role;
		this.description = description;
		this.privileges = privileges;
	}
	
	public RoleDto(Role role) {
		this.id = role.getId();
		this.role = role.getName();
		this.description = role.getDescription();
		this.privileges = role.getPrivileges().stream().map(Privilege::getName).toList();
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public List<String> getPrivileges() {
		return privileges;
	}
	
	public void setPrivileges(List<String> privileges) {
		this.privileges = privileges;
	}

	@Override
	public int hashCode() {
		return Objects.hash(description, id, privileges, role);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RoleDto other = (RoleDto) obj;
		return Objects.equals(description, other.description) && Objects.equals(id, other.id)
				&& Objects.equals(privileges, other.privileges) && Objects.equals(role, other.role);
	}

	@Override
	public String toString() {
		return "RoleDto [id=" + id + ", role=" + role + ", description=" + description + ", privileges=" + privileges
				+ "]";
	}

}
