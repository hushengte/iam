package com.disciples.iam.identity.cmd;

public class SaveUser {

	private Long id;
	private String username;
    private String nickname;
    private String email;
    private String phone;
    
    public SaveUser() {}
    
    public RegisterUser buildRegisterUserCmd() {
    	return new RegisterUser(username, null, nickname, email, phone);
    }
    
    public UpdateUser buildUpdateUserCmd() {
    	return new UpdateUser(id, nickname, email, phone);
    }
    
    public SaveUser(String username, String nickname, String email, String phone) {
		this.username = username;
		this.nickname = nickname;
		this.email = email;
		this.phone = phone;
	}

	public SaveUser(Long id, String nickname, String email, String phone) {
		this.id = id;
		this.nickname = nickname;
		this.email = email;
		this.phone = phone;
	}

	public boolean isUpdate() {
    	return id != null;
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
    
}
