package com.disciples.iam.identity;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.disciples.iam.identity.domain.Group;
import com.disciples.iam.identity.domain.Groups;
import com.disciples.iam.identity.domain.User;
import com.disciples.iam.identity.domain.Users;

@Service
public class IdentityQueryService {
	
	private final Users users;
	private final Groups groups;

	@Autowired
	public IdentityQueryService(Users users, Groups groups) {
		this.users = users;
		this.groups = groups;
	}

	public Page<User> findPagedUsers(Integer page, Integer size, Long groupId, String keyword) {
		Pageable pageable = PageRequest.of(page, size);
		
		if (groupId != null) {
			if (StringUtils.hasText(keyword)) {
				return users.findByGroupIdAndKeyword(groupId, keyword.trim(), pageable);
			}
			return users.findByGroupId(groupId, pageable);
		}
		
		if (StringUtils.hasText(keyword)) {
			return users.findByKeyword(keyword.trim(), pageable);
		}
		return users.findAll(pageable);
	}
	
	public List<Map<String, Object>> findGroupKeyValues() {
		return groups.findKeyValues();
	}
	
	public Page<Group> findPagedGroups(Integer page, Integer size, String keyword) {
		Pageable pageable = PageRequest.of(page, size);
		
		if (StringUtils.hasText(keyword)) {
			return groups.findByName(keyword.trim(), pageable);
		}
		return groups.findAll(pageable);
	}
	
	public List<Long> findGroupIds(Long userId) {
		return groups.findGroupIdsByUserId(userId);
	}
	
}
