package com.disciples.iam.identity.domain;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.util.CollectionUtils;

import com.disciples.data.domain.AggregateRoot;
import com.disciples.data.identifier.LongId;
import com.disciples.iam.identity.domain.event.GroupCreated;
import com.disciples.iam.identity.domain.event.GroupMemberAdded;
import com.disciples.iam.identity.domain.event.GroupMemberRemoved;
import com.disciples.iam.identity.domain.event.GroupUpdated;

@Table("iam_group")
public class Group extends AggregateRoot<Group> {
	
	private String name;
	private String roles;
	
	@Transient
	private Set<GroupMember> members = new HashSet<>();

	public Group(Long id) {
		super(id);
	}
	
	@PersistenceCreator
	public Group(Long id, String name, String roles) {
		super(id);
		this.name = name;
		this.roles = roles;
	}
	
	public static Group build(Long id, String name, String roles) {
		boolean isUpdate = id != null;
		Long groupId = isUpdate ? id : LongId.generate();
		Group group = new Group(groupId, name, roles);
		if (isUpdate) {
			group.registerEvent(new GroupUpdated(group.getId(), group.getName(), group.getRoles()));
		} else {
			group.markAsNew();
			group.registerEvent(new GroupCreated(group.getId(), group.getName(), group.getRoles()));
		}
		return group;
	}
	
	public Group addMembers(Set<GroupMember> members) {
		if (!CollectionUtils.isEmpty(members)) {
			this.members.addAll(members);
			List<Long> userIds = members.stream()
					.map(GroupMember::getUserId)
					.collect(Collectors.toList());
			registerEvent(new GroupMemberAdded(this.getId(), userIds));
		}
		return this;
	}
	
	public Group removeMembers(Set<GroupMember> members) {
		if (!CollectionUtils.isEmpty(members)) {
			this.members.removeAll(members);
			List<Long> userIds = members.stream()
					.map(GroupMember::getUserId)
					.collect(Collectors.toList());
			registerEvent(new GroupMemberRemoved(this.getId(), userIds));
		}
		return this;
	}

	public String getName() {
		return name;
	}

	public String getRoles() {
		return roles;
	}

}
