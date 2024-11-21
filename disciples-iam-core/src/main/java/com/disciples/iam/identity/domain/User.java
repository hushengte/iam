package com.disciples.iam.identity.domain;

import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.disciples.data.domain.AggregateRoot;
import com.disciples.iam.identity.domain.event.UserDeleted;
import com.disciples.iam.identity.domain.event.UserDisabled;
import com.disciples.iam.identity.domain.event.UserEnabled;
import com.disciples.iam.identity.domain.event.UserPasswordChanged;
import com.disciples.iam.identity.domain.event.UserRegistered;
import com.disciples.iam.identity.domain.event.UserUpdated;

@Table("iam_user")
public class User extends AggregateRoot<User> {

	private String username;
    private String password;
    private String nickname;
    private String email;
    private String phone;
    private Status status;
    
    static enum Status {
		ENABLED, LOCKED, DISABLED;
    	boolean isEnabled() {
    		return ENABLED == Status.this;
    	}
	}
    
    public User() {
    	super(null);
    }
    
	public User(Long id) {
		super(id);
	}
	
	@PersistenceCreator
	public User(Long id, String username, String password, 
			String nickname, String email, String phone) {
		super(id);
    	this.username = username;
    	this.password = password;
    	this.nickname = nickname;
    	this.email = email;
    	this.phone = phone;
    	setStatus(Status.ENABLED);
	}
	
	private void setStatus(Status status) {
		this.status = status;
	}
	
	public User update(String nickname, String email, String phone) {
		this.nickname = nickname;
		this.email = email;
		this.phone = phone;
		registerEvent(new UserUpdated(this.getId(), username, nickname, email, phone));
		return this;
	}
	
	public User registered() {
		registerEvent(new UserRegistered(this.getId(), username, nickname, email, phone));
    	return this;
	}
	
	public void changePassword(String currentPassword, String newPassword) {
		changePassword(currentPassword, newPassword, true);
	}
	
	public User resetPassword(String newPassword) {
		changePassword("[mock_password]", newPassword, false);
    	return this;
	}
	
	private void changePassword(String currentPassword, String newPassword, boolean verifyCurrentPassword) {
		Assert.isTrue(StringUtils.hasText(currentPassword) && StringUtils.hasText(newPassword), 
				() -> "Current password and new password cannot be empty.");
		if (verifyCurrentPassword) {
			boolean isCurrentPasswordCorrect = currentPassword.equals(this.password);
			Assert.isTrue(isCurrentPasswordCorrect, () -> "Current password is incorrect.");
		}
		setPassword(newPassword);
	}
	
	private void setPassword(String password) {
		boolean isChanged = password.equals(this.password);
		this.password = password;
		if (isChanged) {
			registerEvent(new UserPasswordChanged(this.getId(), this.username));
		}
	}

	public User disable() {
		setStatus(Status.DISABLED);
		registerEvent(new UserDisabled(this.getId()));
		return this;
	}
	
	public User enable() {
		setStatus(Status.ENABLED);
		registerEvent(new UserEnabled(this.getId()));
		return this;
	}
	
	public boolean isEnabled() {
		return Status.ENABLED.equals(status);
	}

	public User delete() {
		registerEvent(new UserDeleted(this.getId(), username, password, nickname, email, phone));
    	return this;
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public String getPassword() {
		return this.password;
	}

}
