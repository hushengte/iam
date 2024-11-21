package com.disciples.iam.identity.domain;

import java.util.Optional;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.disciples.data.jdbc.repository.MybatisQuery;

@Repository
public interface Users extends PagingAndSortingRepository<User, Long> {

	@MybatisQuery
	@Select("select id from iam_user where username = #{username}")
	@ResultType(Long.class)
	Long getIdByUsername(@Param("username") String username);
	
	@MybatisQuery
	@Select("select * from iam_user where username = #{username}")
	Optional<User> findByUsername(@Param("username") String username);

	@Modifying
	@MybatisQuery
	@Update("update iam_user set password = #{password} where id = #{id}")
	int savePassword(User user);
	
	@Modifying
	@MybatisQuery
	@Update("update iam_user set status = #{status} where id = #{id}")
	int saveStatus(User user);

	@MybatisQuery
	@Select("select u.* from iam_user u where u.username like concat('%', #{keyword}, '%') or u.nickname like concat('%', #{keyword}, '%')")
	Page<User> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
	
	@MybatisQuery
	@Select("select u.* from iam_user u left join iam_user_group m on u.id = m.user_id where m.group_id = #{groupId}")
	Page<User> findByGroupId(@Param("groupId") Long groupId, Pageable pageable);

	@MybatisQuery
	@Select("select u.* from iam_user u left join iam_user_group m on u.id = m.user_id where m.group_id = #{groupId}"
			+ " and (u.username like concat('%', #{keyword}, '%') or u.nickname like concat('%', #{keyword}, '%'))")
	Page<User> findByGroupIdAndKeyword(@Param("groupId") Long groupId, @Param("keyword") String keyword, Pageable pageable);
	
}
