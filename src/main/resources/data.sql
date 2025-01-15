INSERT INTO found_users (username, password, role) VALUES ('root', 'admin', 'ADMIN') ON CONFLICT DO NOTHING;
