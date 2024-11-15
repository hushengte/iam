package com.disciples.iam.identity;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import com.disciples.data.jdbc.repository.config.MybatisSupportConfiguration;
import com.disciples.data.jdbc.repository.support.MybatisRepositoryFactoryBean;
import com.disciples.iam.annotation.EnableIam;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@EnableIam
@ComponentScan(basePackages = {"com.disciples.iam.identity"})
@EnableJdbcRepositories(basePackages = {"com.disciples.iam.identity.domain"}, repositoryFactoryBeanClass = MybatisRepositoryFactoryBean.class)
//@EnableJdbcAuditing
public class ManagerConfig extends MybatisSupportConfiguration {
	
	@Bean
	public DataSource dataSource() {
		HikariDataSource ds = new HikariDataSource();
		ds.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/disciples?useSSL=false&rewriteBatchedStatements=true&autoReconnect=true");
		ds.setUsername("root");
		ds.setPassword("123456");
		ds.setMaximumPoolSize(10);
		ds.setMinimumIdle(3);
		return ds;
	}
	
	@Bean
	public PlatformTransactionManager transactionManager(DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}
	
}
