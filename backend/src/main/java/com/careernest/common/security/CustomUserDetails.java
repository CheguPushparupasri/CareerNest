package com.careernest.common.security;

import com.careernest.modules.user.entity.AccountStatus;
import com.careernest.modules.user.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {
    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    public Long getId() { return user.getId(); }
    public String getPublicId() { return user.getPublicId(); }
    public User getUser() { return user; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().getName().name()));
    }

    @Override
    public String getPassword() { return user.getPasswordHash(); }

    @Override
    public String getUsername() { return user.getEmail(); }

    @Override
    public boolean isAccountNonExpired() { return user.getAccountStatus() != AccountStatus.DELETED; }

    @Override
    public boolean isAccountNonLocked() { return user.getAccountStatus() != AccountStatus.LOCKED; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return user.getAccountStatus() == AccountStatus.ACTIVE; }
}
