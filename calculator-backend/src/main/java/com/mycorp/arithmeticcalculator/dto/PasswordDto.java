package com.mycorp.arithmeticcalculator.dto;

import com.mycorp.arithmeticcalculator.validators.ValidPassword;

public class PasswordDto {

	private String oldPassword;

	public PasswordDto() {
	}

	@ValidPassword
	private String newPassword;

	public PasswordDto(String oldPassword, String newPassword) {
		this.oldPassword = oldPassword;
		this.newPassword = newPassword;
	}

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

}