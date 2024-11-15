package com.disciples.iam.identity.cmd;

public class SaveGroup {
	
	private Long id;
	private String name;
	private String roles;
	
	public SaveGroup() {}

	public SaveGroup(Long id, String name, String roles) {
		this.id = id;
		this.name = name;
		this.roles = roles;
	}
	
	public boolean isUpdate() {
		return this.id != null;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRoles() {
		return roles;
	}

	public void setRoles(String roles) {
		this.roles = roles;
	}
	
}
