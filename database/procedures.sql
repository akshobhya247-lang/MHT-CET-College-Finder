
USE college_info_db;

DROP PROCEDURE IF EXISTS sp_find_eligible_colleges;
DROP PROCEDURE IF EXISTS sp_add_preference;
DROP PROCEDURE IF EXISTS sp_reorder_preference;
DROP PROCEDURE IF EXISTS sp_remove_preference;
DROP PROCEDURE IF EXISTS sp_get_preferences;
DROP PROCEDURE IF EXISTS sp_update_cutoff;

DELIMITER $$

CREATE PROCEDURE sp_find_eligible_colleges(
    IN p_percentile DECIMAL(10,7),
    IN p_seat_type VARCHAR(10),
    IN p_branch VARCHAR(100),
    IN p_year SMALLINT,
    IN p_keyword VARCHAR(100)
)
BEGIN
    SELECT
        cl.institute_code,
        cl.institute_name,
        ci.city_name,
        co.course_name,
        cc.choice_code,
        cu.year,
        cu.cap_round,
        cu.category,
        cu.merit_rank,
        cu.percentile,
        fn_admission_chance(
            p_percentile,
            cu.percentile
        ) AS admission_chance
    FROM cutoffs cu
    JOIN college_courses cc
        ON cc.choice_code = cu.choice_code
    JOIN colleges cl
        ON cl.institute_code = cc.institute_code
    JOIN cities ci
        ON ci.city_id = cl.city_id
    JOIN courses co
        ON co.course_id = cc.course_id
    WHERE cu.percentile IS NOT NULL
      AND p_percentile IS NOT NULL
      AND (
          p_seat_type IS NULL
          OR p_seat_type = ''
          OR cu.category = p_seat_type
      )
      AND (
          p_branch IS NULL
          OR p_branch = ''
          OR co.course_name LIKE CONCAT('%', p_branch, '%')
      )
      AND (
          p_year IS NULL
          OR cu.year = p_year
      )
      AND (
          p_keyword IS NULL
          OR p_keyword = ''
          OR cl.institute_name LIKE CONCAT('%', p_keyword, '%')
          OR CAST(cl.institute_code AS CHAR)
             LIKE CONCAT('%', p_keyword, '%')
          OR co.course_name LIKE CONCAT('%', p_keyword, '%')
      )
    ORDER BY
        cu.percentile DESC,
        cl.institute_name,
        co.course_name;
END$$

CREATE PROCEDURE sp_add_preference(
    IN p_choice_code VARCHAR(15)
)
BEGIN
    DECLARE v_next INT DEFAULT 1;

    IF NOT EXISTS (
        SELECT 1
        FROM college_courses
        WHERE choice_code = p_choice_code
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Unknown choice code';
    END IF;

    IF EXISTS (
        SELECT 1
        FROM saved_preferences
        WHERE choice_code = p_choice_code
    ) THEN
        SELECT preference_order AS current_position
        FROM saved_preferences
        WHERE choice_code = p_choice_code;
    ELSE
        SELECT COALESCE(MAX(preference_order), 0) + 1
        INTO v_next
        FROM saved_preferences;

        INSERT INTO saved_preferences(
            choice_code,
            preference_order
        )
        VALUES(
            p_choice_code,
            v_next
        );
    END IF;
END$$

CREATE PROCEDURE sp_reorder_preference(
    IN p_choice_code VARCHAR(15),
    IN p_new_position INT
)
BEGIN
    DECLARE v_old_position INT;
    DECLARE v_count INT;

    IF p_new_position IS NULL OR p_new_position < 1 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Position must be 1 or greater';
    END IF;

    SELECT preference_order
    INTO v_old_position
    FROM saved_preferences
    WHERE choice_code = p_choice_code
    LIMIT 1;

    IF v_old_position IS NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Preference not found';
    END IF;

    SELECT COUNT(*)
    INTO v_count
    FROM saved_preferences;

    IF p_new_position > v_count THEN
        SET p_new_position = v_count;
    END IF;

    IF p_new_position < v_old_position THEN
        UPDATE saved_preferences
        SET preference_order = preference_order + 1
        WHERE preference_order >= p_new_position
          AND preference_order < v_old_position;

    ELSEIF p_new_position > v_old_position THEN
        UPDATE saved_preferences
        SET preference_order = preference_order - 1
        WHERE preference_order > v_old_position
          AND preference_order <= p_new_position;
    END IF;

    UPDATE saved_preferences
    SET preference_order = -1
    WHERE choice_code = p_choice_code;

    UPDATE saved_preferences
    SET preference_order = p_new_position
    WHERE choice_code = p_choice_code;
END$$

CREATE PROCEDURE sp_remove_preference(
    IN p_choice_code VARCHAR(15)
)
BEGIN
    DECLARE v_position INT;

    SELECT preference_order
    INTO v_position
    FROM saved_preferences
    WHERE choice_code = p_choice_code
    LIMIT 1;

    DELETE FROM saved_preferences
    WHERE choice_code = p_choice_code;

    IF v_position IS NOT NULL THEN
        UPDATE saved_preferences
        SET preference_order = preference_order - 1
        WHERE preference_order > v_position;
    END IF;
END$$

CREATE PROCEDURE sp_get_preferences()
BEGIN
    SELECT
        sp.preference_order,
        cl.institute_code,
        cl.institute_name,
        ci.city_name,
        co.course_name,
        cc.choice_code,
        sp.saved_at
    FROM saved_preferences sp
    JOIN college_courses cc
        ON cc.choice_code = sp.choice_code
    JOIN colleges cl
        ON cl.institute_code = cc.institute_code
    JOIN cities ci
        ON ci.city_id = cl.city_id
    JOIN courses co
        ON co.course_id = cc.course_id
    ORDER BY sp.preference_order;
END$$

CREATE PROCEDURE sp_update_cutoff(
    IN p_cutoff_id BIGINT UNSIGNED,
    IN p_merit_rank INT UNSIGNED,
    IN p_percentile DECIMAL(9,6)
)
BEGIN
    IF p_percentile IS NOT NULL
       AND (p_percentile < 0 OR p_percentile > 100) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Percentile must be between 0 and 100';
    END IF;

    UPDATE cutoffs
    SET merit_rank = p_merit_rank,
        percentile = p_percentile
    WHERE cutoff_id = p_cutoff_id;

    IF ROW_COUNT() = 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Cutoff not found or values unchanged';
    END IF;
END$$

DELIMITER ;
