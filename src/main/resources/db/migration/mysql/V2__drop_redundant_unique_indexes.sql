-- Remove redundant unique indexes duplicated by UNIQUE constraints in V1
-- Keeps schema aligned for future MySQL versions where duplicate indexes may be disallowed.

SET
    @products_idx_exists := (
        SELECT COUNT(*)
        FROM information_schema.statistics
        WHERE
            table_schema = DATABASE()
            AND table_name = 'products'
            AND index_name = 'idx_products_code_unique'
    );

SET
    @products_drop_sql := IF(
        @products_idx_exists > 0,
        'ALTER TABLE products DROP INDEX idx_products_code_unique',
        'SELECT 1'
    );

PREPARE products_stmt FROM @products_drop_sql;

EXECUTE products_stmt;

DEALLOCATE PREPARE products_stmt;

SET
    @raw_materials_idx_exists := (
        SELECT COUNT(*)
        FROM information_schema.statistics
        WHERE
            table_schema = DATABASE()
            AND table_name = 'raw_materials'
            AND index_name = 'idx_raw_materials_code_unique'
    );

SET
    @raw_materials_drop_sql := IF(
        @raw_materials_idx_exists > 0,
        'ALTER TABLE raw_materials DROP INDEX idx_raw_materials_code_unique',
        'SELECT 1'
    );

PREPARE raw_materials_stmt FROM @raw_materials_drop_sql;

EXECUTE raw_materials_stmt;

DEALLOCATE PREPARE raw_materials_stmt;