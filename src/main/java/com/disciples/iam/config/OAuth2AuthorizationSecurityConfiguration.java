package com.disciples.iam.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

/**
 * After IamWebSecurityConfigurerAdapter
 * @see com.disciples.iam.config.IamWebSecurityConfigurerAdapter
 */
@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE + 2)
@ConditionalOnMissingBean({OAuth2AuthorizationSecurityConfiguration.class})
public class OAuth2AuthorizationSecurityConfiguration extends WebSecurityConfigurerAdapter {
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		//TODO: show client info in login page
		LoginUrlAuthenticationEntryPoint authEntryPoint = new LoginUrlAuthenticationEntryPoint("/login.html");
		authEntryPoint.setUseForward(true);
		// @formatter: off
		http
			.requestMatchers()
				.antMatchers("/oauth/authorize")
				.and()
			.authorizeRequests()
				.anyRequest().authenticated()
				.and()
			.exceptionHandling().authenticationEntryPoint(authEntryPoint)
			;
		// @formatter: on
	}
	
}
