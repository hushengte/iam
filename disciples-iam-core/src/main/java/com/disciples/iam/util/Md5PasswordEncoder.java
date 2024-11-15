package com.disciples.iam.util;

import java.nio.charset.StandardCharsets;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.DigestUtils;

/**
 * It is not recommended to use MD5 for password digest, 
 * we use it for back compatibility.
 * 
 * @see org.springframework.security.crypto.password.PasswordEncoder
 */
public class Md5PasswordEncoder implements PasswordEncoder {

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return encode(rawPassword).equals(encodedPassword);
    }
    
    @Override
    public String encode(CharSequence rawPassword) {
    	byte[] bytes = ((String)rawPassword).getBytes(StandardCharsets.UTF_8);
        return DigestUtils.md5DigestAsHex(bytes);
    }
    
}
