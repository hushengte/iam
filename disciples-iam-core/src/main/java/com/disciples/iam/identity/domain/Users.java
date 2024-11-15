package com.disciples.iam.identity.domain;

import java.util.Optional;

import org.apache.ibatis.annotations.Update;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.disciples.data.jdbc.repository.MybatisQuery;

@Repository
public interface Users extends PagingAndSortingRepository<User, Long> {

	@Query("select id from iam_user where username = :username")
	Long getIdByUsername(@Param("username") String username);
	
	@Query("select * from iam_user where username = :username")
	Optional<User> findByUsername(@Param("username") String username);

	@Modifying
	@MybatisQuery
	@Update("update iam_user set password = #{password} where id = #{id}")
	int savePassword(User user);
	
	@Modifying
	@MybatisQuery
	@Update("update iam_user set status = #{status} where id = #{id}")
	int saveStatus(User user);

}
