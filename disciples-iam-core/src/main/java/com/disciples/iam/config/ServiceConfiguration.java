package com.disciples.iam.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.disciples.iam.identity.GroupManager;
import com.disciples.iam.identity.IamUserDetailsService;
import com.disciples.iam.identity.UserManager;
import com.disciples.iam.identity.domain.Groups;
import com.disciples.iam.identity.domain.Users;
import com.disciples.iam.util.Md5PasswordEncoder;

@Configuration(proxyBeanMethods = false)
public class ServiceConfiguration {
    
	@Bean
    public JdbcOperations jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
	
	@Bean
	public UserManager userManager(JdbcOperations jdbcTemplate, Users users) {
		return new UserManager(jdbcTemplate, users, new Md5PasswordEncoder());
	}
	
	@Bean
	public GroupManager groupManager(Groups groups) {
		return new GroupManager(groups);
	}
	
	@Bean
	public UserDetailsService userDetailsService(Users users, Groups groups) {
		return new IamUserDetailsService(users, groups);
	}
	
}
