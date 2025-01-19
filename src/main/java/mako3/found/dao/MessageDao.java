package mako3.found.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

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

    public List<ChatMessage> findByTerms(List<String> accessibleSpaceIds, List<String> sanitizedTermList,
            LocalDate startDate, LocalDate endDate, String creatorEmail, int limit) {

        String sql = "SELECT * FROM found_messages WHERE ";
        MapSqlParameterSource parameters = new MapSqlParameterSource();

        // filter by accessible space ids
        if (!accessibleSpaceIds.isEmpty()) {
            sql += "space_id IN (:spaceIds) ";
            parameters.addValue("spaceIds", accessibleSpaceIds);
        } else {
            // for irregular situation
            sql += "space_id = '' ";
        }

        // filter by terms
        if (!sanitizedTermList.isEmpty()) {
            sql += "AND message_text @@@ :messageText ";
            parameters.addValue("messageText", String.join(" AND ", sanitizedTermList));
        }

        // filter by created_date
        if (startDate != null && endDate != null) {
            sql += "AND created_date BETWEEN :startDate AND :endDate ";
            parameters.addValue("startDate", startDate);
            parameters.addValue("endDate", endDate);
        } else if (startDate != null) {
            sql += "AND created_date >= :startDate ";
            parameters.addValue("startDate", startDate);
        } else if (endDate != null) {
            sql += "AND created_date <= :endDate ";
            parameters.addValue("endDate", endDate);
        }

        // filter by creator_email
        if (!StringUtils.isEmpty(creatorEmail)) {
            sql += "AND creator_email = :creatorEmail ";
            parameters.addValue("creatorEmail", creatorEmail);
        }

        // limit clause
        sql += "limit :limit";
        parameters.addValue("limit", limit);

        return namedJdbcTemplate.query(
                sql,
                parameters, new JdbcRowMapper());
    }

    public List<ChatMessage> list(String spaceId, int limit) {
        return jdbcTemplate.query(
                "select * from found_messages where space_id = ? order by topic_created_date asc, created_date asc limit ?",
                new JdbcRowMapper(), spaceId, limit);
    }

    public List<ChatMessage> listFrom(String spaceId, LocalDateTime dateFrom, int limit) {
        return jdbcTemplate.query(
                "select * from found_messages where space_id = ? and created_date >= ? order by topic_created_date asc, created_date asc limit ?",
                new JdbcRowMapper(), spaceId, dateFrom, limit);
    }

    public List<ChatMessage> listBefore(String spaceId, LocalDateTime dateBefore, int limit) {
        List<ChatMessage> list = jdbcTemplate.query(
                "select * from found_messages where space_id = ? and created_date < ? order by topic_created_date desc, created_date desc limit ?",
                new JdbcRowMapper(), spaceId, dateBefore, limit);
        Collections.reverse(list);
        return list;
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
                    .build();
        }

    }

}
