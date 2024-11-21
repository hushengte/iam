package com.disciples.iam.identity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.disciples.iam.config.ManagerConfig;
import com.disciples.iam.identity.cmd.SaveGroup;
import com.disciples.iam.identity.cmd.SaveUser;
import com.disciples.iam.identity.cmd.UpdateUserGroups;
import com.disciples.iam.identity.domain.Group;
import com.disciples.iam.identity.domain.Groups;
import com.disciples.iam.identity.domain.User;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {ManagerConfig.class})
public class IdentityQueryServiceTest {
	
	@Autowired
	UserManager userManager;
	
	@Autowired
	Groups groups;
	
	@Autowired
	GroupManager groupManager;
	
	@Autowired
	IdentityQueryService identityQueryService;
	
	@Test
	@Rollback
	@Transactional
	public void findPagedUsers_withKeyword() {
		User u1 = userManager.save(new SaveUser("a1", "nick1", "a1name@gmail.com", "13700000001"));
		User u2 = userManager.save(new SaveUser("a2", "nick2", "a2name@gmail.com", "13700000002"));
		
		Page<User> page1 = identityQueryService.findPagedUsers(0, 10, null, "a");
		assertEquals(2, page1.getTotalElements());
		assertThat(page1.getContent().get(0)).isEqualTo(u1);
		assertThat(page1.getContent().get(1)).isEqualTo(u2);
		Page<User> page11 = identityQueryService.findPagedUsers(0, 1, null, "a");
		assertEquals(2, page11.getTotalElements());
        assertEquals(1, page11.getContent().size());
        Page<User> page12 = identityQueryService.findPagedUsers(1, 1, null, "a");
		assertEquals(2, page12.getTotalElements());
        assertEquals(1, page12.getContent().size());
        
        Page<User> notFoundPage = identityQueryService.findPagedUsers(0, 10, null, "b");
        assertEquals(0, notFoundPage.getTotalElements());
	}
	
	@Test
	@Rollback
	@Transactional
	public void findPageUsers_withGroupId() {
		User u1 = userManager.save(new SaveUser("a1", "nick1", "a1name@gmail.com", "13700000001"));
		User u2 = userManager.save(new SaveUser("a2", "nick2", "a2name@gmail.com", "13700000002"));
        Group g1 = groupManager.save(new SaveGroup("g1"));
        List<Long> groupIds = Arrays.asList(g1.getId());
        userManager.updateGroups(new UpdateUserGroups(u1.getId(), groupIds));
        userManager.updateGroups(new UpdateUserGroups(u2.getId(), groupIds));
        
        Long groupId = g1.getId();
        Page<User> page1 = identityQueryService.findPagedUsers(0, 10, groupId, "a");
		assertEquals(2, page1.getTotalElements());
		assertThat(page1.getContent().get(0)).isEqualTo(u1);
		assertThat(page1.getContent().get(1)).isEqualTo(u2);
		Page<User> page1NoKeyword = identityQueryService.findPagedUsers(0, 10, groupId, null);
		assertEquals(2, page1NoKeyword.getTotalElements());
		assertThat(page1NoKeyword.getContent().get(0)).isEqualTo(u1);
		assertThat(page1NoKeyword.getContent().get(1)).isEqualTo(u2);
		
		Page<User> page11 = identityQueryService.findPagedUsers(0, 1, groupId, "a");
		assertEquals(2, page11.getTotalElements());
        assertEquals(1, page11.getContent().size());
        Page<User> page12 = identityQueryService.findPagedUsers(1, 1, groupId, "a");
		assertEquals(2, page12.getTotalElements());
        assertEquals(1, page12.getContent().size());
        
        Page<User> notFoundPage = identityQueryService.findPagedUsers(0, 10, 0L, null);
        assertEquals(0, notFoundPage.getTotalElements());
        
        Page<User> notFoundPageWithKeyword = identityQueryService.findPagedUsers(0, 10, groupId, "b");
        assertEquals(0, notFoundPageWithKeyword.getTotalElements());
	}
	
	@Test
	@Rollback
	@Transactional
    public void findGroupKeyValues_findPagedGroups() {
        Group g1 = groupManager.save(new SaveGroup("g1"));
        Group g2 = groupManager.save(new SaveGroup("g2"));
        
        List<Group> groupList = (List<Group>) groups.findAll();
        assertEquals(2, groupList.size());
        assertThat(groupList.get(0)).isEqualTo(g1);
        assertThat(groupList.get(1)).isEqualTo(g2);
        
        Map<Long, Group> gMap = new HashMap<>();
        gMap.put(g1.getId(), g1);
        gMap.put(g2.getId(), g2);
        List<Map<String, Object>> kvs = identityQueryService.findGroupKeyValues();
        kvs.forEach(kv -> {
            Object key = kv.get("key");
            Group group = gMap.get(key);
            assertNotNull(group);
            assertEquals(group.getName(), kv.get("value"));
        });
        
        Page<Group> page1 = identityQueryService.findPagedGroups(0, 10, "g");
        assertEquals(2, page1.getTotalElements());
        Page<Group> page11 = identityQueryService.findPagedGroups(0, 1, "g");
        assertEquals(2, page11.getTotalElements());
        assertEquals(1, page11.getContent().size());
        Page<Group> page12 = identityQueryService.findPagedGroups(1, 1, "g");
        assertEquals(2, page12.getTotalElements());
        assertEquals(1, page12.getContent().size());
        
        Page<Group> notFoundPage = identityQueryService.findPagedGroups(0, 10, "a");
        assertEquals(0, notFoundPage.getTotalElements());
    }
	
}
