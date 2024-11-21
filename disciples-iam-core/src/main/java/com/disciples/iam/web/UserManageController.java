package com.disciples.iam.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.disciples.iam.identity.IdentityQueryService;
import com.disciples.iam.identity.UserManager;
import com.disciples.iam.identity.cmd.SaveUser;
import com.disciples.iam.identity.cmd.UpdateUserGroups;

@RestController
@RequestMapping("/admin/user")
public class UserManageController {
	
	@Autowired
	private IdentityQueryService identityQueryService;
	
	@Autowired	
	private UserManager userManager;
	
	@PostMapping("/list")
    public Object list(@RequestParam Integer page, @RequestParam Integer size, Long field, String keyword) {
		return identityQueryService.findPagedUsers(page, size, field, keyword);
    }
	
    @PostMapping("/save")
    public Object save(@RequestBody SaveUser dto) {
        return userManager.save(dto);
    }
    
    @PostMapping("/enable")
    public Object enable(@RequestParam Long id) {
    	return userManager.enable(id);
    }
    
    @PostMapping("/disable")
    public Object disable(@RequestParam Long id) {
    	return userManager.disable(id);
    }
    
    @PostMapping("/password/reset")
    public Object resetPassword(@RequestParam Long id) {
    	return userManager.resetPassword(id);
    }
    
    @PostMapping("/delete")
    public Object delete(@RequestParam("ids") List<Long> ids) {
        userManager.delete(ids);
        return ids.size();
    }
    
    @GetMapping("/group/ids")
    public Object groupIds(@RequestParam Long id) {
        return identityQueryService.findGroupIds(id);
    }
    
    @PostMapping("/group/update")
    public Object updateGroups(@RequestBody UpdateUserGroups dto) {
    	return userManager.updateGroups(dto);
    }
    
}
