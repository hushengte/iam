package com.disciples.iam.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
@PropertySource("classpath:jdbc.properties")
public class DbConfig {

	@Autowired
	private Environment env;
	
	@Bean
	public DataSource dataSource() {
		HikariDataSource dataSource = new HikariDataSource();
		dataSource.setDriverClassName(env.getProperty("jdbc.driverClassName"));
		dataSource.setJdbcUrl(env.getProperty("jdbc.url"));
		dataSource.setUsername(env.getProperty("jdbc.username"));
		dataSource.setPassword(env.getProperty("jdbc.password"));
		dataSource.setMinimumIdle(env.getProperty("jdbc.pool.minSize", Integer.class));
		dataSource.setMaximumPoolSize(env.getProperty("jdbc.pool.maxSize", Integer.class));
		
		DatabasePopulatorUtils.execute(new ResourceDatabasePopulator(new ClassPathResource("/mysql.sql")), dataSource);
		return dataSource;
	}
    
}
