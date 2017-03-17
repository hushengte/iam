package com.disciples.iam.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.disciples.iam.domain.User;

public interface UserManager {

	Page<User> find(int page, int size, Integer groupId, String keyword);
	
	User findOneByUsername(String username);
	
	boolean exists(String username);
	
	/**
	 * 如果用户标识为空，执行保存，否则执行更新
	 * @param user 用户数据
	 * @return 保存或更新后的用户
	 */
	User save(User user);
	
	/**
	 * 批量添加用户，如果用户组标识不为空，该用户组为添加用户所在组
	 * @param users 用户数据
	 * @param groupId 用户组标识，可以为null
	 * @return
	 */
	List<User> batchInsert(List<User> users, Integer groupId);
	
	void delete(Integer userId);
	void delete(List<Integer> userIds);
	
	List<Integer> groupIds(Integer userId);
	int updateGroups(Integer userId, List<Integer> groupIds);
	
	void changePassword(String oldPassword, String newPassword);
	void resetPassword(Integer userId);
	
	void enable(Integer userId);
	void disable(Integer userId);
	
}
