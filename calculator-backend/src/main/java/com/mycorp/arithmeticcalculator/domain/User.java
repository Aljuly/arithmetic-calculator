package com.mycorp.arithmeticcalculator.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.jboss.aerogear.security.otp.api.Base32;

import com.mycorp.arithmeticcalculator.dto.UserResponce;

@Entity
@Table(name = "_user")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String login;
	
	private String firstName;

	private String lastName;

	private String email;

	@Column(length = 60)
	private String password;

	private boolean enabled;

	private boolean isUsing2FA;

	private String secret;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
	private List<Role> roles = new ArrayList<Role>();

	public User() {
		super();
		this.secret = Base32.random();
		this.enabled = false;
	}
	
	public User(
			String login,
			String firstName, 
			String lastName, 
			String email, 
			String password, 
			boolean enabled,
			boolean isUsing2FA, 
			String secret
			) {
		super();
		this.login = login;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.password = password;
		this.enabled = enabled;
		this.isUsing2FA = isUsing2FA;
		this.secret = secret;
	}

	public User(UserResponce userResponse) {
		this(
				userResponse.getLogin(),
				userResponse.getFirstName(),
				userResponse.getLastName(),
				userResponse.getEmail(),
				userResponse.getPassword(),
				userResponse.isEnabled(),
				false,
				""
				);
	}
	
	public User updateUser(UserResponce userResponse) {
		this.login = userResponse.getLogin();
		this.firstName = userResponse.getFirstName();
		this.lastName = userResponse.getLastName();
		this.email = userResponse.getEmail();
		this.password = userResponse.getPassword();
		this.enabled = userResponse.isEnabled();
		this.isUsing2FA = false;
		this.secret = "";
		return this;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public void setLogin(String login) {
		this.login = login;
	}
	
	public String getLogin() {
		return login;
	}
	
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(final String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(final String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(final String username) {
		this.email = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(final String password) {
		this.password = password;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(final List<Role> roles) {
		this.roles = roles;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(final boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isUsing2FA() {
		return isUsing2FA;
	}

	public void setUsing2FA(boolean isUsing2FA) {
		this.isUsing2FA = isUsing2FA;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((email == null) ? 0 : email.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final User user = (User) obj;
		if (!email.equals(user.email)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("User [id=").append(id).append(", firstName=").append(firstName).append(", lastName=")
				.append(lastName).append(", email=").append(email).append(", password=").append(password)
				.append(", enabled=").append(enabled).append(", isUsing2FA=").append(isUsing2FA).append(", secret=")
				.append(secret).append(", roles=").append(roles).append("]");
		return builder.toString();
	}

}
