package com.disciples.iam;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.disciples.iam.domain.User;

public abstract class SecurityUtils {
	
	/**
	 * 获取已认证的用户
	 * @return 当前登录的用户
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

}
