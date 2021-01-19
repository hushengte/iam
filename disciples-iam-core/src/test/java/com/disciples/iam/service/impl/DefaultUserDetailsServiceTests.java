package com.disciples.iam.service.impl;

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

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.StringUtils;

import com.disciples.iam.domain.User;
import com.disciples.iam.service.UserManager;

public class DefaultUserDetailsServiceTests {
    
    private UserManager userManager;
    private DefaultUserDetailsService service;
    
    @Before
    public void setUp() {
        this.userManager = Mockito.mock(UserManager.class);
        this.service = new DefaultUserDetailsService(userManager);
    }

    @Test
    public void loadUserByUsername_NotExistUser_ShouldThrowException() {
        String username = "test";
        given(userManager.findOneByUsername(username)).willReturn(null);
        assertThatExceptionOfType(UsernameNotFoundException.class)
            .isThrownBy(() -> service.loadUserByUsername(username))
            .withMessage("User does not exist, username=" + username);
        verify(userManager).findOneByUsername(username);
        verifyNoMoreInteractions(userManager);
    }
    
    @Test
    public void loadUserByUsername() {
        String username = "test";
        List<String> uroles = Arrays.asList("A", "B", "C");
        User user = new User(username, null, "testname", null, null);
        user.setId(1);
        user.setRoles(StringUtils.collectionToCommaDelimitedString(uroles));
        List<String> groles = Arrays.asList("G1", "G2", "G3", "G4", "G5");
        given(userManager.findOneByUsername(eq(username))).willReturn(user);
        given(userManager.getGroupRoles(eq(user.getId()))).willReturn(groles);
        
        UserDetails ud = service.loadUserByUsername(username);
        assertNotNull(ud);
        Collection<? extends GrantedAuthority> authorities = ud.getAuthorities();
        assertEquals(uroles.size() + groles.size(), authorities.size());
        List<String> roles = new ArrayList<>();
        roles.addAll(uroles);
        roles.addAll(groles);
        for (String role : roles) {
            assertTrue(authorities.contains(new SimpleGrantedAuthority(role)));
        }
        
        verify(userManager).findOneByUsername(eq(username));
        verify(userManager).getGroupRoles(eq(user.getId()));
        verifyNoMoreInteractions(userManager);
    }
    
    //============ Assert test =============//
    @Test
    public void construct_NullParam_ShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> new DefaultUserDetailsService(null))
            .withMessage("UserManager is required.");
    }
    
}
