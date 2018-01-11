package com.disciples.iam.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.disciples.iam.web.GroupManageController;
import com.disciples.iam.web.UserController;
import com.disciples.iam.web.UserManageController;

@Configuration
public class WebConfig {
	
	@Bean
	public UserManageController userManageController() {
		return new UserManageController();
	}

	@Bean
	public GroupManageController groupManageController() {
		return new GroupManageController();
	}
	
	@Bean
	public UserController userController() {
		return new UserController();
	}
	
}
