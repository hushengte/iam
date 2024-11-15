package com.disciples.iam.web;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.disciples.iam.util.SecurityUtils;

/**
 * OAuth2 protected
 */
@RestController
@RequestMapping("/api")
public class ApiController {
    
    @GetMapping("/user/info")
    public Object info() {
    	return SecurityUtils.getPrincipal(UserDetails.class);
    }
    
}
