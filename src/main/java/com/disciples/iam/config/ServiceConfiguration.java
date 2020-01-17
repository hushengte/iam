package com.disciples.iam.config;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.disciples.iam.service.GroupManager;
import com.disciples.iam.service.UserManager;
import com.disciples.iam.service.impl.DefaultGroupManager;
import com.disciples.iam.service.impl.DefaultUserManager;

@Configuration
public class ServiceConfiguration {

	private DefaultUserManager userManager;
	
	@Autowired
	private DataSource dataSource;
	
	@PostConstruct
	public void init() {
		ResourceDatabasePopulator populator = new ResourceDatabasePopulator(new ClassPathResource("/database/mysql/basic.sql"));
		populator.execute(dataSource);
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		Long userCount = jdbcTemplate.queryForObject("select count(id) from iam_user", Long.class);
		if (userCount == null || userCount.intValue() == 0) {
			populator.setScripts(new ClassPathResource("/database/mysql/data.sql"));
			populator.execute(dataSource);
		}
	}
	
	@Bean
    public JdbcOperations jdbcTemplate() {
        return new JdbcTemplate(dataSource);
    }
	
	private DefaultUserManager getDefaultUserManager() {
		if (userManager == null) {
			userManager = new DefaultUserManager(jdbcTemplate());
		}
		return userManager;
	}
	
	@Bean
	public UserManager userManager() {
		return getDefaultUserManager();
	}
	
	@Bean
	public UserDetailsService userDetailsService() {
		return getDefaultUserManager();
	}
	
	@Bean
	public GroupManager groupManager() {
		return new DefaultGroupManager(jdbcTemplate());
	}
	
}
