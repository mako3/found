package mako3.found.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import mako3.found.entity.ChatSpace;

@Component
public class SpaceDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<ChatSpace> findByName(String displayName) {
        return jdbcTemplate.query("select * from gchat_spaces1 where display_name like ?",
                new JdbcRowMapper(), "%" + displayName + "%");
    }

    public List<ChatSpace> findByMember(String memberId) {
        return jdbcTemplate.query("select * from gchat_spaces1 where ? = any(member_ids);",
                new JdbcRowMapper(), memberId);
    }

    public List<ChatSpace> findAll() {
        return jdbcTemplate.query("select * from gchat_spaces1",
                new JdbcRowMapper());
    }

    public ChatSpace findOne(String spaceId) {
        return jdbcTemplate.queryForObject("select * from gchat_spaces1 where space_id = ?",
                new JdbcRowMapper(), spaceId);
    }

    public void updateMemberIds(String spaceId, List<String> memberIds) {
        String[] array = memberIds.toArray(new String[0]);
        jdbcTemplate.update("update gchat_spaces1 set member_ids = ?, member_count = ?  where space_id = ?",
                array, memberIds.size(), spaceId);
    }

    public void updateLastImported(String spaceId, String executorName) {
        jdbcTemplate.update(
                "update gchat_spaces1 set last_imported_date = current_timestamp, last_imported_user = ? where space_id = ?",
                executorName, spaceId);
    }

    public void updateMessageCount(String spaceId, int messageCount) {
        jdbcTemplate.update("update gchat_spaces1 set message_count = ? where space_id = ?",
                messageCount, spaceId);
    }

    public class JdbcRowMapper implements RowMapper<ChatSpace> {

        @Override
        @Nullable
        public ChatSpace mapRow(@SuppressWarnings("null") ResultSet rs, int n) throws SQLException {
            return ChatSpace.builder()
                    .spaceId(rs.getString("space_id"))
                    .displayName(rs.getString("display_name"))
                    .accessState(rs.getString("access_state"))
                    .lastImportedUser(rs.getString("last_imported_user"))
                    .lastImportedDate(rs.getObject("last_imported_date", LocalDateTime.class))
                    .memberIds(Arrays.asList((String[]) rs.getArray("member_ids").getArray()))
                    .memberCount(rs.getInt("member_count"))
                    .messageCount(rs.getInt("message_count"))
                    .build();
        }

    }

}
