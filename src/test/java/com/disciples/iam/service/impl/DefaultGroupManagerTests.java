package com.disciples.iam.service.impl;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.test.util.ReflectionTestUtils;

import com.disciples.iam.domain.Group;
import com.disciples.iam.domain.User;

public class DefaultGroupManagerTests {
    
    static final String GROUP_ID_NOT_NULL = "用户组标识不能为空";
    static final String USER_IDS_NOT_EMPTY = "用户标识列表不能为空";
    
    static final String COUNT_USERS = "select count(user_id) from iam_user_group where group_id = ?";
    
    private EmbeddedDatabase dataSource;
    private DefaultUserManager userManager;
    private DefaultGroupManager manager;
    
    @Before
    public void setUp() {
        this.dataSource = new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2)
                .addScript("schema.sql").build();
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        this.userManager = new DefaultUserManager(jdbcTemplate);
        this.manager = new DefaultGroupManager(jdbcTemplate);
    }
    
    @After
    public void tearDown() {
        this.dataSource.shutdown();
    }
    
    @Test
    public void testMapRow() {
        ResultSet rs = Mockito.mock(ResultSet.class);
        try {
            Integer groupId = 1;
            String groupName = "g1";
            String groupRoles = "role";
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            given(rs.getInt(eq(1))).willReturn(groupId);
            given(rs.getString(eq(2))).willReturn(groupName);
            given(rs.getString(eq(3))).willReturn(groupRoles);
            given(rs.getTimestamp(eq(4))).willReturn(timestamp);
            
            Group group = manager.mapRow(rs, 1);
            assertEquals(groupId, group.getId());
            assertEquals(groupName, group.getName());
            assertEquals(groupRoles, group.getRoles());
            assertEquals(timestamp, group.getCreateTime());
            verify(rs).getInt(eq(1));
            verify(rs).getString(eq(2));
            verify(rs).getString(eq(3));
            verify(rs).getTimestamp(eq(4));
            verifyNoMoreInteractions(rs);
        } catch (SQLException e) {
            fail();
        }
    }
    
    @Test
    public void saveAndDeleteGroup() {
        String groupName = "aaa";
        Group g = new Group();
        g.setName(groupName);
        assertNotNull(manager.save(g));
        assertNotNull(g.getId());
        Group saved = manager.findById(g.getId());
        assertNull(saved.getRoles());
        assertNotNull(saved.getCreateTime());
        assertEquals(groupName, saved.getName());
        
        //update
        String newGroupName = "bbb";
        String roles = "admin,manager";
        saved.setName(newGroupName);
        saved.setRoles(roles);
        manager.save(saved);
        Group updated = manager.findById(saved.getId());
        assertEquals(newGroupName, updated.getName());
        assertEquals(roles, updated.getRoles());
        assertEquals(saved.getCreateTime(), updated.getCreateTime());
        
        //delete
        manager.delete(updated.getId());
        assertNull(manager.findById(updated.getId()));
    }
    
    @Test
    public void deleteGroupHasUser() {
        JdbcOperations jdbcOperations = Mockito.mock(JdbcOperations.class);
        DefaultGroupManager gm = new DefaultGroupManager(jdbcOperations);
        
        Integer groupId = 1;
        String countSql = (String) ReflectionTestUtils.getField(DefaultGroupManager.class, "COUNT_USERS");
        given(jdbcOperations.queryForObject(eq(countSql), eq(Long.class), eq(groupId))).willReturn(2L);
        assertThatExceptionOfType(DataIntegrityViolationException.class).isThrownBy(() -> gm.delete(groupId))
            .withMessage("用户组存在用户，请删除用户后再操作");
        verify(jdbcOperations).queryForObject(eq(countSql), eq(Long.class), eq(groupId));
        verifyNoMoreInteractions(jdbcOperations);
    }
    
    @Test
    public void testFindByUserIdOrUsername_removeUser() {
        Group group = new Group();
        group.setName("g1");
        manager.save(group);
        Integer groupId = group.getId();
        String username = "u1";
        User user = new User(username, username, username, null, null);
        userManager.save(user);
        userManager.updateGroups(user.getId(), Arrays.asList(groupId));
        
        List<Group> groups = manager.find(user.getId());
        assertEquals(1, groups.size());
        assertEquals(groupId, groups.get(0).getId());
        
        groups = manager.find(username);
        assertEquals(1, groups.size());
        assertEquals(groupId, groups.get(0).getId());
        
        manager.removeUser(groupId, Arrays.asList(user.getId()));
        groups = manager.find(user.getId());
        assertEquals(0, groups.size());
    }
    
    @Test
    public void testFindGroupList() {
        Group g1 = new Group();
        g1.setName("g1");
        Group g2 = new Group();
        g2.setName("g2");
        manager.save(g1);
        manager.save(g2);
        
        List<Group> groups = manager.findAll();
        assertEquals(2, groups.size());
        
        Map<Integer, Group> gMap = new HashMap<>();
        gMap.put(g1.getId(), g1);
        gMap.put(g2.getId(), g2);
        List<Map<String, Object>> kvs = manager.keyValues();
        kvs.forEach(kv -> {
            Object key = kv.get("key");
            Group group = gMap.get(key);
            assertNotNull(group);
            assertEquals(group.getName(), kv.get("value"));
        });
        
        Page<Group> page1 = manager.find(0, 10, "g");
        assertEquals(2, page1.getTotalElements());
        Page<Group> page11 = manager.find(0, 1, "g");
        assertEquals(2, page11.getTotalElements());
        assertEquals(1, page11.getContent().size());
        Page<Group> page12 = manager.find(1, 1, "g");
        assertEquals(2, page12.getTotalElements());
        assertEquals(1, page12.getContent().size());
        
        Page<Group> page2 = manager.find(0, 10, "a");
        assertEquals(0, page2.getTotalElements());
    }
    
    //============ Assert test =============//
    @Test
    public void constructor_NullJdbcOperations_ShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> new DefaultGroupManager(null))
            .withMessage("JdbcOperations is required.");
    }
    
    @Test
    public void save_NullGroup_ShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> manager.save(null))
            .withMessage("用户组数据不能为空");
    }
    
    @Test
    public void delete_NullGroupId_ShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> manager.delete(null))
            .withMessage(GROUP_ID_NOT_NULL);
    }
    
    @Test
    public void removeUser_NullGroupIdOrEmptyUserIds_ShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> manager.removeUser(null, null))
            .withMessage(GROUP_ID_NOT_NULL);
        assertThatIllegalArgumentException().isThrownBy(() -> manager.removeUser(null, Collections.emptyList()))
            .withMessage(GROUP_ID_NOT_NULL);
        assertThatIllegalArgumentException().isThrownBy(() -> manager.removeUser(null, Arrays.asList(1)))
            .withMessage(GROUP_ID_NOT_NULL);
        
        assertThatIllegalArgumentException().isThrownBy(() -> manager.removeUser(1, null))
            .withMessage(USER_IDS_NOT_EMPTY);
        assertThatIllegalArgumentException().isThrownBy(() -> manager.removeUser(1, Collections.emptyList()))
            .withMessage(USER_IDS_NOT_EMPTY);
    }
    
}
