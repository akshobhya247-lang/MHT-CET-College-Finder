USE college_info_db;

DROP FUNCTION IF EXISTS fn_admission_chance;
DELIMITER $$
CREATE FUNCTION fn_admission_chance(
  p_student_pct DECIMAL(10,7),
  p_cutoff_pct DECIMAL(10,7)
)
RETURNS VARCHAR(10)
DETERMINISTIC
NO SQL
BEGIN
  IF p_student_pct IS NULL OR p_cutoff_pct IS NULL
     OR p_student_pct < 0 OR p_student_pct > 100
     OR p_cutoff_pct < 0 OR p_cutoff_pct > 100 THEN
    RETURN 'Unknown';
  ELSEIF p_student_pct >= p_cutoff_pct + 5 THEN
    RETURN 'Safe';
  ELSEIF p_student_pct >= p_cutoff_pct THEN
    RETURN 'Target';
  ELSE
    RETURN 'Dream';
  END IF;
END$$
DELIMITER ;

-- Example:
-- SELECT fn_admission_chance(98.0, 95.0);
