package com.mycorp.arithmeticcalculator.dto;

import java.util.Objects;

public class LoginResponse {
	
    private String login;
    private String token;
    
	public LoginResponse(String login, String token) {
		super();
		this.login = login;
		this.token = token;
	}
	
	public String getLogin() {
		return login;
	}

	public void setEmail(String login) {
		this.login = login;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	@Override
	public int hashCode() {
		return Objects.hash(login, token);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LoginResponse other = (LoginResponse) obj;
		return Objects.equals(login, other.login) && Objects.equals(token, other.token);
	}
	
	@Override
	public String toString() {
		return "LoginResponse [email=" + login + ", token=" + token + "]";
	}
    
}
