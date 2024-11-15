package com.disciples.iam.identity.domain.event;

import java.util.List;

public class GroupMemberRemoved {
	
	private Long groupId;
	private List<Long> userIds;
	
	public GroupMemberRemoved(Long groupId, List<Long> userIds) {
		this.groupId = groupId;
		this.userIds = userIds;
	}

	public Long getGroupId() {
		return groupId;
	}

	public List<Long> getUserIds() {
		return userIds;
	}

}
