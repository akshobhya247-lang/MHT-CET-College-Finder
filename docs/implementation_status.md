# Functional requirement implementation notes

- Search: implemented in `CollegeDAO` and `SearchPanel`; query supports college name, institute code, city, course, category, CAP round and historical cutoff columns.
- Recommendations: implemented in `CutoffPredictorDAO` and `CutoffPredictorPanel`; categories are Dream/Target/Safe/Unlikely, with city and admission year filters. This is a historical heuristic, not a guaranteed prediction.
- Preferences: implemented in `PreferenceDAO` and `ShortlistPanel`; preferences are scoped to `cet.user.id` (default 1), which must correspond to `app_users.user_id`.
- Admin cutoff CRUD: implemented in `CutoffDAO` and `AdminPanel`; `CET_ADMIN_KEY` unlocks the panel and database triggers must be installed from `database/triggers.sql` for auditing.
- CSV: implemented in `CsvImportTask` and `CsvToolsPanel`; import validates fields and rolls back on failure; export uses the active search filters.

Important setup: run `database/schema.sql` on a fresh database, or review/run `database/migration_functional_requirements.sql` on an existing database. Install `database/triggers.sql`. The app uses environment variables for credentials instead of a password hard-coded in Java.
