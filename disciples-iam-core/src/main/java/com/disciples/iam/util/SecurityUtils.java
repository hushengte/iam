package com.disciples.iam.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

public abstract class SecurityUtils {
	
	/**
	 * Get authentated user
	 * @return Current authentated user
	 */
    public static User getPrincipal() {
        return getPrincipal(User.class);
    }
    
    public static <T> T getPrincipal(Class<T> principalClass) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Object obj = authentication.getPrincipal();
            if (principalClass.isInstance(obj)) {
            	return principalClass.cast(obj);
            }
        }
        return null;
    }
    
    public static String getAuthedUsername() {
        User user = getPrincipal(User.class);
        return user != null ? user.getUsername() : null;
    }

}
