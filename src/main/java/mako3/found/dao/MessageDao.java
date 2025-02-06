package mako3.found.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import io.micrometer.common.util.StringUtils;
import mako3.found.entity.ChatMessage;

@Component
public class MessageDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private NamedParameterJdbcTemplate namedJdbcTemplate;

    private String sanitize(String raw) {
        // sanitized special characters defined at https://docs.paradedb.com/documentation/full-text/overview
        Set<Character> specialChars = Set.of(
                '+', '^', '`', ':', '{', '}', '"', '[', ']', '(', ')', '<', '>', '~', '!', '*', '\\');
        StringBuilder sb = new StringBuilder();
        for (char c : raw.toCharArray()) {
            if (specialChars.contains(c)) {
                sb.append('\\');
            }
            sb.append(c);
        }
        return sb.toString();
    }

    private List<String> preProcess(String rawKeyword) {
        String[] spaceSplittedTermArray = rawKeyword.replaceAll("　", " ").split("\s");
        List<String> spaceSplittedTermList = spaceSplittedTermArray.length == 1 && spaceSplittedTermArray[0].isEmpty()
                ? List.of()
                : List.of(spaceSplittedTermArray);
        List<String> sanitizedTermList = spaceSplittedTermList.stream().map(this::sanitize).toList();
        return sanitizedTermList;
    }

    public List<ChatMessage> findByTerms(List<String> accessibleSpaceIds, String rawKeyword,
            LocalDate startDate, LocalDate endDate, String creatorEmail, int limit) {

        List<String> sanitizedTermList = preProcess(rawKeyword);

        String sql = "SELECT * FROM found_messages WHERE ";
        MapSqlParameterSource parameters = new MapSqlParameterSource();

        // filter by accessible space ids
        if (!accessibleSpaceIds.isEmpty()) {
            sql += "space_id IN (:spaceIds) ";
            parameters.addValue("spaceIds", accessibleSpaceIds, Types.CHAR);
        } else {
            // for irregular situation
            sql += "space_id = '' ";
        }

        // filter by terms
        if (!sanitizedTermList.isEmpty()) {
            sql += "AND message_text @@@ :messageText ";
            parameters.addValue("messageText", String.join(" AND ", sanitizedTermList), Types.VARCHAR);
        }

        // filter by created_date
        if (startDate != null && endDate != null) {
            sql += "AND created_date BETWEEN :startDate AND :endDate ";
            parameters.addValue("startDate", startDate, Types.TIMESTAMP);
            parameters.addValue("endDate", endDate, Types.TIMESTAMP);
        } else if (startDate != null) {
            sql += "AND created_date >= :startDate ";
            parameters.addValue("startDate", startDate, Types.TIMESTAMP);
        } else if (endDate != null) {
            sql += "AND created_date <= :endDate ";
            parameters.addValue("endDate", endDate, Types.TIMESTAMP);
        }

        // filter by creator_email
        if (!StringUtils.isEmpty(creatorEmail)) {
            sql += "AND creator_email = :creatorEmail ";
            parameters.addValue("creatorEmail", creatorEmail, Types.VARCHAR);
        }

        // limit clause
        sql += "limit :limit";
        parameters.addValue("limit", limit, Types.INTEGER);

        return namedJdbcTemplate.query(
                sql,
                parameters, new JdbcRowMapper());
    }

    public List<ChatMessage> list(String spaceId, int seqFrom, int limit) {
        return jdbcTemplate.query(
                "select * from found_messages where space_id = ? and display_seq >= ? order by display_seq asc limit ?",
                new JdbcRowMapper(), spaceId, seqFrom, limit);
    }

    public List<ChatMessage> findByUrl(String messageUrl) {
        String messageId = messageUrl.replace("https://chat.google.com/room/", "");
        return jdbcTemplate.query("select * from found_messages where message_id = ?",
                new JdbcRowMapper(), messageId);
    }

    public void insert(List<ChatMessage> list) {
        list.stream().forEach(e -> jdbcTemplate.update(
                "insert into found_messages (space_id, creator_name, creator_email, creator_user_type, created_date, message_text, topic_id, message_id, thread_reply, has_reply) values (?,?,?,?,?,?,?,?, false, false)",
                e.getSpaceId(),
                e.getCreatorName(),
                e.getCreatorEmail(),
                e.getCreatorUserType(),
                e.getCreatedDate(),
                e.getMessageText(),
                e.getTopicId(),
                e.getMessageId()));
    }

    public boolean checkMessageExistsBySpaceId(String spaceId) {
        return jdbcTemplate.queryForObject("select count(*) from found_messages where space_id = ?",
                Integer.class, spaceId) > 0;
    }

    public int deleteMessagesbySpaceId(String spaceId) {
        return jdbcTemplate.update("delete from found_messages where space_id = ?", spaceId);
    }

    public int updateThreadReplyBySpaceId(String spaceId) {
        return jdbcTemplate.update(
                "UPDATE found_messages parent SET thread_reply = '1' WHERE space_id = ? AND EXISTS (SELECT * FROM found_messages child WHERE parent.space_id = child.space_id AND parent.topic_id = child.topic_id AND child.created_date < parent.created_date);",
                spaceId);
    }

    public int updateDisplaySeq(String spaceId) {
        return jdbcTemplate.update(
                "UPDATE found_messages AS t1 SET display_seq = seq FROM (" + //
                        "  SELECT space_id, message_id, ROW_NUMBER() OVER (ORDER BY topic_created_date ASC, created_date ASC) AS seq"
                        +
                        "  FROM found_messages" + //
                        "  WHERE space_id = ?" + //
                        "  ) AS t2" + //
                        " WHERE t1.message_id = t2.message_id AND t1.space_id = t2.space_id;",
                spaceId);
    }

    public int updateTopicCreatedDateBySpaceId(String spaceId) {
        return jdbcTemplate.update(
                "update found_messages parent set topic_created_date = (select min(child.created_date) from found_messages child where child.space_id = parent.space_id AND child.topic_id = parent.topic_id and child.thread_reply is not true) where parent.space_id=?;",
                spaceId);
    }

    public int updateHasReplyBySpaceId(String spaceId) {
        return jdbcTemplate.update(
                "update found_messages parent set has_reply = '1' where space_id = ? AND thread_reply is false and exists (select * from found_messages child where parent.space_id = child.space_id and parent.topic_id = child.topic_id and child.thread_reply is true);",
                spaceId);
    }

    public class JdbcRowMapper implements RowMapper<ChatMessage> {

        @Override
        @Nullable
        public ChatMessage mapRow(ResultSet rs, int n) throws SQLException {
            return ChatMessage.builder()
                    .spaceId(rs.getString("space_id"))
                    .creatorName(rs.getString("creator_name"))
                    .creatorEmail(rs.getString("creator_email"))
                    .creatorUserType(rs.getString("creator_user_type"))
                    .createdDate(rs.getObject("created_date", LocalDateTime.class))
                    .messageText(rs.getString("message_text"))
                    .topicId(rs.getString("topic_id"))
                    .messageId(rs.getString("message_id"))
                    .threadReply(rs.getBoolean("thread_reply"))
                    .hasReply(rs.getBoolean("has_reply"))
                    .displaySeq(rs.getInt("display_seq"))
                    .build();
        }

    }

}
