-- MHT-CET College Finder
-- Canonical schema based on the uploaded DDL, adapted to preserve leading-zero
-- institute codes and the fields in the uploaded cutoff CSV.
-- Run this first in MySQL Workbench.
CREATE DATABASE IF NOT EXISTS college_info_db
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;
USE college_info_db;

CREATE TABLE IF NOT EXISTS cities (
  city_id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  city_name VARCHAR(100) NOT NULL UNIQUE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS colleges (
  institute_code VARCHAR(20) PRIMARY KEY,
  institute_name VARCHAR(255) NOT NULL,
  city_id INT UNSIGNED NOT NULL,
  CONSTRAINT fk_colleges_city FOREIGN KEY (city_id)
    REFERENCES cities(city_id)
    ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS courses (
  course_id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  course_name VARCHAR(150) NOT NULL UNIQUE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS college_courses (
  choice_code VARCHAR(20) PRIMARY KEY,
  institute_code VARCHAR(20) NOT NULL,
  course_id INT UNSIGNED NOT NULL,
  CONSTRAINT fk_cc_colleges FOREIGN KEY (institute_code)
    REFERENCES colleges(institute_code)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_cc_courses FOREIGN KEY (course_id)
    REFERENCES courses(course_id)
    ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT uq_institute_course UNIQUE (institute_code, course_id)
) ENGINE=InnoDB;

-- Preserve exam/seat/rank/score from the flat CSV.
-- percentile is populated ONLY for verified MHT-CET percentile rows.
CREATE TABLE IF NOT EXISTS cutoffs (
  cutoff_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  choice_code VARCHAR(20) NOT NULL,
  year SMALLINT UNSIGNED NOT NULL,
  cap_round TINYINT UNSIGNED NOT NULL,
  merit_exam VARCHAR(30) NOT NULL,
  allotment_type VARCHAR(20) NOT NULL,
  seat_type VARCHAR(20) NOT NULL,
  category VARCHAR(20) NOT NULL DEFAULT 'UNKNOWN',
  merit_rank INT UNSIGNED NULL,
  merit_score DECIMAL(12,7) NULL,
  percentile DECIMAL(9,6) NULL,
  CONSTRAINT fk_cutoffs_choice FOREIGN KEY (choice_code)
    REFERENCES college_courses(choice_code)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT uq_cutoff_entry UNIQUE
    (choice_code, year, cap_round, merit_exam, allotment_type, seat_type),
  CONSTRAINT chk_percentile_range CHECK
    (percentile IS NULL OR (percentile >= 0 AND percentile <= 100)),
  CONSTRAINT chk_merit_rank CHECK (merit_rank IS NULL OR merit_rank > 0)
) ENGINE=InnoDB;

-- User identities support user-specific college preferences.
CREATE TABLE IF NOT EXISTS app_users (
  user_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(80) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  role ENUM('STUDENT','ADMIN') NOT NULL DEFAULT 'STUDENT',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

INSERT INTO app_users(user_id, username, password_hash, role)
VALUES (1, 'demo_student', 'DISABLED_DEMO_ACCOUNT', 'STUDENT')
ON DUPLICATE KEY UPDATE username=VALUES(username);

CREATE TABLE IF NOT EXISTS saved_preferences (
  preference_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT UNSIGNED NOT NULL DEFAULT 1,
  choice_code VARCHAR(20) NOT NULL,
  preference_order INT UNSIGNED NOT NULL,
  saved_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_pref_user FOREIGN KEY (user_id)
    REFERENCES app_users(user_id) ON DELETE CASCADE,
  CONSTRAINT fk_pref_choice FOREIGN KEY (choice_code)
    REFERENCES college_courses(choice_code)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT uq_user_preference_choice UNIQUE (user_id, choice_code),
  CONSTRAINT uq_user_preference_order UNIQUE (user_id, preference_order)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS cutoff_audit (
  audit_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  cutoff_id BIGINT UNSIGNED NULL,
  choice_code VARCHAR(20) NOT NULL,
  action_type ENUM('INSERT','UPDATE','DELETE') NOT NULL,
  old_percentile DECIMAL(9,6) NULL,
  new_percentile DECIMAL(9,6) NULL,
  old_merit_score DECIMAL(12,7) NULL,
  new_merit_score DECIMAL(12,7) NULL,
  changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  changed_by VARCHAR(288) NOT NULL DEFAULT (CURRENT_USER())
) ENGINE=InnoDB;

-- Temporary import landing table, matching setup_cutoff_flat CSV columns.
-- Import the CSV into this table using MySQL Workbench's Table Data Import Wizard.
CREATE TABLE IF NOT EXISTS cutoff_import_raw (
  year SMALLINT NOT NULL,
  cap_round TINYINT NOT NULL,
  sr_no INT NOT NULL,
  institute_code VARCHAR(20) NOT NULL,
  institute_name VARCHAR(255) NOT NULL,
  choice_code VARCHAR(20) NOT NULL,
  course_name VARCHAR(150) NOT NULL,
  merit_exam VARCHAR(30) NOT NULL,
  allotment_type VARCHAR(20) NOT NULL,
  seat_type VARCHAR(20) NOT NULL,
  all_india_merit INT NULL,
  merit_score DECIMAL(12,7) NULL,
  PRIMARY KEY (year, cap_round, sr_no)
) ENGINE=InnoDB;
