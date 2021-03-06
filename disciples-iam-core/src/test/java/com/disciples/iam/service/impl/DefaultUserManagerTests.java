package com.disciples.iam.service.impl;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import com.disciples.iam.domain.Group;
import com.disciples.iam.domain.User;
import com.disciples.iam.service.GroupManager;
import com.disciples.iam.util.Md5PasswordEncoder;

public class DefaultUserManagerTests {
    
    private EmbeddedDatabase dataSource;
    private DefaultUserManager manager;
    
    @Before
    public void setUp() {
        this.dataSource = new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2)
                .addScript("schema.sql").build();
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        this.manager = new DefaultUserManager(jdbcTemplate);
    }
    
    @After
    public void tearDown() {
        this.dataSource.shutdown();
    }
    
    @Test
    public void testMapRow() {
        ResultSet rs = Mockito.mock(ResultSet.class);
        ResultSetMetaData rsMeta = Mockito.mock(ResultSetMetaData.class);
        try {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            User user = new User();
            user.setId(1);
            user.setUsername("aaa");
            user.setRoles("admin");
            user.setName("A");
            user.setEmail("aaa@gmail.com");
            user.setPhone("13709283123");
            user.setCreateTime(timestamp);
            user.setPassword("12345678");
            
            given(rs.getMetaData()).willReturn(rsMeta);
            given(rsMeta.getColumnCount()).willReturn(9);
            
            given(rs.getInt(eq(1))).willReturn(user.getId());
            given(rs.getString(eq(2))).willReturn(user.getUsername());
            given(rs.getString(eq(3))).willReturn(user.getRoles());
            given(rs.getString(eq(4))).willReturn(user.getName());
            given(rs.getString(eq(5))).willReturn(user.getEmail());
            given(rs.getString(eq(6))).willReturn(user.getPhone());
            given(rs.getTimestamp(eq(7))).willReturn(timestamp);
            given(rs.getInt(eq(8))).willReturn(user.getId());
            given(rs.getString(eq(9))).willReturn(user.getPassword());
            
            
            User mappedUser = manager.mapRow(rs, 1);
            assertEquals(user.getId(), mappedUser.getId());
            assertEquals(user.getUsername(), mappedUser.getUsername());
            assertEquals(user.getRoles(), mappedUser.getRoles());
            assertEquals(user.getName(), mappedUser.getName());
            assertEquals(user.getEmail(), mappedUser.getEmail());
            assertEquals(user.getPhone(), mappedUser.getPhone());
            assertEquals(user.getCreateTime(), mappedUser.getCreateTime());
            assertTrue(mappedUser.isEnabled());
            assertEquals(timestamp, mappedUser.getCreateTime());
            
            verify(rs).getInt(eq(1));
            verify(rs).getString(eq(2));
            verify(rs).getString(eq(3));
            verify(rs).getString(eq(4));
            verify(rs).getString(eq(5));
            verify(rs).getString(eq(6));
            verify(rs).getTimestamp(eq(7));
            verify(rs).getInt(eq(8));
            verify(rs).getString(eq(9));
            verify(rs).getMetaData();
            verify(rsMeta).getColumnCount();
            verifyNoMoreInteractions(rsMeta);
            verifyNoMoreInteractions(rs);
        } catch (SQLException e) {
            fail();
        }
    }
    
    static List<User> users() {
        List<User> users = new ArrayList<User>();
        users.add(new User("ddd", null, "ddd-name", "ddd@gmail.com", null));
        users.add(new User("eeeddd", null, "eeedddname", "eee@gmail.com", null));
        users.add(new User("fffddd", null, "eeefffname", "fff@gmail.com", null));
        return users;
    }
    
    @Test
    public void batchInsert_nullGroupId() {
        List<User> saved = manager.batchInsert(users(), null);
        assertEquals(3, saved.size());
    }
    
    GroupManager groupManager() {
        return new DefaultGroupManager(new JdbcTemplate(dataSource));
    }
    
    @Test
    public void batchInsert_withGroupId() {
        Integer notExistGroupId = 1;
        assertThatExceptionOfType(DataIntegrityViolationException.class)
            .isThrownBy(() -> manager.batchInsert(users(), notExistGroupId))
            .withMessage("Group does not exist, id=" + notExistGroupId);
        
        Integer existGroupId = groupManager().save(new Group(null, "g1", null)).getId();
        List<User> saved = manager.batchInsert(users(), existGroupId);
        assertEquals(3, saved.size());
        
        Page<User> userPage = manager.find(0, 10, existGroupId, "ddd");
        assertEquals(3, userPage.getTotalElements());
    }
    
    @Test
    public void getGroupRoles() {
        User user = new User("aaa", null, "aname", null, null);
        user.setRoles("A,B,C");
        Group savedGroup = groupManager().save(new Group(null, "g1", "GA,GB,GC"));
        User saved = manager.batchInsert(Arrays.asList(user), savedGroup.getId()).get(0);
        List<String> groupRoles = manager.getGroupRoles(saved.getId());
        assertEquals(1, groupRoles.size());
        assertEquals(savedGroup.getRoles(), groupRoles.get(0));
    }
    
    @Test
    public void change_resetPassword() {
        String newRawPassword = "1234567";
        Md5PasswordEncoder encoder = new Md5PasswordEncoder();
        String newEncodedPassword = encoder.encode(newRawPassword);
        
        User user = manager.save(new User("test", null, "testname", "test@gmail.com", null));
        manager.changePassword(user.getId(), manager.getDefaultPassword(), newRawPassword);
        assertEquals(newEncodedPassword, manager.findOne(user.getId()).getPassword());
        
        String defaultEncodedPassword = encoder.encode(manager.getDefaultPassword());
        manager.resetPassword(user.getId());
        assertEquals(defaultEncodedPassword, manager.findOne(user.getId()).getPassword());
    }
    
    //============ Assert test =============//
    @Test
    public void find_NullGroupId_ShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> manager.find((Integer)null))
            .withMessage(DefaultGroupManager.ID_NOT_NULL);
    }
    
    @Test
    public void save_NullUser_ShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> manager.save(null))
            .withMessage("User cannot be null");
    }
    
    @Test
    public void batchInsert_EmptyUserList_ShouldThrowException() {
        String message = "User list cannot be empty.";
        assertThatIllegalArgumentException().isThrownBy(() -> manager.batchInsert(null, null))
            .withMessage(message);
        assertThatIllegalArgumentException().isThrownBy(() -> manager.batchInsert(Collections.emptyList(), null))
            .withMessage(message);
    }
    
    @Test
    public void getGroupRoles_NullUserId_ShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> manager.getGroupRoles(null))
            .withMessage(DefaultUserManager.ID_NOT_NULL);
    }
    
    @Test
    public void delete_NullUserId_ShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> manager.delete((Integer)null))
            .withMessage(DefaultUserManager.ID_NOT_NULL);
    }
    
}
