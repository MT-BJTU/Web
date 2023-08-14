package mycom.model;

import mycom.bean.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class DetailedUser implements UserDetails {
    private Long userId;
    private String userName;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    public DetailedUser(Long userId, String userName, String password, Collection<? extends GrantedAuthority> authorities) {
        this.userId = userId;
        this.userName = userName;
        this.password = password;
        this.authorities = authorities;
    }

    public DetailedUser(User user) {
        this.userId=user.getUserId();
        this.userName=user.getUserName();
        this.password=user.getPassword();
    }

    public Long getUserId() {
        return userId;
    }

    @Override
    public String getUsername() {
        return userName;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    // Implement the remaining methods from the UserDetails interface

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
