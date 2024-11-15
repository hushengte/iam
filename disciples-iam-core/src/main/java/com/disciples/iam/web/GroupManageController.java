package com.disciples.iam.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.disciples.iam.identity.GroupManager;
import com.disciples.iam.identity.IdentityQueryService;
import com.disciples.iam.identity.cmd.SaveGroup;

@RestController
@RequestMapping("/admin/group")
public class GroupManageController {
	
	@Autowired
	private IdentityQueryService identityQueryService;
	
	@Autowired
	private GroupManager groupManager;
	
	@GetMapping("/keyValues")
    public Object keyValues() {
        return identityQueryService.findGroupKeyValues();
    }
	
	@PostMapping("/list")
    public Object list(@RequestParam Integer page, @RequestParam Integer size, String keyword) {
		return identityQueryService.findPagedGroups(page, size, keyword);
    }
	
    @PostMapping("/save")
    public Object save(@RequestBody SaveGroup dto) {
        return groupManager.save(dto);
    }
    
    @PostMapping("/delete")
    public Object delete(@RequestParam Long groupId) {
    	return groupManager.delete(groupId);
    }
    
}
