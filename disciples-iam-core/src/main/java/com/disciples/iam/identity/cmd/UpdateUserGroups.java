package com.disciples.iam.identity.cmd;

import java.util.List;

public class UpdateUserGroups {

	private Long userId;
	private List<Long> groupIds;
    
    public UpdateUserGroups() {}

	public UpdateUserGroups(Long userId, List<Long> groupIds) {
		this.userId = userId;
		this.groupIds = groupIds;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public List<Long> getGroupIds() {
		return groupIds;
	}

	public void setGroupIds(List<Long> groupIds) {
		this.groupIds = groupIds;
	}
    
}
