package mako3.found.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import mako3.found.auth.CustomUserDetails;

@Component
public class UserDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public CustomUserDetails getByName(String username) {
        return jdbcTemplate.queryForObject("select * from found_users where username = ?", new JdbcRowMapper(),
                username);
    }

    public List<CustomUserDetails> getAll() {
        return jdbcTemplate.query("select * from found_users", new JdbcRowMapper());
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

            return new CustomUserDetails(password, username, role, emailForNotification, emailForMessageIdentity);
        }
    }
}
