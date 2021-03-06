package com.disciples.iam.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.disciples.iam.domain.User;
import com.disciples.iam.service.UserManager;

public class DefaultUserDetailsService implements UserDetailsService {
    
    private UserManager userManager;
    
    public DefaultUserDetailsService(UserManager userManager) {
        Assert.notNull(userManager, "UserManager is required.");
        this.userManager = userManager;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userManager.findOneByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User does not exist, username=" + username);
        }
        List<String> groupRoles = userManager.getGroupRoles(user.getId());
        Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
        for (String groupRole : groupRoles) {
            authorities.addAll(getAuthorities(groupRole));
        }
        authorities.addAll(getAuthorities(user.getRoles()));
        user.setAuthorities(Collections.unmodifiableSet(authorities));
        return user;
    }
    
    private static List<? extends GrantedAuthority> getAuthorities(String roles) {
        String[] roleArray = StringUtils.commaDelimitedListToStringArray(roles);
        if (roleArray.length > 0) {
            List<SimpleGrantedAuthority> authorties = new ArrayList<SimpleGrantedAuthority>();
            for (String role : roleArray) {
                authorties.add(new SimpleGrantedAuthority(role));
            }
            return authorties;
        }
        return Collections.emptyList();
    }

}
