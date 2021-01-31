package com.disciples.iam.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

@Configuration(proxyBeanMethods = false)
public class FreeMarkerConfig {

    @Bean
    public freemarker.template.Configuration freeMarkerConfiguration() {
        FreeMarkerConfigurationFactoryBean factory = new FreeMarkerConfigurationFactoryBean();
        factory.setTemplateLoaderPaths("classpath:/templates/");
        factory.setDefaultEncoding("UTF-8");
        try {
            factory.afterPropertiesSet();
            return factory.getObject();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    
    @Bean
    public FreeMarkerViewResolver freeMarkerViewResolver() {
        FreeMarkerViewResolver resolver = new FreeMarkerViewResolver();
        return resolver;
    }
    
}
