package com.disciples.iam.identity.domain;

import com.disciples.data.identifier.LongId;

public class UserFactory {

	private final Users users;
	
	public UserFactory(Users users) {
		this.users = users;
	}
	
	public User build(String username, String password, 
			String nickname, String email, String phone) {
		Long existId = users.getIdByUsername(username);
		if (existId != null) {
			throw new UserAlreadyExistsException(existId, username);
		}
		
		User user = new User(LongId.generate(), username, password,
				nickname, email, phone).markAsNew();
		return users.save(user.registered());
	}
	
}
