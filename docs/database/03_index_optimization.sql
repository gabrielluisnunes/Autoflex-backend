-- =============================================
-- Autoflex Index Optimization
-- =============================================

-- Unique business identifiers
CREATE UNIQUE INDEX IF NOT EXISTS idx_products_code_unique ON products (code);

CREATE UNIQUE INDEX IF NOT EXISTS idx_raw_materials_code_unique ON raw_materials (code);

-- Join-heavy relation table indexes
CREATE INDEX IF NOT EXISTS idx_prm_product_id ON product_raw_materials (product_id);

CREATE INDEX IF NOT EXISTS idx_prm_raw_material_id ON product_raw_materials (raw_material_id);

-- Optional composite index for frequent pair lookups
CREATE INDEX IF NOT EXISTS idx_prm_product_raw_material ON product_raw_materials (product_id, raw_material_id);

-- Sorting and temporal support indexes
CREATE INDEX IF NOT EXISTS idx_products_price ON products (price DESC);

CREATE INDEX IF NOT EXISTS idx_products_created_at ON products (created_at);

CREATE INDEX IF NOT EXISTS idx_raw_materials_created_at ON raw_materials (created_at);