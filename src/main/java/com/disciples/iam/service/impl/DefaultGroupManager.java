package com.disciples.iam.service.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.util.Assert;
import org.springframework.util.NumberUtils;
import org.springframework.util.StringUtils;

import com.disciples.feed.KeyValue;
import com.disciples.feed.manage.ManageException;
import com.disciples.iam.domain.Group;
import com.disciples.iam.domain.User;
import com.disciples.iam.service.GroupManager;

public class DefaultGroupManager implements GroupManager, RowMapper<Group> {

	private static final String COUNT = "select count(id) from iam_group";
	private static final String FIND = "select id, name, roles, create_time from iam_group";
	private static final String FIND_ID_NAMES = "select id, name from iam_group";
	private static final String INSERT = "insert into iam_group (name, roles, create_time) values (?,?,?)";
	private static final String UPDATE = "update iam_group set name = ?, roles = ? where id = ?";
	private static final String DELETE = "delete from iam_group where id = ?";
	
	private static final String COUNT_USERS = "select count(user_id) from iam_user_group where group_id = ?";
	private static final String FIND_USERS = "select u.id, u.username, u.roles, u.name, u.email, u.phone, u.create_time, u.enabled from iam_user u"
			+ " left join iam_user_group m on u.id = m.user_id where m.group_id = ?";
	private static final String DELETE_USER_GROUPS = "delete from iam_user_group where group_id = ? and user_id in (%s)";
	
	private JdbcTemplate jdbcTemplate;
	
	public DefaultGroupManager(DataSource dataSource) {
		Assert.notNull(dataSource, "DataSource is required.");
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Override
	public Group mapRow(ResultSet rs, int rowNum) throws SQLException {
		Group group = new Group(rs.getInt(1));
		group.setName(rs.getString(2));
		group.setRoles(rs.getString(3));
		group.setCreateTime(rs.getTimestamp(4));
		return group;
	}
	
	@Override
	public Page<Group> find(int page, int size, String keyword) {
		Pageable pageable = new PageRequest(page, size);
		
		StringBuilder condition = new StringBuilder();
		List<Object> args = new ArrayList<Object>(4);
		if (StringUtils.hasText(keyword)) {
			condition.append(" where name like ?");
			args.add("%" + keyword.trim() + "%");
		}
		Long count = jdbcTemplate.queryForObject(COUNT + condition.toString(), Long.class, args.toArray());
		if (count == null || count == 0) {
			return new PageImpl<Group>(Collections.<Group>emptyList());
		}
		condition.append(" limit ?,?");
		args.add(pageable.getPageNumber() * pageable.getPageSize());
		args.add(pageable.getPageSize());
		return new PageImpl<Group>(jdbcTemplate.query(FIND + condition.toString(), this, args.toArray()), pageable, count);
	}
	
	@Override
	public List<Group> findAll() {
		return jdbcTemplate.query(FIND, this);
	}
	
	@Override
	public List<KeyValue> keyValues() {
		return jdbcTemplate.query(FIND_ID_NAMES, new RowMapper<KeyValue>() {
			@Override
			public KeyValue mapRow(ResultSet rs, int rowNum) throws SQLException {
				return new KeyValue(rs.getInt(1), rs.getString(2));
			}
		});
	}
	
	@Override
	public Group save(Group group) {
		Assert.notNull(group, "用户组数据不能为空");
		Integer id = group.getId();
		if (id == null) {
			group.setCreateTime(new Date());
			
			PreparedStatementCreatorFactory factory = new PreparedStatementCreatorFactory(INSERT, Types.VARCHAR, Types.VARCHAR, Types.TIMESTAMP);
			factory.setGeneratedKeysColumnNames("id");
			GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
	        jdbcTemplate.update(factory.newPreparedStatementCreator(new Object[] {group.getName(), group.getRoles(), group.getCreateTime()}), keyHolder);
	        
	        group.setId(NumberUtils.convertNumberToTargetClass(keyHolder.getKey(), Integer.class));
	    	return group;
		}
		jdbcTemplate.update(UPDATE, group.getName(), group.getRoles(), group.getId());
        return group;
	}

	@Override
	public void delete(Integer groupId) {
		Assert.notNull(groupId, "用户组标识不能为空");
		if (jdbcTemplate.queryForObject(COUNT_USERS, Long.class, groupId) > 0) {
			throw new ManageException("用户组存在用户，请移除用户后再操作");
		}
		jdbcTemplate.update(DELETE, groupId);
	}

	@Override
	public List<User> findUsers(Integer groupId) {
		Assert.notNull(groupId, "用户组标识不能为空");
		return jdbcTemplate.query(FIND_USERS, new RowMapper<User>() {
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
				return user;
			}
		}, groupId);
	}
	
	@Override
	public void removeUser(Integer groupId, List<Integer> userIds) {
		Assert.notNull(groupId, "用户组标识不能为空");
		Assert.notEmpty(userIds, "用户标识列表不能为空");
		List<Integer> nonNullList = new ArrayList<Integer>();
		for (Integer userId : userIds) {
			if (userId != null) {
				nonNullList.add(userId);
			}
		}
		int size = nonNullList.size();
		if (size > 0) {
			String sql = String.format(DELETE_USER_GROUPS, StringUtils.collectionToCommaDelimitedString(Collections.nCopies(size, "?")));
			jdbcTemplate.update(sql, nonNullList.toArray());
		}
	}

}
