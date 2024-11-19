package com.disciples.iam.identity;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.disciples.iam.identity.domain.Group;
import com.disciples.iam.identity.domain.Groups;
import com.disciples.iam.identity.domain.User;
import com.disciples.iam.identity.domain.Users;

public class IamUserDetailsServiceTest {

	private Users users;
    private Groups groups;
    private IamUserDetailsService service;
    
    @Before
    public void setUp() {
        this.users = Mockito.mock(Users.class);
        this.groups = Mockito.mock(Groups.class);
        this.service = new IamUserDetailsService(users, groups);
    }

    @Test
    public void loadUserByUsername_NotExistUser_ShouldThrowException() {
        String username = "test";
        given(users.findByUsername(username)).willReturn(Optional.empty());
        assertThatExceptionOfType(UsernameNotFoundException.class)
            .isThrownBy(() -> service.loadUserByUsername(username))
            .withMessage("User does not exist, username=" + username);
        verify(users).findByUsername(username);
        verifyNoMoreInteractions(users);
    }
    
    @Test
    public void loadUserByUsername() {
        String username = "test";
        List<Long> groupIds = Arrays.asList(1L, 2L);
        Optional<User> user = Optional.of(new User(1L, username, "testpass", "testname", null, null));
        List<String> groles = Arrays.asList("G1", "G2", "G3", "G4", "G5");
        List<Group> testGroups = groles.stream()
        		.map(grole -> new Group(1L, "testgroup", grole))
        		.collect(Collectors.toList());
        given(users.findByUsername(eq(username))).willReturn(user);
        given(groups.findGroupIdsByUserId(eq(user.get().getId()))).willReturn(groupIds);
        given(groups.findAllById(eq(groupIds))).willReturn(testGroups);
        
        UserDetails ud = service.loadUserByUsername(username);
        assertNotNull(ud);
        Collection<? extends GrantedAuthority> authorities = ud.getAuthorities();
        // a default role is added
        assertEquals(groles.size() + 1, authorities.size());
        List<String> roles = new ArrayList<>();
        roles.addAll(groles);
        for (String role : roles) {
            assertTrue(authorities.contains(new SimpleGrantedAuthority(role)));
        }
        
        verify(users).findByUsername(eq(username));
        verify(groups).findGroupIdsByUserId(eq(user.get().getId()));
        verify(groups).findAllById(eq(groupIds));
        verifyNoMoreInteractions(users);
        verifyNoMoreInteractions(groups);
    }
    
    //============ Assert test =============//
    @Test
    public void construct_NullParam_ShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> new IamUserDetailsService(null, null))
            .withMessage("User repository is required.");
    }
    
}
