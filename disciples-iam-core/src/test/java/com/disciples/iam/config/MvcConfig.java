package com.disciples.iam.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperFactoryBean;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration(proxyBeanMethods = false)
@EnableWebMvc
@Import(WebConfig.class)
public class MvcConfig implements WebMvcConfigurer {
    
	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		Jackson2ObjectMapperFactoryBean factory = new Jackson2ObjectMapperFactoryBean();
		factory.setSimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		factory.afterPropertiesSet();
		MappingJackson2HttpMessageConverter jackson2Converter = new MappingJackson2HttpMessageConverter();
		jackson2Converter.setObjectMapper(factory.getObject());
		converters.add(jackson2Converter);
		converters.add(new FormHttpMessageConverter());
	}
	
}
