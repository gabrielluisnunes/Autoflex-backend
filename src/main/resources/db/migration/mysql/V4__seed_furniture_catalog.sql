-- Seed de catálogo correlacionado para produção de móveis (foco em mesa)
-- Idempotente: não duplica registros quando já existirem.

INSERT INTO
    raw_materials (code, name, stock_quantity)
VALUES (
        'RM-TABUA-PINUS',
        'Tábua de Pinus (m²)',
        250.000
    ),
    (
        'RM-PERNA-MADEIRA',
        'Perna de Madeira (un)',
        400.000
    ),
    (
        'RM-PARAFUSO-6MM',
        'Parafuso 6mm (un)',
        8000.000
    ),
    (
        'RM-COLA-MADEIRA',
        'Cola para Madeira (L)',
        180.000
    ),
    (
        'RM-VERNIZ-FOSCO',
        'Verniz Fosco (L)',
        160.000
    ),
    (
        'RM-LIXA-120',
        'Lixa Grão 120 (un)',
        1200.000
    ),
    (
        'RM-CANTONEIRA-METAL',
        'Cantoneira Metálica (un)',
        900.000
    )
ON DUPLICATE KEY UPDATE
    id = id;

INSERT INTO
    products (code, name, price)
VALUES (
        'PRD-MESA-4L',
        'Mesa de Madeira 4 Lugares',
        899.90
    ),
    (
        'PRD-MESA-6L',
        'Mesa de Madeira 6 Lugares',
        1299.90
    ),
    (
        'PRD-MESA-ESCR',
        'Mesa de Escritório',
        749.90
    ),
    (
        'PRD-BANCO-MAD',
        'Banco de Madeira',
        299.90
    )
ON DUPLICATE KEY UPDATE
    id = id;

-- Associações para PRD-MESA-4L
INSERT INTO
    product_raw_materials (
        product_id,
        raw_material_id,
        required_quantity
    )
SELECT p.id, rm.id, x.required_quantity
FROM (
        SELECT
            'PRD-MESA-4L' AS product_code,
            'RM-TABUA-PINUS' AS raw_code,
            3.200 AS required_quantity
        UNION ALL
        SELECT 'PRD-MESA-4L', 'RM-PERNA-MADEIRA', 4.000
        UNION ALL
        SELECT 'PRD-MESA-4L', 'RM-PARAFUSO-6MM', 32.000
        UNION ALL
        SELECT 'PRD-MESA-4L', 'RM-COLA-MADEIRA', 0.350
        UNION ALL
        SELECT 'PRD-MESA-4L', 'RM-VERNIZ-FOSCO', 0.500
        UNION ALL
        SELECT 'PRD-MESA-4L', 'RM-LIXA-120', 3.000
        UNION ALL
        SELECT 'PRD-MESA-4L', 'RM-CANTONEIRA-METAL', 4.000
    ) x
    JOIN products p ON p.code = x.product_code
    JOIN raw_materials rm ON rm.code = x.raw_code
    LEFT JOIN product_raw_materials prm ON prm.product_id = p.id
    AND prm.raw_material_id = rm.id
WHERE
    prm.id IS NULL;

-- Associações para PRD-MESA-6L
INSERT INTO
    product_raw_materials (
        product_id,
        raw_material_id,
        required_quantity
    )
SELECT p.id, rm.id, x.required_quantity
FROM (
        SELECT
            'PRD-MESA-6L' AS product_code,
            'RM-TABUA-PINUS' AS raw_code,
            4.500 AS required_quantity
        UNION ALL
        SELECT 'PRD-MESA-6L', 'RM-PERNA-MADEIRA', 4.000
        UNION ALL
        SELECT 'PRD-MESA-6L', 'RM-PARAFUSO-6MM', 48.000
        UNION ALL
        SELECT 'PRD-MESA-6L', 'RM-COLA-MADEIRA', 0.450
        UNION ALL
        SELECT 'PRD-MESA-6L', 'RM-VERNIZ-FOSCO', 0.650
        UNION ALL
        SELECT 'PRD-MESA-6L', 'RM-LIXA-120', 4.000
        UNION ALL
        SELECT 'PRD-MESA-6L', 'RM-CANTONEIRA-METAL', 6.000
    ) x
    JOIN products p ON p.code = x.product_code
    JOIN raw_materials rm ON rm.code = x.raw_code
    LEFT JOIN product_raw_materials prm ON prm.product_id = p.id
    AND prm.raw_material_id = rm.id
WHERE
    prm.id IS NULL;

-- Associações para PRD-MESA-ESCR
INSERT INTO
    product_raw_materials (
        product_id,
        raw_material_id,
        required_quantity
    )
SELECT p.id, rm.id, x.required_quantity
FROM (
        SELECT
            'PRD-MESA-ESCR' AS product_code,
            'RM-TABUA-PINUS' AS raw_code,
            2.700 AS required_quantity
        UNION ALL
        SELECT 'PRD-MESA-ESCR', 'RM-PERNA-MADEIRA', 4.000
        UNION ALL
        SELECT 'PRD-MESA-ESCR', 'RM-PARAFUSO-6MM', 28.000
        UNION ALL
        SELECT 'PRD-MESA-ESCR', 'RM-COLA-MADEIRA', 0.250
        UNION ALL
        SELECT 'PRD-MESA-ESCR', 'RM-VERNIZ-FOSCO', 0.400
        UNION ALL
        SELECT 'PRD-MESA-ESCR', 'RM-LIXA-120', 2.000
        UNION ALL
        SELECT 'PRD-MESA-ESCR', 'RM-CANTONEIRA-METAL', 2.000
    ) x
    JOIN products p ON p.code = x.product_code
    JOIN raw_materials rm ON rm.code = x.raw_code
    LEFT JOIN product_raw_materials prm ON prm.product_id = p.id
    AND prm.raw_material_id = rm.id
WHERE
    prm.id IS NULL;

-- Associações para PRD-BANCO-MAD
INSERT INTO
    product_raw_materials (
        product_id,
        raw_material_id,
        required_quantity
    )
SELECT p.id, rm.id, x.required_quantity
FROM (
        SELECT
            'PRD-BANCO-MAD' AS product_code,
            'RM-TABUA-PINUS' AS raw_code,
            1.200 AS required_quantity
        UNION ALL
        SELECT 'PRD-BANCO-MAD', 'RM-PERNA-MADEIRA', 4.000
        UNION ALL
        SELECT 'PRD-BANCO-MAD', 'RM-PARAFUSO-6MM', 16.000
        UNION ALL
        SELECT 'PRD-BANCO-MAD', 'RM-COLA-MADEIRA', 0.120
        UNION ALL
        SELECT 'PRD-BANCO-MAD', 'RM-VERNIZ-FOSCO', 0.200
        UNION ALL
        SELECT 'PRD-BANCO-MAD', 'RM-LIXA-120', 1.000
    ) x
    JOIN products p ON p.code = x.product_code
    JOIN raw_materials rm ON rm.code = x.raw_code
    LEFT JOIN product_raw_materials prm ON prm.product_id = p.id
    AND prm.raw_material_id = rm.id
WHERE
    prm.id IS NULL;