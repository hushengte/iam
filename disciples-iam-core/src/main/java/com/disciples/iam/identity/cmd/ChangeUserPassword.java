package com.disciples.iam.identity.cmd;

public class ChangeUserPassword {
	
	private String username;
	private String currentPassword;
	private String newPassword;
	
	public ChangeUserPassword(String username, String currentPassword, String newPassword) {
		this.username = username;
		this.currentPassword = currentPassword;
		this.newPassword = newPassword;
	}

	public String getUsername() {
		return username;
	}

	public String getCurrentPassword() {
		return currentPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

}
