package com.disciples.iam.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
@Import({IamSecurityConfiguration.class})
public class IamSecurityConfig {

}
