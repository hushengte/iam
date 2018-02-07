package com.disciples.iam.config;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.security.oauth2.OAuth2AutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

@Configuration
@ConditionalOnClass({EnableAuthorizationServer.class, EnableResourceServer.class})
@AutoConfigureBefore(OAuth2AutoConfiguration.class)
@Import({AuthorizationServerConfiguration.class, ResourceServerConfiguration.class, OAuth2AuthorizationSecurityConfiguration.class})
public class IamOAuth2AutoConfiguration {
	
	@Autowired
	private DataSource dataSource;
	
	@PostConstruct
	public void init() {
		new ResourceDatabasePopulator(new ClassPathResource("/database/mysql/oauth2.sql")).execute(dataSource);
	}
	
}
