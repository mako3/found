CREATE TABLE IF NOT EXISTS gchat_messages1(
  space_id varchar(12) NOT NULL,
  creator_name varchar(40),
  creator_email varchar(40),
  creator_user_type varchar(10),
  created_date timestamp,
  topic_created_date timestamp,
  message_text text,
  topic_id varchar(11),
  message_id varchar(35) NOT NULL, 
  thread_reply boolean DEFAULT false,
  has_reply boolean DEFAULT false,
  PRIMARY KEY (message_id)
);

CREATE INDEX found_messages_index ON gchat_messages1 
USING bm25 (message_id, space_id, message_text, created_date, creator_email)
WITH (
  key_field='message_id',
  text_fields='{
    "message_text":{"tokenizer": {"type": "japanese_lindera"}}
  }'
);

CREATE TABLE IF NOT EXISTS gchat_spaces1 (
  space_id varchar(20) NOT NULL,
  display_name varchar(50) NOT NULL,
  access_state varchar(12),
  last_imported_user varchar(100),
  last_imported_date timestamp,
  member_ids varchar(100)[],
  member_count smallint,
  message_count integer,
  PRIMARY KEY (space_id)
);

CREATE TABLE IF NOT EXISTS found_users (
    username varchar(100) not null,
    password varchar(500) not null,
    role varchar(10) not null,
    last_login timestamp,
    email_for_notification varchar(100),
    email_for_message_identity varchar(100),
    primary key (username)
)
