package com.disciples.iam;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import org.junit.Test;

public class UserManageApiTest extends AbstractMvcTests {
	
	@Test
	public void testList() throws Exception {
		mockMvc.perform(jsonPost("/admin/user/list.do?page=0&size=10", "field=1&keyword=ddd"))
			.andDo(print());
	}
	
}
