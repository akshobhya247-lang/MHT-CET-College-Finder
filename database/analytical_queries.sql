USE college_info_db;

-- 1. Number of colleges, courses and cutoff rows.
SELECT COUNT(*) AS colleges_count FROM colleges;
SELECT COUNT(*) AS courses_count FROM courses;
SELECT COUNT(*) AS cutoff_rows FROM cutoffs;

-- 2. Count records by exam and year.
SELECT merit_exam, year, COUNT(*) AS record_count
FROM cutoffs
GROUP BY merit_exam, year
ORDER BY year DESC, merit_exam;

-- 3. Verified MHT-CET percentile ranges only.
SELECT year, seat_type, COUNT(percentile) AS percentile_records,
       MIN(percentile) AS lowest_percentile,
       MAX(percentile) AS highest_percentile
FROM cutoffs
WHERE merit_exam LIKE '%MHT-CET%' AND percentile IS NOT NULL
GROUP BY year, seat_type
ORDER BY year DESC, seat_type;

-- 4. Most common branches.
SELECT co.course_name, COUNT(*) AS cutoff_record_count
FROM cutoffs cu
JOIN college_courses cc ON cc.choice_code = cu.choice_code
JOIN courses co ON co.course_id = cc.course_id
GROUP BY co.course_name
ORDER BY cutoff_record_count DESC
LIMIT 20;

-- 5. Inspect import rows with missing values.
SELECT *
FROM cutoff_import_raw
WHERE institute_code IS NULL OR choice_code IS NULL
   OR course_name IS NULL OR merit_exam IS NULL;
