Sync Service

Spring Boot microservice to sync data from SQLite (source) to Oracle (target) on a schedule.

Configure

- Edit `src/main/resources/application.properties`:
  - `app.datasource.sqlite.url` — path to your SQLite DB. Example: `jdbc:sqlite:../DB/sqllite/sqlite_setup.db`
  - `app.datasource.oracle.url` — e.g. `jdbc:oracle:thin:@//localhost:1521/FREEPDB1`
  - `app.datasource.oracle.username` and `app.datasource.oracle.password`
  - `app.sync.fixedDelayMs` — sync interval in milliseconds (default 300000)

Run

```bash
mvn spring-boot:run -f sync-service/pom.xml
```

What it does

- Creates `Sync_Metadata(table_name, last_synced_at TIMESTAMP)` in Oracle if missing.
- Incrementally syncs tables:
  - Users — upsert by email
  - Categories — upsert by category_name
  - Transactions, Budgets, SavingsGoals — append-only by created_at, mapping FKs via `email`/`category_name`.

Assumptions

- Oracle schemas exist and match the provided DDL.
- Users uniquely identified by `email`; categories by `category_name`.
- Source `created_at`/`updated_at` are populated.

Notes

- For stricter de-duplication of dependent tables, add a deterministic unique key or hash in Oracle.
- To force a full re-sync, delete rows from `Sync_Metadata`.

