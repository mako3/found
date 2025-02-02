package mako3.found.auth;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import mako3.found.entity.ChatSpace;

public class CustomUserDetails implements UserDetails, CredentialsContainer {

    private String password;
    private final String username;
    private final String role;
    private final String emailForNotification;
    private final String emailForMessageIdentity;
    private final LocalDateTime lastLogin;
    private final LocalDateTime lastPasswordUpdate;
    private List<ChatSpace> memberSpaces;
    private boolean forceChangePassowrd;

    public CustomUserDetails(String password, String username, String role, String emailForNotification,
            String emailForMessageIdentity, LocalDateTime lastLogin, LocalDateTime lastPasswordUpdate,
            boolean forceChangePassowrd) {
        this.password = password;
        this.username = username;
        this.role = role;
        this.emailForNotification = emailForNotification;
        this.emailForMessageIdentity = emailForMessageIdentity;
        this.lastLogin = lastLogin;
        this.lastPasswordUpdate = lastPasswordUpdate;
        this.forceChangePassowrd = forceChangePassowrd;
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

    public void setMemberSpaces(List<ChatSpace> memberSpaces) {
        this.memberSpaces = memberSpaces;
    }

    public List<ChatSpace> getMemberSpaces() {
        return memberSpaces;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public LocalDateTime getLastPasswordUpdate() {
        return lastPasswordUpdate;
    }

    public boolean getForceChangePassword() {
        return forceChangePassowrd;
    }

}
