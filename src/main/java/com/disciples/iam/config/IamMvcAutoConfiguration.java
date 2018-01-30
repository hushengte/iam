package com.disciples.iam.config;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.oauth2.OAuth2AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.web.context.ConfigurableWebApplicationContext;

import com.disciples.iam.web.GroupManageController;
import com.disciples.iam.web.UserController;
import com.disciples.iam.web.UserManageController;

@Configuration
@ConditionalOnClass({ConfigurableWebApplicationContext.class})
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
	
	@Configuration
	@ConditionalOnClass({EnableAuthorizationServer.class, EnableResourceServer.class})
	@AutoConfigureBefore(OAuth2AutoConfiguration.class)
	@Import({AuthorizationServerConfiguration.class, ResourceServerConfiguration.class})
	protected static class IamOAuth2AutoConfiguration {}
	
}
