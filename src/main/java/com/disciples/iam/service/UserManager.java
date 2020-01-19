package com.disciples.iam.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.disciples.iam.domain.User;

/**
 * 用户管理服务
 */
public interface UserManager {

    /**
     * 用户分页
     * @param page 页码，从0开始
     * @param size 页记录数
     * @param groupId 用户组ID
     * @param keyword 用户名或用户姓名关键字
     * @return 用户分页数据
     */
	Page<User> find(int page, int size, Integer groupId, String keyword);
	
	/**
	 * 查找指定用户组的用户
	 * @param groupId 用户组ID
	 * @return 用户列表
	 */
	List<User> find(Integer groupId);
	
	/**
	 * 根据用户名查找用户
	 * @param username 用户名
	 * @return 用户
	 */
	User findOneByUsername(String username);
	
	/**
	 * 根据ID查找用户
	 * @param id 用户ID
	 * @return 用户
	 */
	User findOne(Integer id);
	
	/**
	 * 判断指定用户名的用户是否存在
	 * @param username 用户名
	 * @return 存在返回 true, 否则返回 false
	 */
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
	 * @return 添加的用户列表
	 */
	List<User> batchInsert(List<User> users, Integer groupId);
	
	/**
	 * 删除指定用户
	 * @param userId 用户ID
	 */
	void delete(Integer userId);
	
	/**
	 * 删除用户列表
	 * @param userIds 用户ID列表
	 */
	void delete(List<Integer> userIds);
	
	/**
	 * 查找指定用户的用户组ID
	 * @param userId 用户ID
	 * @return 用户组ID列表
	 */
	List<Integer> groupIds(Integer userId);
	
	/**
	 * 更新指定用户的用户组
	 * @param userId 用户ID
	 * @param groupIds 用户组ID列表
	 * @return 影响的记录数
	 */
	int updateGroups(Integer userId, List<Integer> groupIds);
	
	/**
	 * 修改用户密码
	 * @param userId 用户ID
	 * @param oldPassword 原密码
	 * @param newPassword 新密码
	 */
	void changePassword(Integer userId, String oldPassword, String newPassword);
	
	/**
	 * 重置用户密码
	 * @param userId 用户ID
	 */
	void resetPassword(Integer userId);
	
	/**
	 * 用户启用
	 * @param userId 用户ID
	 */
	void enable(Integer userId);
	
	/**
	 * 用户禁用
	 * @param userId 用户ID
	 */
	void disable(Integer userId);
	
}
