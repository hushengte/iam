package com.disciples.iam.config;

import javax.servlet.Servlet;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.DispatcherServlet;

import com.disciples.iam.web.GroupManageController;
import com.disciples.iam.web.UserController;
import com.disciples.iam.web.UserManageController;

@Configuration
@ConditionalOnClass({Servlet.class, DispatcherServlet.class})
@ConditionalOnWebApplication
@Import(ServiceConfiguration.class)
public class IamMvcAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public UserManageController userManageController() {
		return new UserManageController();
	}

	@Bean
	@ConditionalOnMissingBean
	public GroupManageController groupManageController() {
		return new GroupManageController();
	}
	
	@Bean
	@ConditionalOnMissingBean
	public UserController userController() {
		return new UserController();
	}
	
}
