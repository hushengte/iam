package com.disciples.iam.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.disciples.feed.Response;
import com.disciples.iam.domain.User;
import com.disciples.iam.service.UserManager;

@RestController
@RequestMapping("/admin/user")
public class UserManageController {

	@Autowired
	private UserManager userManager;
	
	@RequestMapping(value = "list", method = RequestMethod.POST)
    public Object list(@RequestParam int page, @RequestParam int size, Integer field, String keyword) {
		Page<User> pageData = userManager.find(page, size, field, keyword);
        return Response.success(pageData.getContent(), pageData.getTotalElements());
    }
	
    @RequestMapping(value = "add", method = RequestMethod.POST)
    public Object save(@RequestBody User dto) {
        return Response.success(userManager.save(dto));
    }
    
    @RequestMapping(value = "{id}", method = RequestMethod.PUT)
    public Object update(@RequestBody User dto) {
        return Response.success(userManager.save(dto));
    }
    
    @RequestMapping(value = "{id}/groupIds", method = RequestMethod.GET)
    public Object groupIds(@PathVariable Integer id) {
        return Response.success(userManager.groupIds(id));
    }
    
    @RequestMapping(value = "{id}/updateGroups", method = RequestMethod.POST)
    public Object updateGroups(@PathVariable Integer id, @RequestParam List<Integer> groupIds) {
    	userManager.updateGroups(id, groupIds);
        return Response.success(groupIds);
    }
    
    @RequestMapping(value = "{id}/enable", method = RequestMethod.POST)
    public Object enable(@PathVariable Integer id) {
    	userManager.enable(id);
        return Response.success(true);
    }
    
    @RequestMapping(value = "{id}/disable", method = RequestMethod.POST)
    public Object disable(@PathVariable Integer id) {
    	userManager.disable(id);
        return Response.success(true);
    }
    
    @RequestMapping(value = "{id}/resetPassword", method = RequestMethod.POST)
    public Object resetPassword(@PathVariable Integer id) {
    	userManager.resetPassword(id);
        return Response.success(true);
    }
    
}
