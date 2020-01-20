package com.disciples.iam.service.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.NumberUtils;
import org.springframework.util.StringUtils;

import com.disciples.iam.domain.User;
import com.disciples.iam.service.UserManager;

public class DefaultUserManager implements UserManager, RowMapper<User> {
	
	private static final Logger LOG = LoggerFactory.getLogger(DefaultUserManager.class);
	
	private static final String COMMON_COLUMNS = "u.id, u.username, u.roles, u.name, u.email, u.phone, u.create_time, u.enabled";
	private static final String SELECT_BY_ID = "select " + COMMON_COLUMNS + ", u.password from iam_user u where u.id = ?";
	private static final String SELECT_BY_USERNAME = "select " + COMMON_COLUMNS + ", u.password from iam_user u where u.username = ?";
	private static final String FIND_ALL_ROLES_BY_ID = "select g.roles from iam_user u, iam_user_group m, iam_group g"
			+ " where u.id = m.user_id and m.group_id = g.id and u.id = ?";
	private static final String FIND_BY_GROUP = "select %s from iam_user u left join iam_user_group m on u.id = m.user_id where m.group_id = ?";
	private static final String FIND_BY = "select %s from iam_user u where 1=1";
	private static final String EXIST = "select id from iam_user where username = ?";
	private static final String INSERT = "insert into iam_user (username, password, name, email, phone, roles, create_time) values (?,?,?,?,?,?,?)";
	private static final String UPDATE = "update iam_user set name = ?, email = ?, phone = ?, roles = ? where id = ?";
	private static final String CHANGE_PASSWORD = "update iam_user set password = ? where id = ?";
	private static final String DELETE_BY_ID = "delete from iam_user where id = ?";
	private static final String DELETE_BY_ID_IN = "delete from iam_user where id in (%s)";
	private static final String DELETE_USER_GROUPS_BY_USER_ID = "delete from iam_user_group where user_id = ?";
	private static final String DELETE_USER_GROUPS_BY_USER_ID_IN = "delete from iam_user_group where user_id in (%s)";
	private static final String COUNT_GROUP_BY_ID = "select count(id) from iam_group where id = ?";
	
	public static final String ID_NOT_NULL = "用户标识不能为空";
	
	public static final String DEFAULT_RAW_PASSWORD = "123456";
    
	private JdbcOperations jdbcOperations;
	private PasswordEncoder passwordEncoder;
	private String defaultPassword;
	
	public DefaultUserManager(JdbcOperations jdbcOperations) {
		this(jdbcOperations, new Md5PasswordEncoder(), DEFAULT_RAW_PASSWORD);
	}
	
	public DefaultUserManager(JdbcOperations jdbcOperations, PasswordEncoder passwordEncoder) {
        this(jdbcOperations, passwordEncoder, DEFAULT_RAW_PASSWORD);
    }
	
	public DefaultUserManager(JdbcOperations jdbcOperations, PasswordEncoder passwordEncoder, 
	        String defaultPassword) {
        Assert.notNull(jdbcOperations, "JdbcOperations is required.");
        this.jdbcOperations = jdbcOperations;
        setPasswordEncoder(passwordEncoder);
        setDefaultPassword(defaultPassword);
    }
	
	private static class Md5PasswordEncoder implements PasswordEncoder {

	    @Override
	    public String encode(CharSequence rawPassword) {
	        return DigestUtils.md5Hex((String)rawPassword);
	    }

	    @Override
	    public boolean matches(CharSequence rawPassword, String encodedPassword) {
	        return encode(rawPassword).equals(encodedPassword);
	    }
	}
	
	public PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }

    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        Assert.notNull(passwordEncoder, "PasswordEncoder is required.");
        this.passwordEncoder = passwordEncoder;
    }

    public String getDefaultPassword() {
        return defaultPassword;
    }

    public void setDefaultPassword(String defaultPassword) {
        Assert.hasText(defaultPassword, "Default Password must not be empty.");
        this.defaultPassword = defaultPassword;
    }

    @Override
	public User mapRow(ResultSet rs, int rowNum) throws SQLException {
		User user = new User(rs.getInt(1));
		user.setUsername(rs.getString(2));
		user.setRoles(rs.getString(3));
		user.setName(rs.getString(4));
		user.setEmail(rs.getString(5));
		user.setPhone(rs.getString(6));
		user.setCreateTime(rs.getTimestamp(7));
		user.setEnabled(rs.getInt(8) > 0);
		if (rs.getMetaData().getColumnCount() == 9) {
			user.setPassword(rs.getString(9));
		}
		return user;
	}
	
	@Override
	public User findOneByUsername(String username) {
		List<User> users = jdbcOperations.query(SELECT_BY_USERNAME, this, username);
		if (users.size() > 0) {
			return users.get(0);
		}
		return null;
	}
	
	@Override
	public User findOne(Integer id) {
		List<User> users = jdbcOperations.query(SELECT_BY_ID, this, id);
		if (users.size() > 0) {
			return users.get(0);
		}
		return null;
	}
	
	@Override
	public Page<User> find(int page, int size, Integer groupId, String keyword) {
		Pageable pageable = new PageRequest(page, size);
		List<Object> args = new ArrayList<Object>();
		StringBuilder sqlFormat = new StringBuilder();
		if (groupId != null) {
			sqlFormat.append(FIND_BY_GROUP);
			args.add(groupId);
		} else {
			sqlFormat.append(FIND_BY);
		}
		if (StringUtils.hasText(keyword)) {
			sqlFormat.append(" and (u.username like ? or u.name like ?)");
			String param = "%" + keyword.trim() + "%";
			args.add(param);
			args.add(param);
		}
		Long count = jdbcOperations.queryForObject(String.format(sqlFormat.toString(), "count(u.id)"), Long.class, args.toArray());
		if (count == null || count == 0) {
			return new PageImpl<User>(Collections.<User>emptyList());
		}
		sqlFormat.append(" limit ?,?");
		args.add(pageable.getPageNumber() * pageable.getPageSize());
		args.add(pageable.getPageSize());
		List<User> content = jdbcOperations.query(String.format(sqlFormat.toString(), COMMON_COLUMNS), this, args.toArray());
		return new PageImpl<User>(content, pageable, count);
	}
	
	@Override
	public List<User> find(Integer groupId) {
		Assert.notNull(groupId, DefaultGroupManager.ID_NOT_NULL);
		return jdbcOperations.query(String.format(FIND_BY_GROUP, COMMON_COLUMNS), this, groupId);
	}
	
	@Override
	public boolean exists(String username) {
		List<Integer> userIds = jdbcOperations.queryForList(EXIST, Integer.class, username);
		return userIds.size() > 0;
	}
	
	@Override
//	@Transactional
	public User save(User user) {
		Assert.notNull(user, "用户数据不能为空");
		if (user.getId() == null) {
			if (exists(user.getUsername())) {
	        	throw new DuplicateKeyException(String.format("用户名'%s'已被使用", user.getUsername()));
	        }
	        String password = StringUtils.hasText(user.getPassword()) ?  user.getPassword(): defaultPassword;
	        user.setCreateTime(new Date());
	        
	        Object[] params = new Object[] {user.getUsername(), passwordEncoder.encode(password), user.getName(), 
	        		user.getEmail(), user.getPhone(), user.getRoles(), user.getCreateTime()};
	        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
	        
	        int[] sqlTypes = new int[] {Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.TIMESTAMP};
	        PreparedStatementCreatorFactory factory = new PreparedStatementCreatorFactory(INSERT, sqlTypes);
	        factory.setGeneratedKeysColumnNames("id");
	        jdbcOperations.update(factory.newPreparedStatementCreator(params), keyHolder);
	        
	        user.setId(NumberUtils.convertNumberToTargetClass(keyHolder.getKey(), Integer.class));
	        user.setPassword(null);
	        return user;
		}
		//执行更新
		jdbcOperations.update(UPDATE, user.getName(), user.getEmail(), user.getPhone(), user.getRoles(), user.getId());
        return user;
	}
	
	@Override
//	@Transactional
	public List<User> batchInsert(List<User> users, Integer groupId) {
		Assert.notEmpty(users, "用户数据列表不能为空");
		// check group exists
		if (groupId != null) {
		    if (jdbcOperations.queryForObject(COUNT_GROUP_BY_ID, Long.class, groupId) == 0) {
		        throw new DataIntegrityViolationException("用户组不存在，id=" + groupId);
		    }
		}
		// save new user only
		List<User> savedUsers = new ArrayList<User>();
		for (User user : users) {
		    try {
                savedUsers.add(save(user));
            } catch (DuplicateKeyException e) {
                LOG.warn(e.getMessage(), e);
            }
		}
		if (savedUsers.size() > 0 && groupId != null) {
		    List<Object> args = new ArrayList<>();
            for (User user : savedUsers) {
                args.add(user.getId());
                args.add(groupId);
            }
            StringBuilder insertSql = new StringBuilder("insert into iam_user_group (user_id, group_id) values ");
            List<String> placeholders = Collections.nCopies(savedUsers.size(), "(?,?)");
            insertSql.append(StringUtils.collectionToCommaDelimitedString(placeholders));
            jdbcOperations.update(insertSql.toString(), args.toArray());
        }
		return savedUsers;
	}
	
	@Override
//	@Transactional TODO:
	public void delete(Integer userId) {
	    Assert.notNull(userId, ID_NOT_NULL);
		jdbcOperations.update(DELETE_USER_GROUPS_BY_USER_ID, userId);
		jdbcOperations.update(DELETE_BY_ID, userId);
	}

	@Override
//	@Transactional
	public void delete(List<Integer> userIds) {
		if (!CollectionUtils.isEmpty(userIds)) {
			String placeholders = StringUtils.collectionToCommaDelimitedString(Collections.nCopies(userIds.size(), "?"));
			jdbcOperations.update(String.format(DELETE_USER_GROUPS_BY_USER_ID_IN, placeholders), userIds.toArray());
			jdbcOperations.update(String.format(DELETE_BY_ID_IN, placeholders), userIds.toArray());
		}
	}
	
	@Override
	public void changePassword(Integer userId, String oldPassword, String newPassword) throws IllegalArgumentException {
	    Assert.notNull(userId, ID_NOT_NULL);
	    Assert.isTrue(StringUtils.hasText(oldPassword) && StringUtils.hasText(newPassword), "密码不能为空");
	    
		User currentUser = findOne(userId);
		if (currentUser == null) {
			throw new IllegalArgumentException("不存在用户：id=" + userId);
		}
		if (!currentUser.getPassword().equals(passwordEncoder.encode(oldPassword))) {
            throw new IllegalArgumentException("旧密码错误.");
        }
		jdbcOperations.update(CHANGE_PASSWORD, passwordEncoder.encode(newPassword), currentUser.getId());
	}

	@Override
	public List<Integer> groupIds(Integer userId) {
		return jdbcOperations.queryForList("select m.group_id from iam_user u, iam_user_group m where u.id = m.user_id and u.id = ?",  Integer.class, userId);
	}
	
	@Override
	public List<String> getGroupRoles(Integer userId) {
	    Assert.notNull(userId, ID_NOT_NULL);
	    return jdbcOperations.queryForList(FIND_ALL_ROLES_BY_ID, String.class, userId);
	}
	
	private int batchInsertMembership(Integer userId, List<Integer> groupIds) {
		StringBuilder batch = new StringBuilder("insert into iam_user_group (user_id, group_id) values ");
    	List<Object> args = new ArrayList<Object>();
    	for (Integer groupId : groupIds) {
    		batch.append("(?,?),");
    		args.add(userId);
    		args.add(groupId);
    	}
    	batch.deleteCharAt(batch.length() - 1);
    	return jdbcOperations.update(batch.toString(), args.toArray());
	}
	
	@Override
//	@Transactional
	public int updateGroups(Integer userId, List<Integer> groupIds) {
		if (CollectionUtils.isEmpty(groupIds)) {
			return jdbcOperations.update("delete from iam_user_group where user_id = ?", userId);
        }
        List<Integer> addList = new ArrayList<Integer>();
        List<Integer> currentGroupIds = jdbcOperations.queryForList("select group_id from iam_user_group where user_id = ?", Integer.class, userId);
        if (CollectionUtils.isEmpty(currentGroupIds)) {
        	return batchInsertMembership(userId, groupIds);
        }
        Set<Integer> groupIdSet = new HashSet<Integer>();
        groupIdSet.addAll(groupIds);
        Set<Integer> existGroupIdSet = new HashSet<Integer>();
        List<Integer> deleteList = new ArrayList<Integer>();
        for (Integer groupId : currentGroupIds) {
        	existGroupIdSet.add(groupId);
        	if (!groupIdSet.contains(groupId)) {
        		deleteList.add(groupId);
        	}
        }
        for (Integer groupId : groupIdSet) {
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
	
	@Override
	public void resetPassword(Integer userId) {
		jdbcOperations.update(CHANGE_PASSWORD, passwordEncoder.encode(defaultPassword), userId);
	}
	
	@Override
	public void enable(Integer userId) {
		jdbcOperations.update("update iam_user set enabled = 1 where id = ?", userId);
	}

	@Override
	public void disable(Integer userId) {
		jdbcOperations.update("update iam_user set enabled = 0 where id = ?", userId);
	}

}
