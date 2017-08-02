package com.disciples.iam.service.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.NumberUtils;
import org.springframework.util.StringUtils;

import com.disciples.iam.domain.User;
import com.disciples.iam.service.UserManager;

public class DefaultUserManager implements UserManager, UserDetailsService, RowMapper<User> {
	
	private static final Logger LOG = LoggerFactory.getLogger(DefaultUserManager.class);
	
	private static final String COMMON_COLUMNS = "u.id, u.username, u.roles, u.name, u.email, u.phone, u.create_time, u.enabled";
	private static final String SELECT_BY_ID = "select " + COMMON_COLUMNS + ", u.password from iam_user u where u.id = ?";
	private static final String SELECT_BY_USERNAME = "select " + COMMON_COLUMNS + ", u.password from iam_user u where u.username = ?";
	private static final String FIND_ALL_ROLES_BY_USERNAME = "select g.roles from iam_user u, iam_user_group m, iam_group g"
			+ " where u.id = m.user_id and m.group_id = g.id and u.username = ?";
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
	
	private static final String ROLE_USER = "ROLE_USER";
	private static final String DEFAULT_RAW_PASSWORD = "123456";
    private static final Md5PasswordEncoder MD5_ENCODER = new Md5PasswordEncoder();
    
	private JdbcTemplate jdbcTemplate;
	
	public DefaultUserManager(DataSource dataSource) {
		Assert.notNull(dataSource, "DataSource is required.");
		this.jdbcTemplate = new JdbcTemplate(dataSource);
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
		List<User> users = jdbcTemplate.query(SELECT_BY_USERNAME, this, username);
		if (users.size() > 0) {
			return users.get(0);
		}
		return null;
	}
	
	@Override
	public User findOne(Integer id) {
		List<User> users = jdbcTemplate.query(SELECT_BY_ID, this, id);
		if (users.size() > 0) {
			return users.get(0);
		}
		return null;
	}
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = findOneByUsername(username);
		if (user == null) {
            throw new UsernameNotFoundException(String.format("用户 '%s' 不存在", username));
        }
		List<String> groupRoles = jdbcTemplate.queryForList(FIND_ALL_ROLES_BY_USERNAME, String.class, username);
        Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority(ROLE_USER));
        for (String groupRole : groupRoles) {
        	authorities.addAll(getAuthorities(groupRole));
        }
        authorities.addAll(getAuthorities(user.getRoles()));
        user.setAuthorities(Collections.unmodifiableSet(authorities));
        return user;
	}

	private List<? extends GrantedAuthority> getAuthorities(String roles) {
    	String[] roleArray = StringUtils.commaDelimitedListToStringArray(roles);
    	if (roleArray.length > 0) {
    		List<SimpleGrantedAuthority> authorties = new ArrayList<SimpleGrantedAuthority>();
        	for (String role : roleArray) {
        		authorties.add(new SimpleGrantedAuthority(role));
        	}
        	return authorties;
    	}
    	return Collections.emptyList();
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
		Long count = jdbcTemplate.queryForObject(String.format(sqlFormat.toString(), "count(u.id)"), Long.class, args.toArray());
		if (count == null || count == 0) {
			return new PageImpl<User>(Collections.<User>emptyList());
		}
		sqlFormat.append(" limit ?,?");
		args.add(pageable.getPageNumber() * pageable.getPageSize());
		args.add(pageable.getPageSize());
		List<User> content = jdbcTemplate.query(String.format(sqlFormat.toString(), COMMON_COLUMNS), this, args.toArray());
		return new PageImpl<User>(content, pageable, count);
	}
	
	@Override
	public List<User> find(Integer groupId) {
		Assert.notNull(groupId, "用户组标识不能为空");
		return jdbcTemplate.query(String.format(FIND_BY_GROUP, COMMON_COLUMNS), this, groupId);
	}
	
	@Override
	public boolean exists(String username) {
		List<Integer> userIds = jdbcTemplate.queryForList(EXIST, Integer.class, username);
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
	        String password = StringUtils.hasText(user.getPassword()) ?  user.getPassword(): DEFAULT_RAW_PASSWORD;
	        user.setCreateTime(new Date());
	        
	        Object[] params = new Object[] {user.getUsername(), MD5_ENCODER.encodePassword(password, null), user.getName(), 
	        		user.getEmail(), user.getPhone(), user.getRoles(), user.getCreateTime()};
	        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
	        
	        int[] sqlTypes = new int[] {Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.TIMESTAMP};
	        PreparedStatementCreatorFactory factory = new PreparedStatementCreatorFactory(INSERT, sqlTypes);
	        factory.setGeneratedKeysColumnNames("id");
	        jdbcTemplate.update(factory.newPreparedStatementCreator(params), keyHolder);
	        
	        user.setId(NumberUtils.convertNumberToTargetClass(keyHolder.getKey(), Integer.class));
	        user.setPassword(null);
	        return user;
		}
		//执行更新
		jdbcTemplate.update(UPDATE, user.getName(), user.getEmail(), user.getPhone(), user.getRoles(), user.getId());
        return user;
	}
	
	@Override
//	@Transactional
	public List<User> batchInsert(List<User> users, Integer groupId) {
		Assert.notEmpty(users, "用户数据列表不能为空");
		Set<String> usernames = new HashSet<String>();
		for (User user : users) {
			usernames.add(user.getUsername());
		}
		String placeholders = StringUtils.collectionToCommaDelimitedString(Collections.nCopies(usernames.size(), "?"));
		String findExistUsernamesSql = String.format("select username from iam_user where username in (%s)", placeholders);
		List<String> existUsernames = jdbcTemplate.queryForList(findExistUsernamesSql, String.class, usernames.toArray());
		Set<String> existUsernameSet = new HashSet<String>(existUsernames);
		
		List<String> insertPlaceholders = new ArrayList<String>();
		List<Object> args = new ArrayList<Object>();
		List<User> savedUsers = new ArrayList<User>();
		for (User user : users) {
			if (!existUsernameSet.contains(user.getUsername())) {
				insertPlaceholders.add("(?,?,?,?,?)");
				args.add(user.getUsername());
				args.add(MD5_ENCODER.encodePassword(StringUtils.hasText(user.getPassword()) ?  user.getPassword(): DEFAULT_RAW_PASSWORD, null));
				args.add(user.getName());
				args.add(user.getEmail());
				args.add(user.getPhone());
				savedUsers.add(user);
			}
		}
		if (insertPlaceholders.size() == 0) {
			return Collections.emptyList();
		}
		StringBuilder insertSql = new StringBuilder("insert into iam_user (username, password, name, email, phone) values ");
		insertSql.append(StringUtils.collectionToCommaDelimitedString(insertPlaceholders));
		int[] sqlTypes = new int[args.size()];
		Arrays.fill(sqlTypes, Types.VARCHAR);
		
		PreparedStatementCreatorFactory factory = new PreparedStatementCreatorFactory(insertSql.toString(), sqlTypes);
		factory.setGeneratedKeysColumnNames("id");
		GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(factory.newPreparedStatementCreator(args.toArray()), keyHolder);
		
		List<Map<String, Object>> keyList = keyHolder.getKeyList();
		if (keyList.size() == savedUsers.size()) {
			for (int i = 0; i < keyList.size(); i++) {
				Object genKey = keyList.get(i).get("GENERATED_KEY");
				Integer id = genKey instanceof Integer ? (Integer)genKey : Integer.parseInt(genKey.toString());
				savedUsers.get(i).setId(id);
			}
			if (groupId != null && jdbcTemplate.queryForObject(COUNT_GROUP_BY_ID, Long.class, groupId) > 0) {
				args = new ArrayList<Object>();
				for (User user : savedUsers) {
					args.add(user.getId());
					args.add(groupId);
				}
				insertSql = new StringBuilder("insert into iam_user_group (user_id, group_id) values ");
				insertSql.append(StringUtils.collectionToCommaDelimitedString(Collections.nCopies(savedUsers.size(), "(?,?)")));
				jdbcTemplate.update(insertSql.toString(), args.toArray());
			}
		} else {
			LOG.warn("Batch insert user illegal state: keyList.size() != savedUsers.size()");
		}
		return savedUsers;
	}
	
	@Override
//	@Transactional TODO:
	public void delete(Integer userId) {
		jdbcTemplate.update(DELETE_USER_GROUPS_BY_USER_ID, userId);
		jdbcTemplate.update(DELETE_BY_ID, userId);
	}

	@Override
//	@Transactional
	public void delete(List<Integer> userIds) {
		if (!CollectionUtils.isEmpty(userIds)) {
			String placeholders = StringUtils.collectionToCommaDelimitedString(Collections.nCopies(userIds.size(), "?"));
			jdbcTemplate.update(String.format(DELETE_USER_GROUPS_BY_USER_ID_IN, placeholders), userIds.toArray());
			jdbcTemplate.update(String.format(DELETE_BY_ID_IN, placeholders), userIds.toArray());
		}
	}
	
	@Override
	public void changePassword(String oldPassword, String newPassword) throws AuthenticationException {
		if (!StringUtils.hasText(oldPassword) || !StringUtils.hasText(newPassword)) {
            throw new IllegalArgumentException("密码不能为空");
        }
		Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
		if (currentUser == null) {
			throw new AccessDeniedException("请认证后操作");
		}
		if (!((UserDetails)currentUser.getPrincipal()).getPassword().equals(MD5_ENCODER.encodePassword(oldPassword, null))) {
            throw new IllegalArgumentException("旧密码错误.");
        }
		jdbcTemplate.update(CHANGE_PASSWORD, MD5_ENCODER.encodePassword(newPassword, null), currentUser.getName());
	}

	@Override
	public List<Integer> groupIds(Integer userId) {
		return jdbcTemplate.queryForList("select m.group_id from iam_user u, iam_user_group m where u.id = m.user_id and u.id = ?",  Integer.class, userId);
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
    	return jdbcTemplate.update(batch.toString(), args.toArray());
	}
	
	@Override
//	@Transactional
	public int updateGroups(Integer userId, List<Integer> groupIds) {
		if (CollectionUtils.isEmpty(groupIds)) {
			return jdbcTemplate.update("delete from iam_user_group where user_id = ?", userId);
        }
        List<Integer> addList = new ArrayList<Integer>();
        List<Integer> currentGroupIds = jdbcTemplate.queryForList("select group_id from iam_user_group where user_id = ?", Integer.class, userId);
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
        	jdbcTemplate.update(deleteSql.toString(), args.toArray());
        }
        if (!addList.isEmpty()) {
        	updateCount += batchInsertMembership(userId, addList);
        }
		return updateCount;
	}
	
	@Override
	public void resetPassword(Integer userId) {
		jdbcTemplate.update(CHANGE_PASSWORD, MD5_ENCODER.encodePassword(DEFAULT_RAW_PASSWORD, null), userId);
	}
	
	@Override
	public void enable(Integer userId) {
		jdbcTemplate.update("update iam_user set enabled = 1 where id = ?", userId);
	}

	@Override
	public void disable(Integer userId) {
		jdbcTemplate.update("update iam_user set enabled = 0 where id = ?", userId);
	}

}
