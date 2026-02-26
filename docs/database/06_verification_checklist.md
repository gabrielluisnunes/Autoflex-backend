# Database Verification Checklist

## Schema and Naming

- [x] Table names are in English and use enterprise snake_case plural naming.
- [x] Column names are in English and use snake_case.
- [x] Constraints and indexes use clear professional names.

## Required Tables

- [x] `products`
- [x] `raw_materials`
- [x] `product_raw_materials`

## Required Columns

- [x] `products`: `id`, `code`, `name`, `price`, `created_at`, `updated_at`
- [x] `raw_materials`: `id`, `code`, `name`, `stock_quantity`, `created_at`, `updated_at`
- [x] `product_raw_materials`: `id`, `product_id`, `raw_material_id`, `required_quantity`, `created_at`

## Data Integrity Rules

- [x] `price >= 0` enforced by check constraint.
- [x] `stock_quantity >= 0` enforced by check constraint.
- [x] `required_quantity > 0` enforced by check constraint.
- [x] Duplicate product/raw material relation blocked by unique constraint (`product_id`, `raw_material_id`).
- [x] Foreign keys enforce relational integrity.

## Cascades and Safety

- [x] Deleting a product cascades relation cleanup (`ON DELETE CASCADE`).
- [x] Deleting a raw material is restricted if used (`ON DELETE RESTRICT`) to prevent accidental production-definition loss.

## Performance

- [x] Index on product code.
- [x] Index on raw material code.
- [x] Index on `product_raw_materials.product_id`.
- [x] Index on `product_raw_materials.raw_material_id`.
- [x] Index on product price for sort performance.

## Auditability

- [x] Timestamp defaults defined with `NOW()`.
- [x] `updated_at` auto-update trigger configured.

## Seed and Query Support

- [x] Seed script provides realistic data set (>= 5 products, >= 6 raw materials, realistic relations).
- [x] Query set includes:
  - [x] possible production per product
  - [x] sorted by highest production value
  - [x] total possible production value

## Integration and Migration

- [x] Schema compatible with Spring Boot JPA entities.
- [x] Flyway migration flow defined and applied (`V1`, `V2`, `V3`).
- [x] Supports safe production calculation with deterministic numeric arithmetic.
