package com.disciples.iam.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import com.zaxxer.hikari.HikariDataSource;

@Configuration(proxyBeanMethods = false)
public class DbConfig {
	
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
