package mako3.found.auth;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import mako3.found.dao.SpaceDao;
import mako3.found.dao.UserDao;
import mako3.found.entity.ChatSpace;

@Component
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private SpaceDao spaceDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        CustomUserDetails user = userDao.getByName(username);
        if (user != null) {
            List<ChatSpace> memberSpaces = spaceDao.findByMember(user.getEmailForMessageIdentity());
            user.setMemberSpaces(memberSpaces);
            return user;
        }
        throw new UsernameNotFoundException(String.format("%s is not found on the table", username));
    }

    public List<CustomUserDetails> loadAllUsers() {
        return userDao.getAll();
    }

    public void updateLastLogin(String useString) {
        userDao.updateLastLogin(useString);
    }

}
