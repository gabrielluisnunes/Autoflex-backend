-- Align numeric precision/scale with final technical specification

-- Price: numeric(12,2), non-negative
ALTER TABLE products ALTER COLUMN price TYPE NUMERIC(12, 2);

ALTER TABLE products
DROP CONSTRAINT IF EXISTS ck_products_price_positive;

ALTER TABLE products
DROP CONSTRAINT IF EXISTS ck_products_price_non_negative;

ALTER TABLE products
ADD CONSTRAINT ck_products_price_non_negative CHECK (price >= 0);

-- Quantities: numeric(14,3)
ALTER TABLE raw_materials
ALTER COLUMN stock_quantity TYPE NUMERIC(14, 3);

ALTER TABLE product_raw_materials
ALTER COLUMN required_quantity TYPE NUMERIC(14, 3);

-- Required performance index for sorting by highest product value
CREATE INDEX IF NOT EXISTS idx_products_price ON products (price DESC);