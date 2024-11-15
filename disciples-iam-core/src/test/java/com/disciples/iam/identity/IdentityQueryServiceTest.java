package com.disciples.iam.identity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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

import com.disciples.iam.identity.cmd.SaveGroup;
import com.disciples.iam.identity.domain.Group;
import com.disciples.iam.identity.domain.Groups;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {ManagerConfig.class})
public class IdentityQueryServiceTest {
	
	@Autowired
	Groups groups;
	
	@Autowired
	GroupManager groupManager;
	
	@Autowired
	IdentityQueryService identityQueryService;

	@Test
	@Rollback
	@Transactional
    public void findGroupKeyValues_findPagedGroups() {
        Group g1 = groupManager.save(new SaveGroup("g1"));
        Group g2 = groupManager.save(new SaveGroup("g2"));
        
        List<Group> groupList = (List<Group>) groups.findAll();
        assertEquals(2, groupList.size());
        
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
        
        Page<Group> page2 = identityQueryService.findPagedGroups(0, 10, "a");
        assertEquals(0, page2.getTotalElements());
    }
	
}
