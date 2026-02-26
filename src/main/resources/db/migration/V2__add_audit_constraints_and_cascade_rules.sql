-- Add audit columns and stronger constraints/indexing for production readiness

-- =============================================
-- 1) Audit columns
-- =============================================
ALTER TABLE products
ADD COLUMN IF NOT EXISTS created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
ADD COLUMN IF NOT EXISTS updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW();

ALTER TABLE raw_materials
ADD COLUMN IF NOT EXISTS created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
ADD COLUMN IF NOT EXISTS updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW();

ALTER TABLE product_raw_materials
ADD COLUMN IF NOT EXISTS created_at TIMESTAMPTZ NOT NULL DEFAULT NOW();

-- =============================================
-- 2) Check constraints
-- =============================================
ALTER TABLE products
DROP CONSTRAINT IF EXISTS ck_products_price_positive,
ADD CONSTRAINT ck_products_price_positive CHECK (price > 0);

ALTER TABLE raw_materials
DROP CONSTRAINT IF EXISTS ck_raw_materials_stock_non_negative,
ADD CONSTRAINT ck_raw_materials_stock_non_negative CHECK (stock_quantity >= 0);

ALTER TABLE product_raw_materials
DROP CONSTRAINT IF EXISTS ck_product_raw_material_required_positive,
ADD CONSTRAINT ck_product_raw_material_required_positive CHECK (required_quantity > 0);

-- =============================================
-- 3) Cascade rules on relation FKs
-- =============================================
ALTER TABLE product_raw_materials
DROP CONSTRAINT IF EXISTS fk_product_raw_material_product,
ADD CONSTRAINT fk_product_raw_material_product FOREIGN KEY (product_id) REFERENCES products (id) ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE product_raw_materials
DROP CONSTRAINT IF EXISTS fk_product_raw_material_raw_material,
ADD CONSTRAINT fk_product_raw_material_raw_material FOREIGN KEY (raw_material_id) REFERENCES raw_materials (id) ON UPDATE CASCADE ON DELETE RESTRICT;

-- =============================================
-- 4) Index optimization
-- =============================================
CREATE INDEX IF NOT EXISTS idx_products_created_at ON products (created_at);

CREATE INDEX IF NOT EXISTS idx_raw_materials_created_at ON raw_materials (created_at);

CREATE INDEX IF NOT EXISTS idx_prm_product_id ON product_raw_materials (product_id);

CREATE INDEX IF NOT EXISTS idx_prm_raw_material_id ON product_raw_materials (raw_material_id);

CREATE INDEX IF NOT EXISTS idx_prm_product_raw_material ON product_raw_materials (product_id, raw_material_id);

-- =============================================
-- 5) updated_at trigger support
-- =============================================
CREATE OR REPLACE FUNCTION fn_set_updated_at()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$;

DROP TRIGGER IF EXISTS trg_products_set_updated_at ON products;

CREATE TRIGGER trg_products_set_updated_at
BEFORE UPDATE ON products
FOR EACH ROW
EXECUTE FUNCTION fn_set_updated_at();

DROP TRIGGER IF EXISTS trg_raw_materials_set_updated_at ON raw_materials;

CREATE TRIGGER trg_raw_materials_set_updated_at
BEFORE UPDATE ON raw_materials
FOR EACH ROW
EXECUTE FUNCTION fn_set_updated_at();