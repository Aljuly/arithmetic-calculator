package com.mycorp.arithmeticcalculator.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

	@Column(name = "first_name", length = 50)
	private String firstName;

	@Column(name = "last_name", length = 50)
	private String lastName;

	private String email;

	@Column(length = 60)
	private String password;

	private boolean enabled;

	@Column(name = "is_using2fa")
	private boolean isUsing2FA;

	private String secret;

	@Column(name = "avatarId")
	private String avatarId;

	private boolean banned;

	private boolean verified;

	@Column(columnDefinition = "TEXT")
	private String banReason;

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
			String secret,
			String avatarId,
			boolean banned,
			boolean verified,
			String banReason
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
		this.avatarId = avatarId;
		this.banned = banned;
		this.verified = verified;
		this.banReason = banReason;
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
				"",
				userResponse.getAvatar(),
				userResponse.isBanned(),
				userResponse.isVerified(),
				userResponse.getBanReason()
				);
	}

	public void updateUser(UserResponce userResponse) {
		this.login = userResponse.getLogin();
		this.firstName = userResponse.getFirstName();
		this.lastName = userResponse.getLastName();
		this.email = userResponse.getEmail();
		this.password = userResponse.getPassword();
		this.enabled = userResponse.isEnabled();
		this.isUsing2FA = false;
		this.secret = "";
		this.avatarId = userResponse.getAvatar();
		this.banned = userResponse.isBanned();
		this.verified = userResponse.isVerified();
		this.banReason = userResponse.getBanReason();
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

	public String getAvatarId() {
		return avatarId;
	}

	public void setAvatarId(String avatarId) {
		this.avatarId = avatarId;
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
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		final User user = (User) obj;
		return Objects.equals(email, user.email) &&
				Objects.equals(avatarId, user.avatarId) &&
				banned == user.banned &&
				verified == user.verified;
	}

	@Override
	public String toString() {
		return "User [id=" + id +
				", firstName=" + firstName +
				", lastName=" + lastName +
				", email=" + email +
				", password=" + password +
				", enabled=" + enabled +
				", isUsing2FA=" + isUsing2FA +
				", secret=" + secret +
				", avatarId=" + avatarId +
				", banned=" + banned +
				", verified=" + verified +
				", banReason=" + banReason +
				", roles=" + roles + "]";
	}
}

