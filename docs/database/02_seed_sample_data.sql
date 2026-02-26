-- =============================================
-- Autoflex Sample Seed Data
-- =============================================

BEGIN;

-- Products
INSERT INTO
    products (code, name, price)
VALUES (
        'PRD-100',
        'Premium Widget',
        199.90
    ),
    (
        'PRD-200',
        'Standard Widget',
        89.50
    ),
    (
        'PRD-300',
        'Industrial Kit',
        320.00
    ) ON CONFLICT (code) DO NOTHING;

-- Raw materials
INSERT INTO
    raw_materials (code, name, stock_quantity)
VALUES (
        'RM-STEEL',
        'Steel',
        1000.0000
    ),
    (
        'RM-PLASTIC',
        'Plastic',
        800.0000
    ),
    (
        'RM-COPPER',
        'Copper',
        300.0000
    ),
    (
        'RM-ALUM',
        'Aluminum',
        450.0000
    ) ON CONFLICT (code) DO NOTHING;

-- Product/raw material relations
INSERT INTO product_raw_materials (product_id, raw_material_id, required_quantity)
SELECT p.id, r.id, v.required_quantity
FROM (
    VALUES
        ('PRD-100', 'RM-STEEL',   10.0000::NUMERIC),
        ('PRD-100', 'RM-PLASTIC',  6.0000::NUMERIC),
        ('PRD-200', 'RM-STEEL',    4.0000::NUMERIC),
        ('PRD-200', 'RM-ALUM',     2.0000::NUMERIC),
        ('PRD-300', 'RM-STEEL',   15.0000::NUMERIC),
        ('PRD-300', 'RM-COPPER',   3.0000::NUMERIC)
) AS v(product_code, raw_material_code, required_quantity)
JOIN products p ON p.code = v.product_code
JOIN raw_materials r ON r.code = v.raw_material_code
ON CONFLICT (product_id, raw_material_id) DO NOTHING;

COMMIT;