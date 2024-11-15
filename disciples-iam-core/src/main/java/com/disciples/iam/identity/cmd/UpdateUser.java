package com.disciples.iam.identity.cmd;

public class UpdateUser {
	
	private Long id;
    private String nickname;
    private String email;
    private String phone;
    
	public UpdateUser(Long id, String nickname, String email, String phone) {
		this.id = id;
		this.nickname = nickname;
		this.email = email;
		this.phone = phone;
	}

	public Long getId() {
		return id;
	}

	public String getNickname() {
		return nickname;
	}

	public String getEmail() {
		return email;
	}

	public String getPhone() {
		return phone;
	}
    
}
