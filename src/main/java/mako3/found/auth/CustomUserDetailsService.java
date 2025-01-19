package mako3.found.auth;

import java.security.SecureRandom;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import mako3.found.dao.SpaceDao;
import mako3.found.dao.UserDao;
import mako3.found.entity.ChatSpace;

@Component
public class CustomUserDetailsService implements UserDetailsService {

    private static Log logger = LogFactory.getLog(CustomUserDetailsService.class);

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

    public String resetPassword(String username) {
        String newPassword = generateRandom(8);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = "{bcrypt}" + encoder.encode(newPassword);
        userDao.updatePassword(username, encodedPassword);
        logger.info(String.format("succeeded to update password for %s", username));
        return newPassword;
    }

    private String generateRandom(int length) {
        SecureRandom secureRandom = new SecureRandom();
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@_+";
        StringBuilder s = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = secureRandom.nextInt(characters.length());
            s.append(characters.charAt(index));
        }
        return s.toString();
    }

}
