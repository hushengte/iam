package com.disciples.iam.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.disciples.iam.service.GroupManager;
import com.disciples.iam.service.UserManager;
import com.disciples.iam.service.impl.DefaultGroupManager;
import com.disciples.iam.service.impl.DefaultUserManager;

@Configuration
public class ServiceConfig {
	
	private DefaultUserManager userManager;
	
	@Autowired
	private DataSource dataSource;
	
	private DefaultUserManager getDefaultUserManager() {
		if (userManager == null) {
			userManager = new DefaultUserManager(dataSource);
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
		return new DefaultGroupManager(dataSource);
	}
	
}
