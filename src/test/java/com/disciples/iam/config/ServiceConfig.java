package com.disciples.iam.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.disciples.iam.service.GroupManager;
import com.disciples.iam.service.UserManager;
import com.disciples.iam.service.impl.DefaultGroupManager;
import com.disciples.iam.service.impl.DefaultUserDetailsService;
import com.disciples.iam.service.impl.DefaultUserManager;

@Configuration
public class ServiceConfig {
    
    @Autowired
    private DataSource dataSource;
    
    @Bean
    public JdbcOperations jdbcTemplate() {
        return new JdbcTemplate(dataSource);
    }
    
    @Bean
    public UserManager userManager() {
        return new DefaultUserManager(jdbcTemplate());
    }
    
    @Bean
    public UserDetailsService userDetailsService() {
        return new DefaultUserDetailsService(userManager());
    }
    
    @Bean
    public GroupManager groupManager() {
        return new DefaultGroupManager(jdbcTemplate());
    }
    
}
