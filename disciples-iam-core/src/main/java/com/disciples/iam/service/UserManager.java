package com.disciples.iam.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.disciples.iam.domain.User;

/**
 * User management Service
 */
public interface UserManager {

    /**
     * Find a page of user
     * @param page Page number, start from 0
     * @param size Page size
     * @param groupId Group id
     * @param username Search keyword of username
     * @return A page of user
     */
	Page<User> find(int page, int size, Integer groupId, String username);
	
	/**
	 * Find users by group id
	 * @param groupId Group id
	 * @return List of users of the group
	 */
	List<User> find(Integer groupId);
	
	/**
	 * Find user by username
	 * @param username Username
	 * @return A user
	 */
	User findOneByUsername(String username);
	
	/**
	 * Find user by id
	 * @param id User id
	 * @return A user
	 */
	User findOne(Integer id);
	
	/**
	 * Test whether a user exists.
	 * @param username Username
	 * @return Return <code>true</code> if exists, otherwise return <code>false</code>
	 */
	boolean exists(String username);
	
	/**
	 * Save or update user. If user id is specified, it will execute update,
	 * otherwise execute save operation.
	 * @param user User data
	 * @return A saved user
	 */
	User save(User user);
	
	/**
	 * Batch insert user. If group id is specified, all users will be add to the group.
	 * @param users List of users
	 * @param groupId Group id, can be null
	 * @return List of added user
	 */
	List<User> batchInsert(List<User> users, Integer groupId);
	
	/**
	 * Delete user by id
	 * @param userId User id
	 */
	void delete(Integer userId);
	
	/**
	 * Delete user by ids
	 * @param userIds List of user id
	 */
	void delete(List<Integer> userIds);
	
	/**
	 * Find list of group id of the user
	 * @param userId User id
	 * @return List of group id of the user
	 */
	List<Integer> groupIds(Integer userId);
	
	/**
	 * Find group roles of a user
	 * @param userId User id
	 * @return List of group roles of the user
	 */
	List<String> getGroupRoles(Integer userId);
	
	/**
	 * Update group id list of a user
	 * @param userId User id
	 * @param groupIds New group id list
	 * @return Updated count
	 */
	int updateGroups(Integer userId, List<Integer> groupIds);
	
	/**
	 * Change user's password
	 * @param userId User id
	 * @param oldPassword Old password
	 * @param newPassword New password
	 */
	void changePassword(Integer userId, String oldPassword, String newPassword);
	
	/**
	 * Reset user's password
	 * @param userId User id
	 */
	void resetPassword(Integer userId);
	
	/**
	 * Enable a user
	 * @param userId User id
	 */
	void enable(Integer userId);
	
	/**
	 * Disable a user
	 * @param userId User id
	 */
	void disable(Integer userId);
	
}
