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
- Keep decimal precision/scale aligned between entity and SQL:
  - `products.price` -> `NUMERIC(12,2)`
  - `raw_materials.stock_quantity` -> `NUMERIC(14,3)`
  - `product_raw_materials.required_quantity` -> `NUMERIC(14,3)`
- Use `ddl-auto: validate` in production-like environments.
- Handle `updated_at` in DB triggers or in JPA callbacks (`@PreUpdate`), but avoid duplicating both if not needed.
- In JPA entities, map audit columns explicitly when needed:
  - `created_at` as insertable timestamp
  - `updated_at` updated by DB trigger

## Flyway Best Practices

- Use versioned migrations (`V1__...sql`, `V2__...sql`) for deterministic evolution.
- Never edit already-applied migrations in shared environments.
- Add new migrations for changes.
- Keep migration files idempotent when possible for safer local/dev runs.
- Seed data should be environment-specific:
  - production: usually no sample seeds
  - local/dev: dedicated seed script or repeatable migration with clear guards

## Suggested Flyway Structure

- `V1__init_schema.sql`
  - Base tables, PKs, unique constraints, base FKs.
- `V2__add_audit_constraints_and_cascade_rules.sql`
  - Audit timestamps, triggers, check constraints, FK delete rules, indexes.
- `V3__align_numeric_precision_and_price_index.sql`
  - Precision alignment and additional performance index (`price DESC`).
- `R__seed_dev_data.sql` (optional repeatable migration for local/dev only)
  - Re-loadable test data for non-production environments.
