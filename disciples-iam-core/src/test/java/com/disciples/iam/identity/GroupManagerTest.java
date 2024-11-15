package com.disciples.iam.identity;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.disciples.iam.identity.cmd.SaveGroup;
import com.disciples.iam.identity.domain.Group;
import com.disciples.iam.identity.domain.Groups;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {ManagerConfig.class})
public class GroupManagerTest {
	
	@Autowired
	Groups groups;
	
	@Autowired
	GroupManager groupManager;

	@Test
	@Rollback
	@Transactional
    public void save_deleteGroup() {
		SaveGroup createCmd = new SaveGroup("aaa");
		Group saved = groupManager.save(createCmd);
        assertNotNull(saved);
        assertNull(saved.getRoles());
        assertEquals(createCmd.getName(), saved.getName());
        
        //update
        SaveGroup updateCmd = new SaveGroup(saved.getId(), "bbb", "admin,manager");
        
        groupManager.save(updateCmd);
        Group updated = groups.findById(saved.getId()).get();
        assertEquals(updateCmd.getName(), updated.getName());
        assertEquals(updateCmd.getRoles(), updated.getRoles());
        
        //delete
        groupManager.delete(updated.getId());
        assertFalse(groups.findById(updated.getId()).isPresent());
    }
    
    @Test
    public void deleteGroup_hasMembers_shouldThrowExcpetion() {
        Groups mockGroups = Mockito.mock(Groups.class);
        GroupManager gm = new GroupManager(mockGroups);
        
        Long groupId = 1L;
        given(mockGroups.countMembers(eq(groupId))).willReturn(2L);
        assertThatExceptionOfType(DataIntegrityViolationException.class)
        	.isThrownBy(() -> gm.delete(groupId))
            .withMessage(GroupManager.GROUP_HAS_MEMBERS_MSG);
        verify(mockGroups).countMembers(eq(groupId));
        verifyNoMoreInteractions(mockGroups);
    }
    
    //============ Assert test =============//
    @Test
    public void constructor_NullGroups_ShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> new GroupManager(null))
            .withMessageContaining("is required");
    }
    
    @Test
    public void delete_NullGroupId_ShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> groupManager.delete(null))
        	.withMessageContaining("is required");
    }
    
}
