package com.disciples.iam.identity.domain;

public class GroupNotExistsException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	private Long userId;

	public GroupNotExistsException(Long userId) {
		setUserId(userId);
	}
	
	public GroupNotExistsException(Long userId, String message, Throwable cause) {
		super(message, cause);
		setUserId(userId);
	}

	public GroupNotExistsException(Long userId, String message) {
		super(message);
		setUserId(userId);
	}

	public GroupNotExistsException(Long userId, Throwable cause) {
		super(cause);
		setUserId(userId);
	}

	private void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getUserId() {
		return userId;
	}
	
}
