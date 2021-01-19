package com.disciples.iam.service.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.NumberUtils;
import org.springframework.util.StringUtils;

import com.disciples.iam.domain.Group;
import com.disciples.iam.service.GroupManager;

public class DefaultGroupManager implements GroupManager, RowMapper<Group> {

	private static final String COUNT = "select count(id) from iam_group";
	private static final String FIND = "select id, name, roles, create_time from iam_group";
	private static final String FIND_BY_ID = "select id, name, roles, create_time from iam_group where id = ?";
	private static final String FIND_ID_NAMES = "select `id` as `key`, `name` as `value` from iam_group";
	private static final String INSERT = "insert into iam_group (name, roles, create_time) values (?,?,?)";
	private static final String UPDATE = "update iam_group set name = ?, roles = ? where id = ?";
	private static final String DELETE = "delete from iam_group where id = ?";
	
	private static final String COUNT_USERS = "select count(user_id) from iam_user_group where group_id = ?";
	private static final String DELETE_USER_GROUPS = "delete from iam_user_group where group_id = ? and user_id in (%s)";
	private static final String FIND_BY_USER_ID = "select g.id, g.name, g.roles, g.create_time from iam_group g"
			+ " left join iam_user_group ug on g.id = ug.group_id where ug.user_id = ?";
	private static final String FIND_BY_USERNAME = "select g.id, g.name, g.roles, g.create_time from iam_group g"
			+ " left join iam_user_group ug on g.id = ug.group_id left join iam_user u on ug.user_id = u.id where u.username = ?";
	
	public static final String ID_NOT_NULL = "Group id cannot be null.";
	
	private JdbcOperations jdbcOperations;
	
	public DefaultGroupManager(JdbcOperations jdbcOperations) {
		Assert.notNull(jdbcOperations, "JdbcOperations is required.");
		this.jdbcOperations = jdbcOperations;
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
	public Group findById(Integer groupId) {
	    List<Group> groups = jdbcOperations.query(FIND_BY_ID, this, groupId);
	    if (!CollectionUtils.isEmpty(groups)) {
	        return groups.get(0);
	    }
	    return null;
	}
	
	@Override
	public Page<Group> find(int page, int size, String keyword) {
		Pageable pageable = PageRequest.of(page, size);
		
		StringBuilder condition = new StringBuilder();
		List<Object> args = new ArrayList<Object>(4);
		if (StringUtils.hasText(keyword)) {
			condition.append(" where name like ?");
			args.add("%" + keyword.trim() + "%");
		}
		Long count = jdbcOperations.queryForObject(COUNT + condition.toString(), Long.class, args.toArray());
		List<Group> content = Collections.emptyList();
		if (count > 0) {
		    condition.append(" limit ?,?");
	        args.add(pageable.getPageNumber() * pageable.getPageSize());
	        args.add(pageable.getPageSize());
	        content = jdbcOperations.query(FIND + condition.toString(), this, args.toArray());
		}
		return new PageImpl<>(content, pageable, count);
	}
	
	@Override
	public List<Group> find(Integer userId) {
		return jdbcOperations.query(FIND_BY_USER_ID, this, userId);
	}
	
	@Override
	public List<Group> find(String username) {
		return jdbcOperations.query(FIND_BY_USERNAME, this, username);
	}
	
	@Override
	public List<Group> findAll() {
		return jdbcOperations.query(FIND, this);
	}
	
	@Override
	public List<Map<String, Object>> keyValues() {
		return jdbcOperations.queryForList(FIND_ID_NAMES);
	}
	
	@Override
	public Group save(Group group) {
		Assert.notNull(group, "Group cannot be null.");
		Integer id = group.getId();
		if (id == null) {
			group.setCreateTime(new Date());
			
			PreparedStatementCreatorFactory factory = new PreparedStatementCreatorFactory(INSERT, Types.VARCHAR, Types.VARCHAR, Types.TIMESTAMP);
			factory.setGeneratedKeysColumnNames("id");
			GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
			jdbcOperations.update(factory.newPreparedStatementCreator(new Object[] {group.getName(), group.getRoles(), group.getCreateTime()}), keyHolder);
	        
	        group.setId(NumberUtils.convertNumberToTargetClass(keyHolder.getKey(), Integer.class));
	    	return group;
		}
		jdbcOperations.update(UPDATE, group.getName(), group.getRoles(), group.getId());
        return group;
	}

	@Override
	public void delete(Integer groupId) {
		Assert.notNull(groupId, ID_NOT_NULL);
		if (jdbcOperations.queryForObject(COUNT_USERS, Long.class, groupId) > 0) {
			throw new DataIntegrityViolationException("There are users in this group, please delete these users first.");
		}
		jdbcOperations.update(DELETE, groupId);
	}

	@Override
	public void removeUser(Integer groupId, List<Integer> userIds) {
		Assert.notNull(groupId, ID_NOT_NULL);
		Assert.notEmpty(userIds, "User id list cannot be empty.");
		List<Integer> nonNullList = new ArrayList<Integer>();
		for (Integer userId : userIds) {
			if (userId != null) {
				nonNullList.add(userId);
			}
		}
		int size = nonNullList.size();
		if (size > 0) {
			String sql = String.format(DELETE_USER_GROUPS, StringUtils.collectionToCommaDelimitedString(Collections.nCopies(size, "?")));
			List<Object> args = new ArrayList<>();
			args.add(groupId);
			args.addAll(nonNullList);
			jdbcOperations.update(sql, args.toArray());
		}
	}

}
