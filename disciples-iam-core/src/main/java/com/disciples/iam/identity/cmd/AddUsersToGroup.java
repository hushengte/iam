package com.disciples.iam.identity.cmd;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.disciples.iam.identity.domain.GroupMember;
import com.disciples.iam.identity.domain.User;

public class AddUsersToGroup {
	
	private Long groupId;
	private Set<GroupMember> members;
	
	public AddUsersToGroup(List<User> users, Long groupId) {
		this.members = users.stream().map(user -> new GroupMember(groupId, user.getId()))
			.collect(Collectors.toSet());
		this.groupId = groupId;
	}

	public Long getGroupId() {
		return groupId;
	}

	public Set<GroupMember> getMembers() {
		return members;
	}

}
