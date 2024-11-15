package com.disciples.iam.web;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import com.disciples.iam.identity.UserManager;
import com.disciples.iam.identity.cmd.RegisterUser;

public class UserManageApiTest extends AbstractMvcTests {
    
    @Autowired
    private UserManager userManger;
	
	@Test
	@Rollback
	@Transactional
	public void testList() throws Exception {
	    userManger.register(new RegisterUser("ddd", null, "dname", null, null));
		mockMvc.perform(formPost("/admin/user/list?page=0&size=10", "field=1&keyword=ddd"))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
			.andDo(MockMvcResultHandlers.print());
	}
	
}
