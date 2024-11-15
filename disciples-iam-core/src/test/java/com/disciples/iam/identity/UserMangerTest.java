package com.disciples.iam.identity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.disciples.iam.config.ManagerConfig;
import com.disciples.iam.identity.cmd.ChangeUserPassword;
import com.disciples.iam.identity.cmd.RegisterUser;
import com.disciples.iam.identity.cmd.SaveUser;
import com.disciples.iam.identity.domain.User;
import com.disciples.iam.identity.domain.Users;
import com.disciples.iam.util.Md5PasswordEncoder;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {ManagerConfig.class})
public class UserMangerTest {
	
	@Autowired
	Users users;

	@Autowired
	UserManager userManager;
	
	@Test
	@Rollback
	@Transactional
	public void saveUser() {
		SaveUser createUser = new SaveUser("test", "test1", "test1@gmail.com", "13700000001");
		User user = userManager.save(createUser);
		
		String[] commonFields = {"nickname", "email", "phone"};
		assertEquals(createUser.getUsername(), user.getUsername());
		assertThat(user).isEqualToComparingOnlyGivenFields(createUser, commonFields);
		
		SaveUser updateUser = new SaveUser(user.getId(), "updatedNickname", "updatedEmail", "updatedPhone");
		userManager.save(updateUser);
		assertThat(users.findById(user.getId()).get()).isEqualToComparingOnlyGivenFields(updateUser, commonFields);
	}
	
	User registerUser(String username) {
		return userManager.register(new RegisterUser(username, null, "testname", "test@gmail.com", null));
	}
    
    @Test
    @Rollback
    @Transactional
    public void change_resetPassword() {
    	String username = "test";
        String newRawPassword = "1234567";
        Md5PasswordEncoder encoder = new Md5PasswordEncoder();
        String newEncodedPassword = encoder.encode(newRawPassword);
        String defaultEncodedPassword = encoder.encode(UserManager.DEFAULT_RAW_PASSWORD);
        
        User user = registerUser(username);
        userManager.changePassword(new ChangeUserPassword(username, UserManager.DEFAULT_RAW_PASSWORD, newRawPassword));
        assertEquals(newEncodedPassword, users.findById(user.getId()).get().getPassword());
        
        userManager.resetPassword(user.getId());
        assertEquals(defaultEncodedPassword, users.findById(user.getId()).get().getPassword());
    }
    
    @Test
    @Rollback
    @Transactional
	public void enable_disable() {
    	String username = "test";
    	Long userId = registerUser(username).getId();
    	assertTrue(userManager.disable(userId));
    	assertTrue(!users.findById(userId).get().isEnabled());
    	
    	assertTrue(userManager.enable(userId));
    	assertTrue(users.findById(userId).get().isEnabled());
	}
    
    //============ Assert test =============//
    @Test
    public void enableAndDisableAndDeleteAndResetPassword_NullUserId_ShouldThrowException() {
    	Long nullUserId = (Long)null;
    	String message = UserManager.USER_ID_IS_REQUIRED.get();
    	assertThatIllegalArgumentException().isThrownBy(() -> userManager.enable(nullUserId))
    		.withMessage(message);
        assertThatIllegalArgumentException().isThrownBy(() -> userManager.disable(nullUserId))
            .withMessage(message);
        assertThatIllegalArgumentException().isThrownBy(() -> userManager.delete(nullUserId))
        	.withMessage(message);
        assertThatIllegalArgumentException().isThrownBy(() -> userManager.resetPassword(nullUserId))
    		.withMessage(message);
    }
    
}
