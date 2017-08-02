package com.disciples.iam.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.disciples.iam.domain.Group;
import com.disciples.iam.service.GroupManager;
import com.disciples.iam.service.UserManager;

@RestController
@RequestMapping("/admin/group")
public class GroupManageController {
	
	@Autowired
	private GroupManager groupManager;
	@Autowired
	private UserManager userManager;
	
	@RequestMapping(value = "keyValues", method = RequestMethod.GET)
    public Object keyValues() {
    	List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
    	for (Map<String, Object> dto : groupManager.keyValues()) {
    		Map<String, Object> map = new HashMap<String, Object>();
    		map.put("key", dto.get("id"));
    		map.put("value", dto.get("name"));
    		result.add(map);
    	}
        return Response.success(result, result.size());
    }
	
	@RequestMapping(value = "list", method = RequestMethod.POST)
    public Object list(@RequestParam int page, @RequestParam int size, String keyword) {
		Page<Group> pageData = groupManager.find(page, size, keyword);
        return Response.success(pageData.getContent(), pageData.getTotalElements());
    }
	
    @RequestMapping(value = "add", method = RequestMethod.POST)
    public Object save(@RequestBody Group dto) {
        return Response.success(groupManager.save(dto));
    }
    
    @RequestMapping(value = "{id}", method = RequestMethod.PUT)
    public Object update(@RequestBody Group dto) {
        return Response.success(groupManager.save(dto));
    }
    
    @RequestMapping(value = "delete", method = RequestMethod.POST)
    public Object delete(@RequestParam Integer groupId) {
    	groupManager.delete(groupId);
        return Response.success(true);
    }
    
    @RequestMapping(value = "{id}/users", method = RequestMethod.POST)
    public Object users(@PathVariable Integer groupId) {
        return Response.success(userManager.find(groupId));
    }
    
}
