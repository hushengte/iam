package com.disciples.iam.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableWebSecurity
public class IamSecurityConfig {

    @Bean
    public IamWebSecurityConfigurerAdapter iamWebSecurityConfigurerAdapter() {
        return new IamWebSecurityConfigurerAdapter();
    }
    
}
