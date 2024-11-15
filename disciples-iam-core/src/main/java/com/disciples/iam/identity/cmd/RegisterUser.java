package com.disciples.iam.identity.cmd;

public class RegisterUser {
	
    private String username;
    private String password;
    private String nickname;
    private String email;
    private String phone;
    
	public RegisterUser(String username, String password, String nickname, String email, String phone) {
		this.username = username;
		this.password = password;
		this.nickname = nickname;
		this.email = email;
		this.phone = phone;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getNickname() {
		return nickname;
	}

	public String getEmail() {
		return email;
	}

	public String getPhone() {
		return phone;
	}

}
