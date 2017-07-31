package com.disciples.iam;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.disciples.iam.domain.User;

public abstract class SecurityUtils {
	
	/**
     * 获取已认证的用户
     */
    public static User getPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Object obj = authentication.getPrincipal();
            if (obj instanceof User) {
                return (User)obj;
            }
        }
        return null;
    }

}
