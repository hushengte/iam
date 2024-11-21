package com.disciples.iam.identity.domain;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.disciples.data.jdbc.repository.MybatisQuery;

@Repository
public interface Groups extends PagingAndSortingRepository<Group, Long> {
	
	@MybatisQuery
	@Select("select `id` as `key`, `name` as `value` from iam_group")
	List<Map<String, Object>> findKeyValues();
	
	@MybatisQuery
	@Select("select group_id from iam_user_group where user_id = #{userId}")
	List<Long> findGroupIdsByUserId(@Param("userId") Long userId);

	@MybatisQuery
	@Select("select count(user_id) from iam_user_group where group_id = #{groupId}")
	@ResultType(Long.class)
	Long countMembers(@Param("groupId") Long groupId);
	
	@MybatisQuery
	@Select("select * from iam_group where `name` like concat('%', #{name}, '%')")
	Page<Group> findByName(@Param("name") String name, Pageable pageable);
	
}
