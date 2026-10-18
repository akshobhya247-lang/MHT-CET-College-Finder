USE college_info_db;

CREATE INDEX idx_colleges_name ON colleges(institute_name);
CREATE INDEX idx_courses_name ON courses(course_name);
CREATE INDEX idx_cutoffs_search
  ON cutoffs(merit_exam, year, seat_type, percentile);
CREATE INDEX idx_cutoffs_choice_year
  ON cutoffs(choice_code, year, cap_round);
CREATE INDEX idx_preferences_order
  ON saved_preferences(preference_order);
