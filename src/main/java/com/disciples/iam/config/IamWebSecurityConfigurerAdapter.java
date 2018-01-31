package com.disciples.iam.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

/**
 * After IgnoredPathsWebSecurityConfigurerAdapter
 * @see org.springframework.boot.autoconfigure.security.SpringBootWebSecurityConfiguration
 */
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class IamWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

	@Autowired
	private UserDetailsService userDetailsService;

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(new Md5PasswordEncoder());
	}
	
	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/css/**");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.authorizeRequests()
				.antMatchers("/admin/**").access("hasRole('ADMIN')")
				.antMatchers("/user/**").access("hasRole('ROLE_USER')")
				.and()
			.csrf().disable()
			.formLogin()
				.loginPage("/login.html").permitAll()
				.loginProcessingUrl("/login.do").defaultSuccessUrl("/")
				.failureForwardUrl("/authfailed.do")
				.and()
			.logout()
				.logoutUrl("/logout.do").logoutSuccessUrl("/")
				.and()
			.exceptionHandling().authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login.html"))
			;
	}
	
}
