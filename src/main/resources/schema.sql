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

CALL paradedb.create_bm25(
  index_name => 'gchat_messages1_index',
  table_name => 'gchat_messages1',
  key_field => 'message_id',
  text_fields => paradedb.field(
	name => 'message_text', 
	tokenizer => paradedb.tokenizer('japanese_lindera')
  ),
  datetime_fields => paradedb.field('created_date')
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
    email_for_notification varchar(100),
    email_for_message_identity varchar(100),
    primary key (username)
)
