package com.disciples.iam.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.disciples.iam.annotation.EnableIam;

@Configuration
@Import({DbConfig.class})
@EnableIam
//@EnableJdbcAuditing
public class ManagerConfig {
	
}
