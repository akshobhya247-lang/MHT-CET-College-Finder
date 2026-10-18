USE college_info_db;

-- 1. Verify database objects and loaded rows.
SHOW TABLES;
SELECT COUNT(*) AS raw_import_rows FROM cutoff_import_raw;
SELECT COUNT(*) AS normalized_cutoff_rows FROM cutoffs;
SELECT * FROM v_college_cutoff_details LIMIT 20;

-- 2. Test admission chance function.
SELECT fn_admission_chance(98.0, 95.0) AS expected_safe;
SELECT fn_admission_chance(95.5, 95.0) AS expected_target;
SELECT fn_admission_chance(80.0, 95.0) AS expected_dream;

-- 3. Test search (results require verified percentile data in cutoffs).
CALL sp_find_eligible_colleges(95.5, NULL, NULL, NULL, NULL);

-- 4. Test preferences with an actual choice_code from college_courses.
-- Replace the sample code with a real code returned by:
-- SELECT choice_code FROM college_courses LIMIT 10;
-- CALL sp_add_preference('REPLACE_CODE');
-- CALL sp_get_preferences();
-- CALL sp_reorder_preference('REPLACE_CODE', 1);
-- CALL sp_remove_preference('REPLACE_CODE');

-- 5. Test audit after changing a known cutoff record (run only in test database).
-- UPDATE cutoffs SET percentile = 90.123456 WHERE cutoff_id = 1;
-- SELECT * FROM cutoff_audit ORDER BY audit_id DESC LIMIT 10;

-- 6. Check views.
SELECT * FROM v_student_preference_summary;
SELECT * FROM v_yearly_cutoff_trend;
