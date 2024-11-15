package com.disciples.iam.identity.domain;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.disciples.data.jdbc.repository.MybatisQuery;

@Repository
public interface Groups extends PagingAndSortingRepository<Group, Long> {
	
	@MybatisQuery
	@Select("select * from iam_user_group where user_id = #{userId}")
	List<GroupMember> findMembersByUserId(@Param("userId") Long userId);
	
	@MybatisQuery
	@Select("select group_id from iam_user_group where user_id = #{userId}")
	List<Long> findGroupIdsByUserId(@Param("userId") Long userId);

	@MybatisQuery
	@Select("select count(user_id) from iam_user_group where group_id = #{groupId}")
	@ResultType(Long.class)
	Long countMembers(@Param("groupId") Long groupId);
	
}
