-- =============================================
-- Production Calculation Queries
-- =============================================

-- 1) Possible production per product
-- possible_production = min(stock_quantity / required_quantity)
SELECT
    p.id,
    p.code,
    p.name,
    p.price,
    MIN(FLOOR(rm.stock_quantity / prm.required_quantity))::BIGINT AS possible_quantity
FROM products p
JOIN product_raw_materials prm ON prm.product_id = p.id
JOIN raw_materials rm ON rm.id = prm.raw_material_id
GROUP BY p.id, p.code, p.name, p.price
ORDER BY p.code;

-- 2) Production list sorted by highest production value
SELECT
    p.id,
    p.code,
    p.name,
    MIN(FLOOR(rm.stock_quantity / prm.required_quantity))::BIGINT AS possible_quantity,
    p.price AS unit_price,
    (MIN(FLOOR(rm.stock_quantity / prm.required_quantity)) * p.price)::NUMERIC(14,2) AS total_value
FROM products p
JOIN product_raw_materials prm ON prm.product_id = p.id
JOIN raw_materials rm ON rm.id = prm.raw_material_id
GROUP BY p.id, p.code, p.name, p.price
ORDER BY total_value DESC;

-- 3) Total possible production value (global)
WITH production_suggestions AS (
    SELECT
        p.id,
        MIN(FLOOR(rm.stock_quantity / prm.required_quantity))::BIGINT AS possible_quantity,
        p.price
    FROM products p
    JOIN product_raw_materials prm ON prm.product_id = p.id
    JOIN raw_materials rm ON rm.id = prm.raw_material_id
    GROUP BY p.id, p.price
)
SELECT
    COALESCE(SUM(possible_quantity * price), 0)::NUMERIC(16,2) AS total_possible_production_value
FROM production_suggestions;