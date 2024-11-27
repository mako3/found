package mako3.found.auth;

import java.util.Collection;
import java.util.Set;

import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserDetails implements UserDetails, CredentialsContainer {

    private String password;
    private final String username;
    private final String role;
    private final String emailForNotification;
    private final String emailForMessageIdentity;

    public CustomUserDetails(String password, String username, String role, String emailForNotification,
            String emailForMessageIdentity) {
        this.password = password;
        this.username = username;
        this.role = role;
        this.emailForNotification = emailForNotification;
        this.emailForMessageIdentity = emailForMessageIdentity;
    }

    @Override
    public void eraseCredentials() {
        this.password = null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Set.of(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    public String getEmailForNotification() {
        return emailForNotification;
    }

    public String getEmailForMessageIdentity() {
        return emailForMessageIdentity;
    }

    public String getRole() {
        return role;
    }

}
