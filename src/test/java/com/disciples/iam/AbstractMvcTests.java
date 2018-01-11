package com.disciples.iam;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.io.IOException;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.disciples.iam.config.DbConfig;
import com.disciples.iam.config.MvcConfig;
import com.disciples.iam.config.ServiceConfiguration;

@WebAppConfiguration
@ContextConfiguration(classes = {DbConfig.class, ServiceConfiguration.class, MvcConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
public abstract class AbstractMvcTests {

	@Autowired
	private WebApplicationContext context;
	
	protected MockMvc mockMvc;
	
	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
	}
	
	protected MockHttpServletRequestBuilder jsonPost(String url, String payload) throws IOException {
		return post(url).contentType(MediaType.APPLICATION_JSON_UTF8).content(payload);
	}
	
}
