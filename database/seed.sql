-- Run after schema.sql. For complete data, import the CSV into cutoff_import_raw
-- first, then run this file. This script does not invent missing city values.
USE college_info_db;

INSERT INTO cities(city_name)
VALUES ('Not provided')
ON DUPLICATE KEY UPDATE city_name = VALUES(city_name);

INSERT INTO cities(city_name)
VALUES ('Pune'), ('Mumbai'), ('Nagpur'), ('Nashik'), ('Aurangabad')
ON DUPLICATE KEY UPDATE city_name = VALUES(city_name);

INSERT INTO courses(course_name)
SELECT DISTINCT TRIM(course_name)
FROM cutoff_import_raw
WHERE course_name IS NOT NULL AND TRIM(course_name) <> ''
ON DUPLICATE KEY UPDATE course_name = VALUES(course_name);

INSERT INTO colleges(institute_code, institute_name, city_id)
SELECT x.institute_code, MAX(x.institute_name),
       (SELECT city_id FROM cities WHERE city_name = 'Not provided' LIMIT 1)
FROM cutoff_import_raw x
WHERE x.institute_code IS NOT NULL AND TRIM(x.institute_code) <> ''
GROUP BY x.institute_code
ON DUPLICATE KEY UPDATE institute_name = VALUES(institute_name);

INSERT INTO college_courses(choice_code, institute_code, course_id)
SELECT x.choice_code, x.institute_code, c.course_id
FROM (
  SELECT choice_code, institute_code, course_name
  FROM cutoff_import_raw
  WHERE choice_code IS NOT NULL AND TRIM(choice_code) <> ''
  GROUP BY choice_code, institute_code, course_name
) x
JOIN courses c ON c.course_name = TRIM(x.course_name)
JOIN colleges cl ON cl.institute_code = x.institute_code
ON DUPLICATE KEY UPDATE institute_code = VALUES(institute_code),
                        course_id = VALUES(course_id);

-- IMPORTANT: merit_score is preserved as raw merit score. It is not assumed to
-- be an MHT-CET percentile for every exam. Percentile stays NULL until verified.
INSERT INTO cutoffs
(choice_code, year, cap_round, merit_exam, allotment_type, seat_type,
 category, merit_rank, merit_score, percentile)
SELECT r.choice_code, r.year, r.cap_round, r.merit_exam, r.allotment_type,
       r.seat_type, r.seat_type, NULLIF(r.all_india_merit, 0), r.merit_score,
       NULL
FROM cutoff_import_raw r
JOIN college_courses cc ON cc.choice_code = r.choice_code
ON DUPLICATE KEY UPDATE
  merit_rank = VALUES(merit_rank),
  merit_score = VALUES(merit_score),
  category = VALUES(category);

-- After importing, verify:
SELECT COUNT(*) AS imported_raw_rows FROM cutoff_import_raw;
SELECT COUNT(*) AS normalized_cutoff_rows FROM cutoffs;
SELECT COUNT(*) AS college_count FROM colleges;
SELECT COUNT(*) AS course_count FROM courses;
