package com.disciples.iam.identity.domain.event;

public class UserEnabled {

	private Long userId;
	
	public UserEnabled(Long userId) {
		this.userId = userId;
	}

	public Long getUserId() {
		return userId;
	}

}
