package com.disciples.iam.web;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.disciples.iam.SecurityUtils;

/**
 * OAuth2 protected
 */
@RestController
@RequestMapping("/api")
public class ApiController {
    
    @RequestMapping(value = "user/info", method = RequestMethod.GET)
    public Object info() {
    	return SecurityUtils.getPrincipal(UserDetails.class);
    }
    
}
