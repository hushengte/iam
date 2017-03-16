package com.disciples.iam.domain;

import java.util.Date;

import com.disciples.feed.AbstractModel;

@SuppressWarnings("serial")
public class Group extends AbstractModel {
    
    private String name;
    private String roles;
    private Date createTime;
    
    public Group() {}
    
    public Group(Integer id) {
        this.setId(id);
    }
    
    public Group(Integer id, String name) {
		this.setId(id);
		this.name = name;
	}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getRoles() {
		return roles;
	}

	public void setRoles(String roles) {
		this.roles = roles;
	}

}
