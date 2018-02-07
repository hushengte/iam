package com.disciples.iam.config;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.util.WebUtils;

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
		// @formatter: off
		http
			.requestMatchers()
				.antMatchers("/admin/**", "/user/**", "/login.do")
				.and()
			.authorizeRequests()
				.antMatchers("/admin/**").access("hasRole('ADMIN')")
				.antMatchers("/user/**").access("hasRole('ROLE_USER')")
				.and()
			.csrf().disable()
			.formLogin()
				.loginPage("/login.html")
				.loginProcessingUrl("/login.do").defaultSuccessUrl("/")
				.failureHandler(new UsernameAwareAuthenticationFailureHandler("/login.html?error=true"))
				.permitAll()
				.and()
			.logout()
				.logoutUrl("/logout.do").logoutSuccessUrl("/")
				.and()
			.exceptionHandling().authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login.html"))
			;
		// @formatter: on
	}
	
	private static class UsernameAwareAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
		
		public UsernameAwareAuthenticationFailureHandler(String defaultFailureUrl) {
			super(defaultFailureUrl);
		}

		@Override
		public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
		        AuthenticationException exception) throws IOException, ServletException {
			
			String name = UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY;
			WebUtils.setSessionAttribute(request, name, request.getParameter(name));
			
			super.onAuthenticationFailure(request, response, exception);
		}
		
	}
	
}
