package com.disciples.iam.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.disciples.iam.identity.UserManager;
import com.disciples.iam.identity.cmd.ChangeUserPassword;
import com.disciples.iam.util.SecurityUtils;

@RestController
@RequestMapping("/user")
public class UserController {
    
    @Autowired
    private UserManager userManager;
    
    @GetMapping("/detail")
    public Object detail() {
        return SecurityUtils.getPrincipal();
    }
    
    @PostMapping("/password/change")
    public Object changePassword(@RequestParam String oldPassword, @RequestParam String newPassword) {
    	ChangeUserPassword cmd = new ChangeUserPassword(SecurityUtils.getAuthedUsername(), 
    			oldPassword, newPassword);
    	return userManager.changePassword(cmd);
    }
    
}
