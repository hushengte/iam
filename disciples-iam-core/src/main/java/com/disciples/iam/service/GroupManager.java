package com.disciples.iam.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import com.disciples.iam.domain.Group;

/**
 * Group management service
 */
public interface GroupManager {

    /**
     * Find group by ID
     * @param groupId Group's ID
     * @return A group
     */
    Group findById(Integer groupId);
    
    /**
     * Find a page of group data
     * @param page Page number, start from 0
     * @param size Page size
     * @param name Search keyword of group name
     * @return A page of group
     */
	Page<Group> find(int page, int size, String name);
	
	/**
	 * Find groups of the user by user's id
	 * @param userId User's ID
	 * @return List of group that the user belongs to
	 */
	List<Group> find(Integer userId);
	
	/**
	 * Find groups of the user by username
	 * @param username Username
	 * @return List of group that the user belongs to
	 */
	List<Group> find(String username);
	
	/**
	 * Find all groups
	 * @return List of group
	 */
	List<Group> findAll();

	/**
	 * Find the key-values pairs of all groups: "key" is group id, "value" is group name
	 * @return List of key-values pairs
	 */
	List<Map<String, Object>> keyValues();
	
	/**
	 * Save or update group. If group id is specified, it will execute update, 
	 * otherwise execute save operation.
	 * @param group Group data
	 * @return The saved group
	 */
	Group save(Group group);
	
	/**
	 * Delete group by id. If there are users in this group, delete will be failed.
	 * @param groupId Group id
	 */
	void delete(Integer groupId);

	/**
	 * Remove users from group
	 * @param groupId Group id
	 * @param userIds List of user id
	 */
	void removeUser(Integer groupId, List<Integer> userIds);

}
