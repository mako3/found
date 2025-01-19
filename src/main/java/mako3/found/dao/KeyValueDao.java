package mako3.found.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class KeyValueDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public String getValue(String key) {
        return jdbcTemplate.queryForObject("select value from found_keyvalue where key = ?", String.class, key);
    }

    public void updateValue(String key, String value) {
        jdbcTemplate.update("update found_keyvalue set value = ? where key = ?", value, key);
    }

}
