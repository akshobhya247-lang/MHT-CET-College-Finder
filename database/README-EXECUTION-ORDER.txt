MHT-CET COLLEGE FINDER - SQL FILES
=================================

These scripts use one schema: college_info_db, based on the uploaded DDL PDF,
adapted to preserve leading-zero institute codes and the uploaded flat CSV fields.

IMPORTANT DATA LIMITATION
-------------------------
The CSV includes merit_exam, merit_score and all_india_merit. Do not assume
merit_score is an MHT-CET percentile for every exam. The import keeps percentile
NULL until it is verified/mapped. Therefore the admission search procedure returns
only records whose percentile has been populated and whose exam is MHT-CET.
This is intentional to avoid false recommendations.

EXECUTION ORDER
---------------
1. schema.sql
2. Import the CSV file into college_info_db.cutoff_import_raw using MySQL Workbench
   Table Data Import Wizard. Match the 12 CSV columns to the same-named columns.
3. seed.sql
4. functions.sql
5. procedures.sql
6. triggers.sql
7. views.sql
8. indexes.sql
9. test_queries.sql

roles_grants.sql is optional and contains commented example commands only.

DO NOT
------
- Do not run setup_cutoff_flat.sql and MINI-PROJ-SQL-SCRIPT.sql against this schema.
  They create different database designs.
- Do not execute schema.sql on a production database without a backup.
- Do not assume city names from institute names. The import maps colleges to
  "Not provided" because the flat CSV has no city column. Fill city mappings from
  a reliable source later if city filtering is required.
- Do not import the same data repeatedly without checking for duplicates.

PREFERENCES
-----------
The agreed procedure signatures accept choice_code and position but no user_id,
and the current GUI plan removes login/registration. Therefore these procedures
use a shared single-user demo preference list. For multi-user preferences, change
the agreed signatures and use app_users/user_id as specified in the Functional
Requirements PDF.

JAVA INTEGRATION
----------------
Java calls should match the procedures exactly:
CALL sp_find_eligible_colleges(?, ?, ?, ?, ?)
CALL sp_add_preference(?)
CALL sp_reorder_preference(?, ?)
CALL sp_get_preferences()
CALL sp_remove_preference(?)
SELECT fn_admission_chance(?, ?)

Use PreparedStatement/CallableStatement and handle SQL exceptions in Java.
