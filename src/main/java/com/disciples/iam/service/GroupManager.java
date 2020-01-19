package com.disciples.iam.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import com.disciples.iam.domain.Group;

/**
 * 用户组管理服务
 */
public interface GroupManager {

    /**
     * 根据ID查找用户组
     * @param groupId 用户组ID
     * @return 用户组
     */
    Group findById(Integer groupId);
    
    /**
     * 查找用户组分页
     * @param page 页码，从0开始
     * @param size 页记录数
     * @param name 用户组名称关键字 
     * @return 用户组分页数据
     */
	Page<Group> find(int page, int size, String name);
	
	/**
	 * 查找指定用户所在的用户组
	 * @param userId 用户ID
	 * @return 用户所属的用户组列表
	 */
	List<Group> find(Integer userId);
	
	/**
	 * 查找指定用户所在的用户组
	 * @param username 用户名
	 * @return 用户所属的用户组列表
	 */
	List<Group> find(String username);
	
	/**
	 * 所有用户组
	 * @return 用户组列表
	 */
	List<Group> findAll();

	/**
	 * 所有用户组ID和名称键值对列表
	 * @return 用户组键值对列表
	 */
	List<Map<String, Object>> keyValues();
	
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

	/**
	 * 删除指定用户组中的用户
	 * @param groupId 用户组ID
	 * @param userIds 用户ID列表
	 */
	void removeUser(Integer groupId, List<Integer> userIds);

}
