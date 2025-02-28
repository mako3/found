SET paradedb.create_index_memory_budget = 512;
SET timezone TO 'Asia/Tokyo';

CREATE TABLE IF NOT EXISTS found_messages(
  space_id varchar(12) NOT NULL,
  message_id varchar(35) NOT NULL, 
  display_seq integer NOT NULL DEFAULT 0,
  topic_id varchar(11),
  creator_name varchar(80),
  creator_email varchar(80),
  creator_user_type varchar(10),
  created_date timestamp,
  attached_files varchar(200)[],
  topic_created_date timestamp,
  message_text text,
  thread_reply boolean DEFAULT false,
  has_reply boolean DEFAULT false,
  PRIMARY KEY (message_id)
);

CREATE INDEX IF NOT EXISTS found_messages_order_index ON found_messages (space_id, display_seq);

CREATE INDEX IF NOT EXISTS found_messages_index ON found_messages 
USING bm25 (message_id, space_id, message_text, created_date, creator_email)
WITH (
  key_field='message_id',
  text_fields='{
    "message_text":{"tokenizer": {"type": "japanese_lindera"}}
  }'
);

CREATE TABLE IF NOT EXISTS found_spaces (
  space_id varchar(20) NOT NULL,
  display_name varchar(100) NOT NULL,
  access_state varchar(12),
  last_imported_user varchar(100),
  last_imported_date timestamp,
  import_status smallint,
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
    last_password_update timestamp,
    email_for_notification varchar(100),
    email_for_message_identity varchar(100),
    force_change_password boolean,
    primary key (username)
);

CREATE UNIQUE INDEX IF NOT EXISTS found_users_index ON found_users (
  email_for_notification
);


CREATE TABLE IF NOT EXISTS found_keyvalue (
  key char(20),
  value text,
  primary key (key)
);

CREATE TABLE IF NOT EXISTS found_password_reset (
  token varchar(128) not null,
  username varchar(100) not null,
  expired_at timestamp not null,
  primary key (token)
);

CREATE TABLE IF NOT EXISTS found_tasks (
  task_id varchar(128), 
  task_status smallint default 0,
  registered_at timestamp not null,
  registered_by varchar(100),
  finished_at timestamp,
  error_message text,
  primary key (task_id)
);
 