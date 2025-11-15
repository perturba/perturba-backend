package com.hyunwoosing.perturba.common.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public record AuthPrincipal(
        Long userId,
        Long guestId,
        Long apiKeyId,
        String username,
        Collection<? extends GrantedAuthority> authorities
) implements UserDetails {

    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
    @Override public String getPassword() { return null; }
    @Override public String getUsername() { return username; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }

    public boolean isUser()  { return userId  != null; }
    public boolean isGuest() { return guestId != null; }
    public boolean isApi()   { return apiKeyId != null; }
}
