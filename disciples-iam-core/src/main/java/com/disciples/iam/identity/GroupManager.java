package com.disciples.iam.identity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.disciples.iam.identity.cmd.AddUsersToGroup;
import com.disciples.iam.identity.cmd.SaveGroup;
import com.disciples.iam.identity.domain.Group;
import com.disciples.iam.identity.domain.GroupNotExistsException;
import com.disciples.iam.identity.domain.Groups;

/**
 * Group management Service
 */
@Service
public class GroupManager {

	private final Groups groups;
	
	@Autowired
	public GroupManager(Groups groups) {
		this.groups = groups;
	}
	
	public Group save(SaveGroup cmd) {
		Group group = Group.build(cmd.getId(), cmd.getName(), cmd.getRoles());
		return groups.save(group);
	}
	
	private Group existingGroup(Long groupId) {
		return groups.findById(groupId)
						.orElseThrow(() -> new GroupNotExistsException(groupId));
	}
	
	public void addUsers(AddUsersToGroup cmd) {
		Group group = existingGroup(cmd.getGroupId());
		group.addMembers(cmd.getMembers());
		groups.save(group);
	}
	
	public boolean delete(Long groupId) {
		Assert.notNull(groupId, () -> "groupId is required.");
		if (groups.countMembers(groupId) > 0) {
			throw new DataIntegrityViolationException("There are users in this group, please delete these users first.");
		}
		groups.deleteById(groupId);
		return true;
	}
	
}
