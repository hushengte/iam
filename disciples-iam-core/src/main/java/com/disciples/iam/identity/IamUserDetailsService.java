package com.disciples.iam.identity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.disciples.iam.identity.domain.Group;
import com.disciples.iam.identity.domain.Groups;
import com.disciples.iam.identity.domain.User;
import com.disciples.iam.identity.domain.Users;

public class IamUserDetailsService implements UserDetailsService {

	private final Users users;
    private final Groups groups;
    
    public IamUserDetailsService(Users users, Groups groups) {
        Assert.notNull(users, "User repository is required.");
        Assert.notNull(groups, "Group repository is required.");
        
        this.users = users;
        this.groups = groups;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = users.findByUsername(username)
        		.orElseThrow(() -> new UsernameNotFoundException("User does not exist, username=" + username));
        
        Set<GrantedAuthority> authorities = new HashSet<>();
        List<String> groupRoles = getGroupRoles(user.getId());
        for (String groupRole : groupRoles) {
            authorities.addAll(getAuthorities(groupRole));
        }
        return new org.springframework.security.core.userdetails.User(username, user.getPassword(), 
        		user.isEnabled(), true, true, true, authorities);
    }
    
    private List<String> getGroupRoles(Long userId) {
    	List<Long> groupIds = groups.findGroupIdsByUserId(userId);
    	if (CollectionUtils.isEmpty(groupIds)) {
    		return Collections.emptyList();
    	}
    	Iterable<Group> groupIt = groups.findAllById(groupIds);
    	return StreamSupport.stream(groupIt.spliterator(), false)
    		.map(Group::getRoles)
    		.collect(Collectors.toList());
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
