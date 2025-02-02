package mako3.found.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import mako3.found.auth.CustomUserDetails;

@Component
public class UserDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private NamedParameterJdbcTemplate namedJdbcTemplate;

    public CustomUserDetails getByName(String username) {
        return jdbcTemplate.queryForObject("select * from found_users where username = ?", new JdbcRowMapper(),
                username);
    }

    public CustomUserDetails getByEmail(String emailForNotification) {
        return jdbcTemplate.queryForObject("select * from found_users where email_for_notification = ?",
                new JdbcRowMapper(), emailForNotification);
    }

    public List<CustomUserDetails> getAll() {
        return jdbcTemplate.query("select * from found_users order by last_login desc", new JdbcRowMapper());
    }

    public void updateLastLogin(String username) {
        jdbcTemplate.update("update found_users set last_login = current_timestamp where username = ?", username);
    }

    public void updateForceChangePassword(String username, boolean forceChangePassword) {
        jdbcTemplate.update("update found_users set force_change_password = ? where username = ?", forceChangePassword,
                username);
    }

    public void updatePassword(String username, String password) {
        jdbcTemplate.update(
                "update found_users set password = ?, last_password_update = current_timestamp where username = ?",
                password, username);
    }

    public void updatePassword(String username, String password, boolean forceChangePassword) {
        jdbcTemplate.update(
                "update found_users set password = ?, last_password_update = current_timestamp, force_change_password = ? where username = ?",
                password, forceChangePassword, username);
    }

    public void insertUser(String username, String password, String role, String emailForNotification,
            String emailForMessageIdentity) throws DuplicateKeyException {
        jdbcTemplate.update(
                "insert into found_users (username, password, role, email_for_notification, email_for_message_identity) values (?, ?, ?, ?, ?)",
                username, password, role, emailForNotification, emailForMessageIdentity);
    }

    public int deleteUser(String username) {
        return jdbcTemplate.update("delete from found_users where username = ?", username);
    }

    public int deleteUsers(List<String> usernames) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();

        String sql = "delete from found_users where ";
        sql += "username IN (:usernames) ";
        parameters.addValue("usernames", usernames);

        return namedJdbcTemplate.update(sql, parameters);
    }

    public class JdbcRowMapper implements RowMapper<CustomUserDetails> {

        @Override
        @Nullable
        public CustomUserDetails mapRow(ResultSet rs, int n) throws SQLException {
            String username = rs.getString("username");
            String password = rs.getString("password");
            String role = rs.getString("role");
            String emailForNotification = rs.getString("email_for_notification");
            String emailForMessageIdentity = rs.getString("email_for_message_identity");
            LocalDateTime lastLogin = rs.getObject("last_login", LocalDateTime.class);
            LocalDateTime lastPasswordUpdate = rs.getObject("last_password_update", LocalDateTime.class);
            boolean forceChangePassowrd = rs.getBoolean("force_change_password");

            return new CustomUserDetails(password, username, role, emailForNotification, emailForMessageIdentity,
                    lastLogin, lastPasswordUpdate, forceChangePassowrd);
        }
    }
}
