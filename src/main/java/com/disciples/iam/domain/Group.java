package com.disciples.iam.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "iam_group", indexes = @Index(columnList = "name"))
public class Group extends BaseEntity {
    
    private String name;
    private String roles;
    private Date createTime;
    
    public Group() {}
    
    public Group(Integer id) {
        this.setId(id);
    }
    
    public Group(Integer id, String name, String roles) {
		this.setId(id);
		this.name = name;
		this.roles = roles;
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
