package com.disciples.iam.identity.domain.event;

public class UserRegistered {

	private Long userId;
	private String username;
	private String nickname;
	private String email;
	private String phone;
	
	public UserRegistered(Long userId, String username,
			String nickname, String email, String phone) {
		this.userId = userId;
		this.username = username;
		this.nickname = nickname;
		this.email = email;
		this.phone = phone;
	}

	public Long getUserId() {
		return userId;
	}

	public String getUsername() {
		return username;
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
