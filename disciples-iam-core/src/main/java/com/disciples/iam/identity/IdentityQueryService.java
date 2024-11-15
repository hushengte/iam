package com.disciples.iam.identity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.core.convert.EntityRowMapper;
import org.springframework.data.jdbc.core.convert.JdbcConverter;
import org.springframework.data.relational.core.mapping.RelationalMappingContext;
import org.springframework.data.relational.core.mapping.RelationalPersistentEntity;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.disciples.iam.identity.domain.Group;
import com.disciples.iam.identity.domain.GroupMember;
import com.disciples.iam.identity.domain.Groups;
import com.disciples.iam.identity.domain.User;

@Service
public class IdentityQueryService {
	
	private static final String COUNT_GROUP = "select count(id) from iam_group";
	private static final String FIND_GROUP = "select * from iam_group";
	private static final String FIND_GROUP_ID_NAMES = "select `id` as `key`, `name` as `value` from iam_group";
	
	private static final String FIND_USER_BY_GROUP_ID = "select %s from iam_user u left join iam_user_group m on u.id = m.user_id where m.group_id = ?";
	private static final String FIND_USER_BY = "select %s from iam_user u where 1=1";
	
	private final JdbcOperations jdbcOperations;
	private final Groups groups;
	private final EntityRowMapper<User> userRowMapper;
	private final EntityRowMapper<Group> groupRowMapper;

	@Autowired
	@SuppressWarnings("unchecked")
	public IdentityQueryService(JdbcOperations jdbcOperations, Groups groups,
			RelationalMappingContext mappingContext, JdbcConverter jdbcConverter) {
		this.jdbcOperations = jdbcOperations;
		this.groups = groups;
		
		RelationalPersistentEntity<User> userEntity = (RelationalPersistentEntity<User>) mappingContext.getPersistentEntity(User.class);
		RelationalPersistentEntity<Group> groupEntity = (RelationalPersistentEntity<Group>) mappingContext.getPersistentEntity(Group.class);
		
		this.userRowMapper = new EntityRowMapper<User>(userEntity, jdbcConverter);
		this.groupRowMapper = new EntityRowMapper<Group>(groupEntity, jdbcConverter); 
	}

	public Page<User> findPagedUsers(Integer page, Integer size, Integer groupId, String keyword) {
		Pageable pageable = PageRequest.of(page, size);
		List<Object> args = new ArrayList<Object>();
		StringBuilder sqlFormat = new StringBuilder();
		if (groupId != null) {
			sqlFormat.append(FIND_USER_BY_GROUP_ID);
			args.add(groupId);
		} else {
			sqlFormat.append(FIND_USER_BY);
		}
		if (StringUtils.hasText(keyword)) {
			sqlFormat.append(" and (u.username like ? or u.nickname like ?)");
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
		List<User> content = jdbcOperations.query(String.format(sqlFormat.toString(), "u.*"), userRowMapper, args.toArray());
		return new PageImpl<User>(content, pageable, count);
	}
	
	public List<Map<String, Object>> findGroupKeyValues() {
		return jdbcOperations.queryForList(FIND_GROUP_ID_NAMES);
	}
	
	public Page<Group> findPagedGroups(Integer page, Integer size, String keyword) {
		Pageable pageable = PageRequest.of(page, size);
		
		StringBuilder condition = new StringBuilder();
		List<Object> args = new ArrayList<Object>(4);
		if (StringUtils.hasText(keyword)) {
			condition.append(" where name like ?");
			args.add("%" + keyword.trim() + "%");
		}
		Long count = jdbcOperations.queryForObject(COUNT_GROUP + condition.toString(), Long.class, args.toArray());
		List<Group> content = Collections.emptyList();
		if (count > 0) {
		    condition.append(" limit ?,?");
	        args.add(pageable.getPageNumber() * pageable.getPageSize());
	        args.add(pageable.getPageSize());
	        content = jdbcOperations.query(FIND_GROUP + condition.toString(), groupRowMapper, args.toArray());
		}
		return new PageImpl<>(content, pageable, count);
	}
	
	public List<Long> findGroupIds(Long userId) {
		return groups.findMembersByUserId(userId).stream()
				.map(GroupMember::getGroupId)
				.collect(Collectors.toList());
	}
	
}
