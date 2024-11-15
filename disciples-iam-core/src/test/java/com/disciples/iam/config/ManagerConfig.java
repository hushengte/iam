package com.disciples.iam.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

import com.disciples.data.jdbc.repository.config.MybatisSupportConfiguration;
import com.disciples.data.jdbc.repository.support.MybatisRepositoryFactoryBean;
import com.disciples.iam.annotation.EnableIam;

@Configuration
@Import({DbConfig.class})
@EnableIam
@ComponentScan(basePackages = {"com.disciples.iam.identity"})
@EnableJdbcRepositories(basePackages = {"com.disciples.iam.identity.domain"}, repositoryFactoryBeanClass = MybatisRepositoryFactoryBean.class)
//@EnableJdbcAuditing
public class ManagerConfig extends MybatisSupportConfiguration {
	
}
