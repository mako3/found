package mako3.found.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class SchemaDao {

    @Autowired
    private JdbcTemplate jdbcTempplate;

    public void dropBM25Index() {
        jdbcTempplate.execute("drop index if exists found_messages_index");
    }

    public void createBM25Index() {
        jdbcTempplate.execute(
                """
                        CREATE INDEX IF NOT EXISTS found_messages_index ON found_messages
                        USING bm25 (message_id, space_id, message_text, created_date, creator_email)
                        WITH (
                            key_field='message_id',
                            text_fields='{
                            "message_text":{"tokenizer": {"type": "japanese_lindera"}}
                        }')""");
    }

}
