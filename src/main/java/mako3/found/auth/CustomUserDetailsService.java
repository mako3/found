package mako3.found.auth;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import mako3.found.dao.UserDao;

@Component
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserDao userDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails user = userDao.getByName(username);
        if (user != null) {
            return user;
        }
        throw new UsernameNotFoundException(String.format("%s is not found on the table", username));
    }

    public List<CustomUserDetails> loadAllUsers() {
        return userDao.getAll();
    }

}
