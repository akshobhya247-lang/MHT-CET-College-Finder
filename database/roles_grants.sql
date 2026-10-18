-- OPTIONAL: Run only if you want a separate least-privilege application account.
-- Replace the example password before running; do not commit real credentials.
-- The account is restricted to local connections.
USE college_info_db;

-- CREATE USER IF NOT EXISTS 'cet_app'@'localhost'
--   IDENTIFIED BY 'REPLACE_WITH_A_STRONG_PASSWORD';
-- GRANT SELECT ON college_info_db.* TO 'cet_app'@'localhost';
-- GRANT EXECUTE ON PROCEDURE college_info_db.sp_find_eligible_colleges TO 'cet_app'@'localhost';
-- GRANT EXECUTE ON PROCEDURE college_info_db.sp_add_preference TO 'cet_app'@'localhost';
-- GRANT EXECUTE ON PROCEDURE college_info_db.sp_reorder_preference TO 'cet_app'@'localhost';
-- GRANT EXECUTE ON PROCEDURE college_info_db.sp_remove_preference TO 'cet_app'@'localhost';
-- GRANT EXECUTE ON PROCEDURE college_info_db.sp_get_preferences TO 'cet_app'@'localhost';
-- GRANT EXECUTE ON PROCEDURE college_info_db.sp_update_cutoff TO 'cet_app'@'localhost';
-- GRANT EXECUTE ON FUNCTION college_info_db.fn_admission_chance TO 'cet_app'@'localhost';
-- For admin write access, grant only the required INSERT/UPDATE/DELETE permissions.
-- FLUSH PRIVILEGES;
