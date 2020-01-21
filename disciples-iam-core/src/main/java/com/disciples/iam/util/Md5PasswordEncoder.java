package com.disciples.iam.util;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * It is not recommend to use MD5 for password digest, 
 * we use it for back compatibility.
 * 
 * @author Ted Smith
 * @see org.springframework.security.crypto.password.PasswordEncoder
 */
public class Md5PasswordEncoder implements PasswordEncoder {

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return encode(rawPassword).equals(encodedPassword);
    }
    
    @Override
    public String encode(CharSequence rawPassword) {
        return DigestUtils.md5Hex((String)rawPassword);
    }
    
}
