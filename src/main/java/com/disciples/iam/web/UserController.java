package com.disciples.iam.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.disciples.iam.SecurityUtils;
import com.disciples.iam.service.UserManager;

@RestController
@RequestMapping("/user")
public class UserController {
    
    @Autowired
    private UserManager userManager;
    
    //用户详情
    @RequestMapping(value = "detail", method = RequestMethod.GET)
    public Object detail() {
        return Response.success(SecurityUtils.getPrincipal());
    }
    
    //修改密码
    @RequestMapping(value = "changePassword", method = RequestMethod.POST)
    public Object changePassword(@RequestParam String oldPassword, @RequestParam String newPassword) {
    	userManager.changePassword(oldPassword, newPassword);
        return Response.success(true);
    }
    
}
