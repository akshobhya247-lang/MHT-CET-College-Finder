# Functional Requirements

**FR-01 — Student Registration:** The system shall allow a new student to create an account.

**FR-02 — Student Login:** The system shall authenticate registered students.

**FR-03 — Student Search:** The system shall allow students to enter their MHT-CET percentile and admission category.

**FR-04 — Search Filters:** The system shall support filtering college results by city, engineering branch and year.

**FR-05 — Eligibility Classification:** The system shall classify matching college-branch options as Safe, Target or Dream using the agreed admission-chance rules.

**FR-06 — College Details:** The system shall display college information and relevant historical cutoff records.

**FR-07 — Preference Management:** The system shall allow students to save selected college-branch options to a preference list.

**FR-08 — Preference Ordering:** The system shall allow students to reorder saved preferences.

**FR-09 — Preference Export:** The system shall allow students to export their preference list to a text file.

**FR-10 — College Management:** The administrator shall be able to add, edit and delete college records.

**FR-11 — Branch and Cutoff Management:** The administrator shall be able to manage branch associations and cutoff records.

**FR-12 — CSV Import:** The system shall allow administrators to import cutoff data from CSV files.

**FR-13 — Import Validation:** The system shall validate imported records and report invalid data.

**FR-14 — Audit Logging:** The database shall record relevant cutoff changes through an audit trigger.

**FR-15 — Duplicate Prevention:** The database shall prevent duplicate preference entries according to the agreed uniqueness rules.

**FR-16 — Input Validation:** The system shall reject invalid percentile values and invalid search inputs.

**FR-17 — Search History:** The system shall store student search history where supported by the finalized schema.

**FR-18 — Data Retrieval:** The system shall retrieve and update data through the agreed Java DAO and JDBC architecture.