package mako3.found.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import mako3.found.entity.PasswordResetToken;

@Component
public class PasswordResetDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void insertToken(String token, String username) {
        jdbcTemplate.update(
                "insert into found_password_reset (token, username, expired_at) values (?, ?, current_timestamp + interval '24 hours')",
                token,
                username);
    }

    public PasswordResetToken findByToken(String token) {
        return jdbcTemplate.queryForObject(
                "select token, username from found_password_reset where token = ? and expired_at > current_timestamp",
                new DataClassRowMapper<>(PasswordResetToken.class),
                token);
    }

    public void deleteToken(String token) {
        jdbcTemplate.update("delete from found_password_reset where token = ?", token);
    }

}
