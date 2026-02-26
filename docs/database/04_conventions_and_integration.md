# Database Conventions and Integration Notes

## Naming Conventions

- Tables: snake_case plural (`products`, `raw_materials`, `product_raw_materials`)
- Columns: snake_case (`created_at`, `required_quantity`)
- Primary keys: `id`
- Foreign keys: `<table>_id` (`product_id`, `raw_material_id`)
- Constraints:
  - `pk_<table>` for primary keys (implicit in PostgreSQL when using `PRIMARY KEY`)
  - `uk_<table>_<column>` for unique constraints
  - `fk_<from>_<to>` for foreign keys
  - `ck_<table>_<rule>` for check constraints
- Indexes: `idx_<table>_<column_or_purpose>`

## Spring Boot JPA Integration Tips

- Keep entity names aligned with table names via `@Table(name = "...")`.
- Keep decimal precision/scale aligned between entity and SQL (`NUMERIC(19,2)` and `NUMERIC(19,4)`).
- Use `ddl-auto: validate` in production-like environments.
- Handle `updated_at` in DB triggers or in JPA callbacks (`@PreUpdate`), but avoid duplicating both if not needed.

## Flyway Best Practices

- Use versioned migrations (`V1__...sql`, `V2__...sql`) for deterministic evolution.
- Never edit already-applied migrations in shared environments.
- Add new migrations for changes.
- Keep migration files idempotent when possible for safer local/dev runs.
- Seed data should be environment-specific:
  - production: usually no sample seeds
  - local/dev: dedicated seed script or repeatable migration with clear guards
