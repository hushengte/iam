package com.disciples.iam.web;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.disciples.iam.domain.User;
import com.disciples.iam.service.UserManager;

public class UserManageApiTest extends AbstractMvcTests {
    
    @Autowired
    private UserManager userManger;
	
	@Test
	public void testList() throws Exception {
	    userManger.save(new User("ddd", null, "dname", null, null));
		mockMvc.perform(jsonPost("/admin/user/list.do?page=0&size=10", "field=1&keyword=ddd"))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andDo(MockMvcResultHandlers.print());
	}
	
}
