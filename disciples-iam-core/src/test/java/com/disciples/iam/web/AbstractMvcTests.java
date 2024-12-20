package com.disciples.iam.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.io.IOException;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.disciples.iam.config.ManagerConfig;
import com.disciples.iam.config.MvcConfig;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {ManagerConfig.class, MvcConfig.class})
@WebAppConfiguration
public abstract class AbstractMvcTests {

	@Autowired
	private WebApplicationContext context;
	
	protected MockMvc mockMvc;
	
	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
	}
	
	protected MockHttpServletRequestBuilder formPost(String url, String payload) throws IOException {
        return post(url).contentType(MediaType.APPLICATION_FORM_URLENCODED).content(payload);
    }
	
	protected MockHttpServletRequestBuilder jsonPost(String url, String payload) throws IOException {
		return post(url).contentType(MediaType.APPLICATION_JSON).content(payload);
	}
	
}
