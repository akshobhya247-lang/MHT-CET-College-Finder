# MHT-CET College Finder 

Java 21, Swing, Maven and MySQL. This is still the desktop application; no web conversion is included.

## Five functional requirements implemented in this build

1. **College search:** search by college name, institute code, city, course, category and CAP round. Results include historical year, CAP round, category, merit rank and percentile where available.
2. **Personalized recommendations:** filter by candidate percentile, category, preferred city, course, admission year and CAP round. Classifications are `Dream`, `Target`, `Safe`, `Unlikely` and are indicative only, not admission guarantees.
3. **Saved preferences:** save, view, reorder and remove college-course preferences scoped to a valid `app_users.user_id`. The current UI uses demo user ID `1`; this is not a login system. Set `-Dcet.user.id=<id>` in VM options to select another existing user.
4. **Admin cutoff management:** add, update and delete cutoff records. `database/triggers.sql` writes INSERT/UPDATE/DELETE audit events into `cutoff_audit`. Set `CET_ADMIN_KEY` in the environment before starting the application to unlock the Admin tab. This key gates the UI; database permissions should also be restricted for production.
5. **CSV import/export:** the CSV Import / Export tab validates cutoff rows before importing and can export filtered search results.

## Database setup

### Fresh database
1. Open MySQL Workbench.
2. Run `database/schema.sql`.
3. Run `database/triggers.sql`.
4. Run `database/procedures.sql`, `database/functions.sql`, and `database/indexes.sql` if needed by your assignment; the Swing features in this build primarily use the DAO queries.
5. Load your existing college/course/cutoff data using the dataset instructions already in `database/README-EXECUTION-ORDER.txt`.

### Existing database
Back up the database first. Run `database/migration_functional_requirements.sql` only if `app_users` and `saved_preferences.user_id` have not already been added. If you already altered these tables, inspect the migration before rerunning it; ALTER statements are not idempotent.

Verify audit triggers with:
```sql
SHOW TRIGGERS FROM college_info_db;
SELECT * FROM cutoff_audit ORDER BY audit_id DESC;
```

## Database connection

The application reads these environment variables (or equivalent Java system properties):
- `CET_DB_URL` (default `jdbc:mysql://localhost:3306/college_info_db`)
- `CET_DB_USER` (default `root`)
- `CET_DB_PASSWORD` (no password is embedded in source)
- `CET_ADMIN_KEY` (required to unlock the admin tools)

Windows PowerShell example for the terminal session:
```powershell
$env:CET_DB_USER = "root"
$env:CET_DB_PASSWORD = "YOUR_MYSQL_PASSWORD"
$env:CET_ADMIN_KEY = "YOUR_ADMIN_KEY"
mvn clean package
mvn exec:java -Dexec.mainClass="com.cet.finder.Main"
```

In IntelliJ, add the same variables under **Run → Edit Configurations → Environment variables** for the `Main` run configuration. Do not commit real passwords or admin keys.

## CSV import format

The first line must match exactly:
```csv
choice_code,year,cap_round,merit_exam,allotment_type,seat_type,category,merit_rank,merit_score,percentile
```

Blank `merit_rank`, `merit_score` and `percentile` are allowed. Percentile must be between 0 and 100. Every `choice_code` must already exist in `college_courses`, otherwise MySQL rejects the row. Imports run in a transaction and roll back on validation/database errors.

## Build check

The uploaded source was compiled successfully with `javac --release 21`. Run `mvn clean package` on your computer to verify Maven dependencies and package the project against your local environment.
