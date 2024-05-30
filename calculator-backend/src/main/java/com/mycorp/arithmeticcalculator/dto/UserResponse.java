package com.mycorp.arithmeticcalculator.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycorp.arithmeticcalculator.domain.User;
import com.mycorp.arithmeticcalculator.validators.ValidEmail;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"id",
"login",
"password",
"email",
"firstName",
"lastName",
"avatar",
"lastlogin",
"enabled",
"banned",
"verified",
"banReason",
"userRoles"
})
public class UserResponse {
	
	@JsonProperty("id")
	private String id;
	
	@JsonProperty("login")
	private String login;
	
	@JsonProperty("password")
	private String password;
    
	@ValidEmail
    @NotNull
    @Size(min = 3)
	@JsonProperty("email")
	private String email;
    
	@NotNull
    @Size(min = 1)
	@JsonProperty("firstName")
	private String firstName;
	
	@NotNull
    @Size(min = 1)
	@JsonProperty("lastName")
	private String lastName;
	
	@JsonProperty("avatar")
	private String avatar;
	
	@JsonProperty("lastlogin")
	private String lastlogin;
	
	@JsonProperty("enabled")
	private boolean enabled;
	
	@JsonProperty("banned")
	private boolean banned;
	
	@JsonProperty("verified")
	private boolean verified;
	
	@JsonProperty("banReason")
	private String banReason;
	
	@JsonProperty("userRoles")
	@Valid
	private List<RoleDto> userRoles = new ArrayList<>();
	
	/**
	* No args constructor for use in serialization
	*
	*/
	public UserResponse() {
	}

	/**
	*
	* @param lastName
	* @param last login
	* @param verified
	* @param avatar
	* @param login
	* @param enabled
	* @param banReason
	* @param firstName
	* @param userRoles
	* @param password
	* @param id
	* @param banned
	* @param email
	*/
	public UserResponse(
			String id, 
			String login, 
			String password, 
			String email, 
			String firstName, 
			String lastName, 
			String avatar, 
			String lastlogin, 
			boolean enabled, 
			boolean banned, 
			boolean verified, 
			String banReason, 
			List<RoleDto> userRoles) {
		super();
		this.id = id;
		this.login = login;
		this.password = password;
		this.email = email;
		this.firstName = firstName;
		this.lastName = lastName;
		this.avatar = avatar;
		this.lastlogin = lastlogin;
		this.enabled = enabled;
		this.banned = banned;
		this.verified = verified;
		this.banReason = banReason;
		this.userRoles = userRoles;
	}

	public UserResponse(User user) {
		this(
				user.getId().toString(),
				"", //user.getLogin(), 
				"", //password 
				user.getEmail(), 
				user.getFirstName(), 
				user.getLastName(), 
				"", //avatar, 
				"", //lastlogin,
				user.isEnabled(), 
				false, //banned, 
				true, //verified, 
				"", //banReason, 
			 	Optional.ofNullable(user.getRoles())
			 	.orElseGet(ArrayList::new).stream()
			 	.map(r -> new RoleDto(r))
			 	.collect(Collectors.toList())
				);
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getLastlogin() {
		return lastlogin;
	}

	public void setLastlogin(String lastlogin) {
		this.lastlogin = lastlogin;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isBanned() {
		return banned;
	}

	public void setBanned(boolean banned) {
		this.banned = banned;
	}

	public boolean isVerified() {
		return verified;
	}

	public void setVerified(boolean verified) {
		this.verified = verified;
	}

	public String getBanReason() {
		return banReason;
	}

	public void setBanReason(String banReason) {
		this.banReason = banReason;
	}

	public List<RoleDto> getUserRoles() {
		return userRoles;
	}

	public void setUserRoles(List<RoleDto> userRoles) {
		this.userRoles = userRoles;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(avatar, banReason, banned, email, enabled, firstName, id, lastName, lastlogin, login,
				password, userRoles, verified);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserResponse other = (UserResponse) obj;
		return Objects.equals(avatar, other.avatar) && Objects.equals(banReason, other.banReason)
				&& banned == other.banned && Objects.equals(email, other.email) && enabled == other.enabled
				&& Objects.equals(firstName, other.firstName) && Objects.equals(id, other.id)
				&& Objects.equals(lastName, other.lastName) && Objects.equals(lastlogin, other.lastlogin)
				&& Objects.equals(login, other.login) && Objects.equals(password, other.password)
				&& Objects.equals(userRoles, other.userRoles) && verified == other.verified;
	}

	@Override
	public String toString() {
		return "UserResponce [id=" + id + ", login=" + login + ", password=" + password + ", email=" + email
				+ ", firstName=" + firstName + ", lastName=" + lastName + ", avatar=" + avatar + ", lastlogin="
				+ lastlogin + ", enabled=" + enabled + ", banned=" + banned + ", verified=" + verified + ", banReason="
				+ banReason + ", userRoles=" + userRoles + "]";
	}
	
	// ToDo add to the Helper class
	public String toJson() throws JsonProcessingException {
		return (new ObjectMapper()).writeValueAsString(this);
	}
}


