package com.disciples.iam.oauth2.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

/**
 * OAuth2 security configuration
 */
@Configuration(proxyBeanMethods = false)
public class OAuth2AuthorizationSecurityConfiguration extends WebSecurityConfigurerAdapter implements Ordered {
    
    public static final int SECURITY_ORDER = 10;
	
    /**
     * After IamWebSecurityConfigurerAdapter
     */
    @Override
    public int getOrder() {
        return SECURITY_ORDER;
    }
    
    // @formatter: off
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		//TODO: show client info in login page
		LoginUrlAuthenticationEntryPoint authEntryPoint = new LoginUrlAuthenticationEntryPoint("/login.html");
		authEntryPoint.setUseForward(true);
		http
			.requestMatchers()
				.antMatchers("/oauth/authorize")
				.and()
			.authorizeRequests()
				.anyRequest().authenticated()
				.and()
			.exceptionHandling().authenticationEntryPoint(authEntryPoint)
			;
	}
	// @formatter: on

}
