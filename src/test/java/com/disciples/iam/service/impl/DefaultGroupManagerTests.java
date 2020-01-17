package com.disciples.iam.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcOperations;

import com.disciples.iam.domain.Group;

public class DefaultGroupManagerTests {
    
    private static final String FIND_BY_USER_ID = "select g.id, g.name, g.roles, g.create_time from iam_group g"
            + " left join iam_user_group ug on g.id = ug.group_id where ug.user_id = ?";
    
    static final String GROUP_ID_NOT_NULL = "用户组标识不能为空";
    static final String USER_IDS_NOT_EMPTY = "用户标识列表不能为空";
    
    @Mock
    private JdbcOperations jdbcOperations;

    private DefaultGroupManager manager;
    
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.manager = new DefaultGroupManager(jdbcOperations);
        given(jdbcOperations.query(FIND_BY_USER_ID, manager, 1)).willReturn(Arrays.asList(new Group(1)));
    }
    
    @Test
    public void find_ByUserId_ShouldReturnGroupList() {
        List<Group> groups = manager.find(1);
        verify(jdbcOperations).query(eq(FIND_BY_USER_ID), eq(manager), eq(1));
        assertThat(groups).hasSize(1);
        verifyNoMoreInteractions(jdbcOperations);
    }
    
    
    //============ Assert test =============//
    @Test
    public void constructor_NullGroup_ShouldThrowException() {
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
