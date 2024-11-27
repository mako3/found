package mako3.found.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import mako3.found.entity.ChatMessage;

@Component
public class MessageDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<ChatMessage> find(String messageText) {
        return jdbcTemplate.query(
                "select space_id,creator_name,created_date,creator_email,creator_user_type,paradedb.snippet(message_text) as message_text,topic_id,message_id,thread_reply,has_reply from gchat_messages1 where message_text @@@ ?",
                new DataClassRowMapper<>(ChatMessage.class), messageText);
    }

    public List<ChatMessage> list(String spaceId, int limit) {
        return jdbcTemplate.query(
                "select * from gchat_messages1 where space_id = ? order by topic_created_date asc, created_date asc limit ?",
                new DataClassRowMapper<>(ChatMessage.class), spaceId, limit);
    }

    public List<ChatMessage> findByUrl(String messageUrl) {
        String messageId = messageUrl.replace("https://chat.google.com/room/", "");
        return jdbcTemplate.query("select * from gchat_messages1 where message_id = ?",
                new DataClassRowMapper<>(ChatMessage.class), messageId);
    }

    public void insert(List<ChatMessage> list) {
        list.stream().forEach(e -> jdbcTemplate.update(
                "insert into gchat_messages1 (space_id, creator_name, creator_email, creator_user_type, created_date, message_text, topic_id, message_id, thread_reply, has_reply) values (?,?,?,?,?,?,?,?, false, false)",
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
        return jdbcTemplate.queryForObject("select count(*) from gchat_messages1 where space_id = ?",
                Integer.class, spaceId) > 0;
    }

    public int deleteMessagesbySpaceId(String spaceId) {
        return jdbcTemplate.update("delete from gchat_messages1 where space_id = ?", spaceId);
    }

    public int updateThreadReplyBySpaceId(String spaceId) {
        return jdbcTemplate.update(
                "UPDATE gchat_messages1 parent SET thread_reply = '1' WHERE space_id = ? AND EXISTS (SELECT * FROM gchat_messages1 child WHERE parent.space_id = child.space_id AND parent.topic_id = child.topic_id AND child.created_date < parent.created_date);",
                spaceId);
    }

    public int updateTopicCreatedDateBySpaceId(String spaceId) {
        return jdbcTemplate.update(
                "update gchat_messages1 parent set topic_created_date = (select min(child.created_date) from gchat_messages1 child where child.space_id = parent.space_id AND child.topic_id = parent.topic_id and child.thread_reply is not true) where parent.space_id=?;",
                spaceId);
    }

    public int updateHasReplyBySpaceId(String spaceId) {
        return jdbcTemplate.update(
                "update gchat_messages1 parent set has_reply = '1' where space_id = ? AND thread_reply is false and exists (select * from gchat_messages1 child where parent.space_id = child.space_id and parent.topic_id = child.topic_id and child.thread_reply is true);",
                spaceId);
    }

}
