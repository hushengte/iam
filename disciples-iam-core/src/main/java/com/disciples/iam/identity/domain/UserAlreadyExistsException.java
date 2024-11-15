package com.disciples.iam.identity.domain;

public class UserAlreadyExistsException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	private Long userId;
	private String username;

	public UserAlreadyExistsException(Long userId, String username) {
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
