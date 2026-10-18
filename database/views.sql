
USE college_info_db;

CREATE OR REPLACE VIEW v_college_cutoff_details AS
SELECT
    c.institute_code,
    c.institute_name,
    ci.city_name,
    cc.choice_code,
    co.course_name,
    cu.year,
    cu.cap_round,
    cu.category,
    cu.merit_rank,
    cu.percentile
FROM cutoffs cu
JOIN college_courses cc
    ON cu.choice_code = cc.choice_code
JOIN colleges c
    ON cc.institute_code = c.institute_code
JOIN cities ci
    ON c.city_id = ci.city_id
JOIN courses co
    ON cc.course_id = co.course_id;

CREATE OR REPLACE VIEW v_yearly_cutoff_trend AS
SELECT
    year,
    category,
    MIN(percentile) AS lowest_percentile,
    MAX(percentile) AS highest_percentile,
    COUNT(*) AS records_count
FROM cutoffs
GROUP BY year, category;
