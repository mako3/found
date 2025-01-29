package mako3.found.auth;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import mako3.found.dao.PasswordResetDao;
import mako3.found.dao.SpaceDao;
import mako3.found.dao.UserDao;
import mako3.found.entity.ChatSpace;
import mako3.found.entity.NewUser;
import mako3.found.entity.PasswordResetToken;
import mako3.found.mail.MailSendingException;
import mako3.found.mail.PasswordMailSenderService;

@Component
public class CustomUserDetailsService implements UserDetailsService {

    private static Log logger = LogFactory.getLog(CustomUserDetailsService.class);

    private static final int DEFAULT_PASSWORD_LENGTH = 8;

    @Autowired
    private UserDao userDao;

    @Autowired
    private SpaceDao spaceDao;

    @Autowired
    private PasswordResetDao resetDao;

    @Autowired
    private PasswordMailSenderService mailService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            CustomUserDetails user = userDao.getByName(username);
            List<ChatSpace> memberSpaces = spaceDao.findByMember(user.getEmailForMessageIdentity());
            user.setMemberSpaces(memberSpaces);
            return user;
        } catch (EmptyResultDataAccessException e) {
            throw new UsernameNotFoundException(String.format("%s is not found on the table", username));
        }
    }

    public Optional<String> resolveUsernameIfExists(String email) {
        try {
            CustomUserDetails user = userDao.getByEmail(email);
            return Optional.of(user.getUsername());
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<CustomUserDetails> loadAllUsers() {
        return userDao.getAll();
    }

    public void updateLastLogin(String username) {
        userDao.updateLastLogin(username);
    }

    public void resetPasswordWithMail(String username, String emailForNotification) throws MailSendingException {
        String newPassword = generateRandom(DEFAULT_PASSWORD_LENGTH);
        mailService.sendPasswordMail(emailForNotification, username, newPassword);
        updatePassword(username, newPassword);
    }

    public void recordTokenWithMail(String username, String emailForNotification)
            throws MailSendingException {
        String token = UUID.randomUUID().toString();
        mailService.sendResetTokenMail(emailForNotification, token);
        resetDao.insertToken(token, username);
    }

    public Optional<PasswordResetToken> findToken(String token) {
        try {
            PasswordResetToken resetToken = resetDao.findByToken(token);
            return Optional.of(resetToken);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void invalidateToken(String token) {
        resetDao.deleteToken(token);
    }

    public void updatePassword(String username, String newPassword) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = "{bcrypt}" + encoder.encode(newPassword);
        userDao.updatePassword(username, encodedPassword);
        logger.info(String.format("succeeded to update password for %s", username));
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

    public void addUser(NewUser newUser) throws DuplicateKeyException {
        String password = generateRandom(DEFAULT_PASSWORD_LENGTH);
        userDao.insertUser(newUser.getUsername(), password, newUser.getRole(), newUser.getEmailForNotification(),
                newUser.getEmailForMessageIdentity());
    }

    public void deleteUser(String username) {
        userDao.deleteUser(username);
    }

}
