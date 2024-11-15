package com.disciples.iam.identity.domain;

public class UserNotExistsException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	private Long userId;
	private String username;

	public UserNotExistsException(Long userId) {
		setUserId(userId);
	}
	
	public UserNotExistsException(String username) {
		this.username = username;
	}

	public UserNotExistsException(Long userId, String message, Throwable cause) {
		super(message, cause);
		setUserId(userId);
	}

	public UserNotExistsException(Long userId, String message) {
		super(message);
		setUserId(userId);
	}

	public UserNotExistsException(Long userId, Throwable cause) {
		super(cause);
		setUserId(userId);
	}

	private void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getUserId() {
		return userId;
	}

	public String getUsername() {
		return username;
	}
	
}
