package com.disciples.iam.identity.domain.event;

public class GroupUpdated {
	
	private Long id;
	private String name;
	private String roles;
	
	public GroupUpdated(Long id, String name, String roles) {
		this.id = id;
		this.name = name;
		this.roles = roles;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getRoles() {
		return roles;
	}

}
