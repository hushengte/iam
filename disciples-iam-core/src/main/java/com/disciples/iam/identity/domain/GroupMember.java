package com.disciples.iam.identity.domain;

import java.util.Objects;

import org.springframework.data.relational.core.mapping.Table;

import com.disciples.data.domain.AuditableEntity;

@Table("iam_user_group")
public class GroupMember extends AuditableEntity<Long> {

	private Long groupId;
	private Long userId;
	
	public GroupMember(Long groupId, Long userId) {
		this.groupId = groupId;
		this.userId = userId;
	}
	
	public Long getGroupId() {
		return groupId;
	}
	
	public Long getUserId() {
		return userId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(groupId, userId);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		GroupMember other = (GroupMember) obj;
		return Objects.equals(groupId, other.groupId) && Objects.equals(userId, other.userId);
	}
	
}
