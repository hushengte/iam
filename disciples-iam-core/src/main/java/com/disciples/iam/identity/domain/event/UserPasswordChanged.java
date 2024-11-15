package com.disciples.iam.identity.domain.event;

public class UserPasswordChanged {

	private Long userId;
	private String username;
	
	public UserPasswordChanged(Long userId, String username) {
		this.userId = userId;
		this.username = username;
	}

	public Long getUserId() {
		return userId;
	}

	public String getUsername() {
		return username;
	}

}
