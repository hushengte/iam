package com.disciples.iam.config;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.ForwardAuthenticationFailureHandler;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security support: form login
 */
@Configuration
public class IamSecurityConfiguration {
	
    public static final int SECURITY_ORDER = 0;
    
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_USER = "USER";
    
    protected static final String URL_LOGIN = "/login.do";
    protected static final String URL_LOGOUT = "/logout.do";
    protected static final String URL_LOGIN_PAGE = "/login.html";

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().antMatchers("/css/**");
    }
    
    // @formatter: off
    @Bean
    public SecurityFilterChain iamSecurityFilterChain(HttpSecurity http) throws Exception {
    	http
			.requestMatchers()
				.antMatchers("/admin/**", "/user/**", URL_LOGIN, URL_LOGOUT)
				.and()
			.authorizeRequests()
				.antMatchers("/admin/**").hasAuthority(ROLE_ADMIN)
				.antMatchers("/user/**").hasAuthority(ROLE_USER)
				.and()
			.csrf().disable()
			.formLogin()
				.loginPage(URL_LOGIN_PAGE)
				.loginProcessingUrl(URL_LOGIN).defaultSuccessUrl("/")
				.failureHandler(new UsernameAwareFailureHandler(URL_LOGIN_PAGE))
				.permitAll()
				.and()
			.logout()
				.logoutUrl(URL_LOGOUT).logoutSuccessUrl("/")
				.and()
			.exceptionHandling().authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint(URL_LOGIN_PAGE))
			;
    	return http.build();
    }
	// @formatter: on
	
	private static class UsernameAwareFailureHandler extends ForwardAuthenticationFailureHandler {
        
        public UsernameAwareFailureHandler(String defaultFailureUrl) {
            super(defaultFailureUrl);
        }

        @Override
        public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                AuthenticationException exception) throws IOException, ServletException {
            
            String usernameKey = UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY;
            request.setAttribute(usernameKey, request.getParameter(usernameKey));
            request.setAttribute("error", true);
            
            super.onAuthenticationFailure(request, response, exception);
        }
        
    }

}
