package com.disciples.iam.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.disciples.iam.config.DbConfig;
import com.disciples.iam.config.FreeMarkerConfig;
import com.disciples.iam.config.IamSecurityConfig;
import com.disciples.iam.config.MvcConfig;
import com.disciples.iam.config.ServiceConfig;

@WebAppConfiguration
@ContextConfiguration(classes = {DbConfig.class, ServiceConfig.class, 
        MvcConfig.class, FreeMarkerConfig.class, IamSecurityConfig.class})
@RunWith(SpringRunner.class)
public class IamWebSecurityTests {

    @Autowired
    private WebApplicationContext context;
    @Autowired
    private FilterChainProxy filterChainProxy;
    
    protected MockMvc mockMvc;
    
    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).addFilters(filterChainProxy).build();
    }
    
    protected MockHttpServletRequestBuilder formPost(String url, String payload) throws IOException {
        return post(url).contentType(MediaType.APPLICATION_FORM_URLENCODED).content(payload);
    }
    
    @Test
    public void testLogin() throws Exception {
        mockMvc.perform(formPost("/login.do", "username=test&password=test"))
            .andExpect(MockMvcResultMatchers.request().attribute("username", "test"))
            .andExpect(MockMvcResultMatchers.forwardedUrl("/login.html?error=true"));
    }
    
    @Test
    public void testLogout() throws Exception {
        mockMvc.perform(formPost("/logout.do", ""))
            .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
            .andExpect(MockMvcResultMatchers.redirectedUrl("/"));
    }
    
}
