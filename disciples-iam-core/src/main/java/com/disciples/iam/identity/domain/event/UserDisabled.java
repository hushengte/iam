package com.disciples.iam.identity.domain.event;

public class UserDisabled {

	private Long userId;
	
	public UserDisabled(Long userId) {
		this.userId = userId;
	}

	public Long getUserId() {
		return userId;
	}

}
