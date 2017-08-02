package com.disciples.iam.domain;

import java.util.Collection;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.disciples.iam.GrantedAuthoritySerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@SuppressWarnings("serial")
@Entity
@Table(name = "iam_user")
public class User extends BaseEntity implements UserDetails {
    
	private String username;
    private String password;
    private String email;
    private String phone;
    private String name;
    private String roles;
    private Boolean enabled = Boolean.TRUE;
    private Date createTime;
    
    private Collection<? extends GrantedAuthority> authorities;
    
    public User() {}
    
    public User(Integer id) {
        this.setId(id);
    }
    
    public User(String username, String password, String name, String email, String phone) {
		this.username = username;
		this.password = password;
		this.email = email;
		this.phone = phone;
		this.name = name;
		this.enabled = Boolean.TRUE;
		this.createTime = new Date();
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@JsonIgnore
	public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
		return name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
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

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
		this.authorities = authorities;
	}
	
	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}
	// ===================== UserDetails Implementation ======================
	@Override
	@JsonSerialize(contentUsing = GrantedAuthoritySerializer.class)
	@Transient
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
	
	@Override
	public String getUsername() {
		return username;
	}
	 
    @Override
    @JsonIgnore 
    @Transient
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore 
    @Transient
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonIgnore 
    @Transient
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

}
