package com.disciples.iam.config;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.disciples.iam.web.UserManageController;

@Configuration
@ConditionalOnWebApplication
@AutoConfigureAfter({FreeMarkerAutoConfiguration.class})
@Import(ServiceConfiguration.class)
@ComponentScan(basePackageClasses = {UserManageController.class})
public class IamMvcAutoConfiguration {
	
	@Bean
	@ConditionalOnMissingBean({IamWebSecurityConfigurerAdapter.class})
	public IamWebSecurityConfigurerAdapter iamWebSecurityConfigurerAdapter() {
		return new IamWebSecurityConfigurerAdapter();
	}
	
}
