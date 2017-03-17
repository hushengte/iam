package com.disciples.iam.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.disciples.feed.KeyValue;
import com.disciples.iam.domain.Group;
import com.disciples.iam.domain.User;

public interface GroupManager {

	Page<Group> find(int page, int size, String keyword);
	
	List<Group> findAll();

	List<KeyValue> keyValues();
	
	/**
	 * 标识为空，执行保存，否则执行更新
	 * @param group 用户组数据
	 * @return 保存或更新后的用户组
	 */
	Group save(Group group);
	
	/**
	 * 删除用户组：如果用户组中存在用户，不允许删除
	 * @param groupId 用户组标识
	 */
	void delete(Integer groupId);

	List<User> findUsers(Integer groupId);
	
	void removeUser(Integer groupId, List<Integer> userIds);

}
