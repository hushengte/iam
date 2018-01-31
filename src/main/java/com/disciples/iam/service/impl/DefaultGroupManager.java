package com.disciples.iam.service.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.dao.DataIntegrityViolationException;
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

import com.disciples.iam.domain.Group;
import com.disciples.iam.service.GroupManager;

public class DefaultGroupManager implements GroupManager, RowMapper<Group> {

	private static final String COUNT = "select count(id) from iam_group";
	private static final String FIND = "select id, name, roles, create_time from iam_group";
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
	public List<Group> find(Integer userId) {
		return jdbcTemplate.query(FIND_BY_USER_ID, this, userId);
	}
	
	@Override
	public List<Group> find(String username) {
		return jdbcTemplate.query(FIND_BY_USERNAME, this, username);
	}
	
	@Override
	public List<Group> findAll() {
		return jdbcTemplate.query(FIND, this);
	}
	
	@Override
	public List<Map<String, Object>> keyValues() {
		return jdbcTemplate.queryForList(FIND_ID_NAMES);
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
			throw new DataIntegrityViolationException("用户组存在用户，请删除用户后再操作");
		}
		jdbcTemplate.update(DELETE, groupId);
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
