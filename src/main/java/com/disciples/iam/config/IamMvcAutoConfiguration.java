package com.disciples.iam.config;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.disciples.iam.web.AuthController;
import com.disciples.iam.web.GroupManageController;
import com.disciples.iam.web.UserController;
import com.disciples.iam.web.UserManageController;

@Configuration
@ConditionalOnWebApplication
@AutoConfigureAfter({FreeMarkerAutoConfiguration.class})
@Import(ServiceConfiguration.class)
public class IamMvcAutoConfiguration {
	
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
	
	@Bean
	public AuthController authController() {
		return new AuthController();
	}
	
	@Bean
	@ConditionalOnMissingBean({IamWebSecurityConfigurerAdapter.class})
	public IamWebSecurityConfigurerAdapter iamWebSecurityConfigurerAdapter() {
		return new IamWebSecurityConfigurerAdapter();
	}
	
}
