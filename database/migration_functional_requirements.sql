-- Run AFTER schema.sql on an existing college_info_db database.
-- Adds user-linked preferences required by Functional Requirement #3.
USE college_info_db;

CREATE TABLE IF NOT EXISTS app_users (
  user_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(80) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  role ENUM('STUDENT','ADMIN') NOT NULL DEFAULT 'STUDENT',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- Demo student account identity (password hash is a non-login placeholder).
INSERT INTO app_users(user_id,username,password_hash,role)
VALUES (1,'demo_student','DISABLED_DEMO_ACCOUNT','STUDENT')
ON DUPLICATE KEY UPDATE username=VALUES(username);

-- Add user_id to existing preferences, assigning existing rows to demo student.
ALTER TABLE saved_preferences
  ADD COLUMN user_id BIGINT UNSIGNED NOT NULL DEFAULT 1 AFTER preference_id;

ALTER TABLE saved_preferences DROP INDEX uq_preference_choice;
ALTER TABLE saved_preferences DROP INDEX uq_preference_order;
ALTER TABLE saved_preferences
  ADD CONSTRAINT fk_preferences_user FOREIGN KEY (user_id)
    REFERENCES app_users(user_id) ON DELETE CASCADE,
  ADD CONSTRAINT uq_user_preference_choice UNIQUE (user_id, choice_code),
  ADD CONSTRAINT uq_user_preference_order UNIQUE (user_id, preference_order);

-- Audit triggers are installed by database/triggers.sql. Verify them with:
-- SELECT * FROM cutoff_audit ORDER BY audit_id DESC;
