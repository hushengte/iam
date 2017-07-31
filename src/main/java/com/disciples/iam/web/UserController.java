package com.disciples.iam.web;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.disciples.feed.Response;
import com.disciples.iam.domain.User;
import com.disciples.iam.service.UserManager;

@RestController
@RequestMapping("/user")
public class UserController {
    
    @Autowired
    private UserManager userManager;
    
    //用户详情
    @RequestMapping(value = "detail", method = RequestMethod.GET)
    public Object detail() {
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    	if (authentication == null) {
    		return Response.error("用户信息获取失败，请先登录");
    	}
    	User user = (User)authentication.getPrincipal();
    	User dto = new User(user.getId());
    	BeanUtils.copyProperties(user, dto, "password");
        return Response.success(dto);
    }
    
    //修改密码
    @RequestMapping(value = "changePassword", method = RequestMethod.POST)
    public Object changePassword(@RequestParam String oldPassword, @RequestParam String newPassword) {
    	userManager.changePassword(oldPassword, newPassword);
        return Response.success(true);
    }
    
}
