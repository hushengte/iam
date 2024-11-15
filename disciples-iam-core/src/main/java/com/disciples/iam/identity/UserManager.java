package com.disciples.iam.identity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.disciples.iam.identity.cmd.ChangeUserPassword;
import com.disciples.iam.identity.cmd.RegisterUser;
import com.disciples.iam.identity.cmd.SaveUser;
import com.disciples.iam.identity.cmd.UpdateUser;
import com.disciples.iam.identity.cmd.UpdateUserGroups;
import com.disciples.iam.identity.domain.User;
import com.disciples.iam.identity.domain.UserFactory;
import com.disciples.iam.identity.domain.UserNotExistsException;
import com.disciples.iam.identity.domain.Users;
import com.disciples.iam.util.Md5PasswordEncoder;

/**
 * User management Service
 */
@Service
public class UserManager {
	
	static final Supplier<String> USER_ID_IS_REQUIRED = () -> "userId is required.";
	static final String DEFAULT_RAW_PASSWORD = "000000";
	
	private final JdbcOperations jdbcOperations;
	private final Users users;
	private final UserFactory userFactory;
	private final PasswordEncoder passwordEncoder;
	
	@Autowired
	public UserManager(JdbcOperations jdbcOperations, Users users, PasswordEncoder passwordEncoder) {
		Assert.notNull(jdbcOperations, () -> "JdbcOperations is required.");
		Assert.notNull(users, () -> "User repository is required.");
		
		this.jdbcOperations = jdbcOperations;
		this.users = users;
		this.userFactory = new UserFactory(users);
		this.passwordEncoder = passwordEncoder != null ? passwordEncoder : new Md5PasswordEncoder();
	}
	
	@Transactional
	public User save(SaveUser cmd) {
		if (cmd.isUpdate()) {
    		return update(cmd.buildUpdateUserCmd());
    	}
        return register(cmd.buildRegisterUserCmd());
	}
	
	@Transactional
	public User register(RegisterUser cmd) {
		String username = cmd.getUsername();
		Assert.hasText(username, "username is required.");
		
		String rawPassword = cmd.getPassword();
		String password = passwordEncoder.encode(StringUtils.hasText(rawPassword) ? rawPassword : DEFAULT_RAW_PASSWORD);
		User user = userFactory.build(username, password, 
				cmd.getNickname(), cmd.getEmail(), cmd.getPhone());
		return user;
	}
	
	@Transactional
	public User update(UpdateUser cmd) {
		User user = existingUser(cmd.getId());
		user.update(cmd.getNickname(), cmd.getEmail(), cmd.getPhone());
		return users.save(user);
	}
	
	private User existingUser(Long userId) {
		return users.findById(userId)
						.orElseThrow(() -> new UserNotExistsException(userId));
	}
	
	private User existingUser(String username) {
		return users.findByUsername(username)
						.orElseThrow(() -> new UserNotExistsException(username));
	}

	@Transactional
	public boolean changePassword(ChangeUserPassword cmd) {
		User user = existingUser(cmd.getUsername());
		String currentPassword = passwordEncoder.encode(cmd.getCurrentPassword());
		String newPassword = passwordEncoder.encode(cmd.getNewPassword());
		user.changePassword(currentPassword, newPassword);
		return users.savePassword(user) > 0;
	}
	
	@Transactional
	public boolean resetPassword(Long userId) {
		Assert.notNull(userId, USER_ID_IS_REQUIRED);
		
		String newPassword = passwordEncoder.encode(DEFAULT_RAW_PASSWORD);
		User user = new User(userId).resetPassword(newPassword);
		return users.savePassword(user) > 0;
	}
	
	@Transactional
	public boolean disable(Long userId) {
		Assert.notNull(userId, USER_ID_IS_REQUIRED);
		
		User user = new User(userId).disable();
		return users.saveStatus(user) > 0;
	}
	
	@Transactional
	public boolean enable(Long userId) {
		Assert.notNull(userId, USER_ID_IS_REQUIRED);
		
		User user = new User(userId).enable();
		return users.saveStatus(user) > 0;
	}
	
	@Transactional
	public void delete(Long userId) {
		Assert.notNull(userId, USER_ID_IS_REQUIRED);
		
		delete(Arrays.asList(userId));
	}
	
	@Transactional
	public void delete(List<Long> userIds) {
		Assert.notEmpty(userIds, () -> "userIds is required.");
		
		Iterable<User> deleteUsers = users.findAllById(userIds);
		for (User user : deleteUsers) {
			users.delete(user.delete());
		}
	}
	
	@Transactional
	public List<Long> updateGroups(UpdateUserGroups cmd) {
		Long userId = cmd.getUserId();
		List<Long> groupIds = cmd.getGroupIds();
		doUpdateGroups(userId, groupIds);
		return groupIds;
	}
	
	private int doUpdateGroups(Long userId, List<Long> groupIds) {
		if (CollectionUtils.isEmpty(groupIds)) {
			return jdbcOperations.update("delete from iam_user_group where user_id = ?", userId);
        }
        List<Long> addList = new ArrayList<>();
        List<Long> currentGroupIds = jdbcOperations.queryForList("select group_id from iam_user_group where user_id = ?", Long.class, userId);
        if (CollectionUtils.isEmpty(currentGroupIds)) {
        	return batchInsertMembership(userId, groupIds);
        }
        Set<Long> groupIdSet = new HashSet<>();
        groupIdSet.addAll(groupIds);
        Set<Long> existGroupIdSet = new HashSet<>();
        List<Long> deleteList = new ArrayList<>();
        for (Long groupId : currentGroupIds) {
        	existGroupIdSet.add(groupId);
        	if (!groupIdSet.contains(groupId)) {
        		deleteList.add(groupId);
        	}
        }
        for (Long groupId : groupIdSet) {
        	if (!existGroupIdSet.contains(groupId)) {
        		addList.add(groupId);
        	}
        }
        int updateCount = deleteList.size();
        if (updateCount > 0) {
        	String placeholders = StringUtils.collectionToDelimitedString(Collections.nCopies(updateCount, "?"), ",");
        	String deleteSql = String.format("delete from iam_user_group where user_id = ? and group_id in (%s)", placeholders);
        	List<Object> args = new ArrayList<Object>();
        	args.add(userId);
        	args.addAll(deleteList);
        	jdbcOperations.update(deleteSql.toString(), args.toArray());
        }
        if (!addList.isEmpty()) {
        	updateCount += batchInsertMembership(userId, addList);
        }   
		return updateCount;
	}
	
	private int batchInsertMembership(Long userId, List<Long> groupIds) {
		StringBuilder batch = new StringBuilder("insert into iam_user_group (user_id, group_id) values ");
    	List<Object> args = new ArrayList<Object>();
    	for (Long groupId : groupIds) {
    		batch.append("(?,?),");
    		args.add(userId);
    		args.add(groupId);
    	}
    	batch.deleteCharAt(batch.length() - 1);
    	return jdbcOperations.update(batch.toString(), args.toArray());
	}

}
