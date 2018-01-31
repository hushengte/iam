package com.disciples.iam;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.disciples.iam.config.DbConfig;
import com.disciples.iam.config.ServiceConfiguration;
import com.disciples.iam.domain.Group;
import com.disciples.iam.domain.User;
import com.disciples.iam.service.GroupManager;
import com.disciples.iam.service.UserManager;

@ContextConfiguration(classes = {DbConfig.class, ServiceConfiguration.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class ServiceTest {

	@Autowired
	private UserManager userManager;
	@Autowired
	private GroupManager groupManager;
	
	@Test
	public void testAll() {
		testGroupSaveAndFind();
		testGroupKeyValues();
		
		testUserSaveAndFind();
	}
	
	@Test
	public void testGroupSaveAndFind() {
		Integer groupId = groupManager.save(new Group(null, "group-b", "a")).getId();
		Assert.assertNotNull(groupId);
		Assert.assertEquals(groupManager.save(new Group(groupId, "group-a", "a")).getName(), "group-a");
		groupManager.save(new Group(null, "group-bbaa", "bbaa"));
		groupManager.save(new Group(null, "group-ccc", null));
		
		Assert.assertEquals(groupManager.find(0, 10, "group").getTotalElements(), 3);
		Page<Group> groupPage = groupManager.find(0, 10, "a");
		Assert.assertEquals(groupPage.getTotalElements(), 2);
		Assert.assertEquals(groupPage.getContent().get(0).getName(), "group-a");
		Assert.assertEquals(groupPage.getContent().get(1).getName(), "group-bbaa");
	}
	
	@Test
	public void testGroupKeyValues() {
		List<Map<String, Object>> kvs = groupManager.keyValues();
		List<Group> groups = groupManager.findAll();
		for (int i = 0; i < groups.size(); i++) {
			Map<String, Object> kv = kvs.get(i);
			Group saved = groups.get(i);
			Assert.assertEquals((Integer)kv.get("key"), saved.getId());
			Assert.assertEquals((String)kv.get("value"), saved.getName());
		}
	}
	
	@Test
	public void testUserSaveAndFind() {
		List<User> users = new ArrayList<User>();
		users.add(new User("aaa", null, "aaaname", "aaa@gmail.com", "18767122509"));
		users.add(new User("bbb", null, "bbbname", "bbb@gmail.com", "18767122509"));
		users.add(new User("ccc", null, "cccname", "ccc@gmail.com", "18767122509"));
		List<User> saved = userManager.batchInsert(users, null);
		Assert.assertEquals(3, saved.size());
		
		users = new ArrayList<User>();
		users.add(new User("bbb", null, "bbbname", "bbb@gmail.com", "18767122509"));
		users.add(new User("ccc", null, "cccname", "ccc@gmail.com", "18767122509"));
		
		User savedUser = userManager.save(new User("ddd", null, "ddd-name", "ddd@gmail.com", "18767122509"));
		savedUser.setName("dddname");
		users.add(userManager.save(savedUser));
		Assert.assertTrue(userManager.exists(savedUser.getUsername()));
		Assert.assertEquals(savedUser.getName(), userManager.findOneByUsername(savedUser.getUsername()).getName());
		
		users.add(new User("eeeddd", null, "eeedddname", "eee@gmail.com", "18767122509"));
		users.add(new User("fffddd", null, "eeefffname", "fff@gmail.com", "18767122509"));
		List<Map<String, Object>> kvs = groupManager.keyValues();
		Integer groupId = kvs.size() > 0 ? (Integer)kvs.get(0).get("id") : null;
		saved = userManager.batchInsert(users, groupId);
		Assert.assertEquals(2, saved.size());
		
		Page<User> userPage = userManager.find(0, 10, groupId, "ddd");
		Assert.assertEquals(2, userPage.getTotalElements());
	}
	
}
