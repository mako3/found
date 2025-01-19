INSERT INTO found_users (username, password, role) VALUES ('root', 'admin', 'ADMIN') ON CONFLICT DO NOTHING;
INSERT INTO found_keyvalue (key, value) VALUES ('mail.subject', '[found] New Account') ON CONFLICT DO NOTHING;
INSERT INTO found_keyvalue (key, value) VALUES ('mail.body', 'ようこそ') ON CONFLICT DO NOTHING;
INSERT INTO found_keyvalue (key, value) VALUES ('mail.username', '') ON CONFLICT DO NOTHING;
INSERT INTO found_keyvalue (key, value) VALUES ('mail.password', '') ON CONFLICT DO NOTHING;
INSERT INTO found_keyvalue (key, value) VALUES ('mail.from', '') ON CONFLICT DO NOTHING;
INSERT INTO found_keyvalue (key, value) VALUES ('mail.port', '25') ON CONFLICT DO NOTHING;
INSERT INTO found_keyvalue (key, value) VALUES ('mail.host', '') ON CONFLICT DO NOTHING;
INSERT INTO found_keyvalue (key, value) VALUES ('notice', '') ON CONFLICT DO NOTHING;