USE college_info_db;

DROP TRIGGER IF EXISTS trg_cutoffs_after_insert;
DROP TRIGGER IF EXISTS trg_cutoffs_after_update;
DROP TRIGGER IF EXISTS trg_cutoffs_after_delete;
DELIMITER $$

CREATE TRIGGER trg_cutoffs_after_insert
AFTER INSERT ON cutoffs
FOR EACH ROW
BEGIN
  INSERT INTO cutoff_audit
    (cutoff_id, choice_code, action_type, old_percentile, new_percentile,
     old_merit_score, new_merit_score)
  VALUES
    (NEW.cutoff_id, NEW.choice_code, 'INSERT', NULL, NEW.percentile,
     NULL, NEW.merit_score);
END$$

CREATE TRIGGER trg_cutoffs_after_update
AFTER UPDATE ON cutoffs
FOR EACH ROW
BEGIN
  INSERT INTO cutoff_audit
    (cutoff_id, choice_code, action_type, old_percentile, new_percentile,
     old_merit_score, new_merit_score)
  VALUES
    (NEW.cutoff_id, NEW.choice_code, 'UPDATE', OLD.percentile, NEW.percentile,
     OLD.merit_score, NEW.merit_score);
END$$

CREATE TRIGGER trg_cutoffs_after_delete
AFTER DELETE ON cutoffs
FOR EACH ROW
BEGIN
  INSERT INTO cutoff_audit
    (cutoff_id, choice_code, action_type, old_percentile, new_percentile,
     old_merit_score, new_merit_score)
  VALUES
    (OLD.cutoff_id, OLD.choice_code, 'DELETE', OLD.percentile, NULL,
     OLD.merit_score, NULL);
END$$
DELIMITER ;
