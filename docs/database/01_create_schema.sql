-- =============================================
-- Autoflex Production Inventory Schema
-- PostgreSQL 16+ (stable features)
-- =============================================

BEGIN;

-- =============================================
-- 1) Utility function for updated_at handling
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

-- =============================================
-- 2) Core tables
-- =============================================
CREATE TABLE IF NOT EXISTS products (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL,
    name VARCHAR(150) NOT NULL,
    price NUMERIC(19, 2) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_products_code UNIQUE (code),
    CONSTRAINT ck_products_price_positive CHECK (price > 0)
);

CREATE TABLE IF NOT EXISTS raw_materials (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL,
    name VARCHAR(150) NOT NULL,
    stock_quantity NUMERIC(19, 4) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_raw_materials_code UNIQUE (code),
    CONSTRAINT ck_raw_materials_stock_non_negative CHECK (stock_quantity >= 0)
);

CREATE TABLE IF NOT EXISTS product_raw_materials (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL,
    raw_material_id BIGINT NOT NULL,
    required_quantity NUMERIC(19, 4) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_product_raw_material UNIQUE (product_id, raw_material_id),
    CONSTRAINT ck_product_raw_material_required_positive CHECK (required_quantity > 0),
    CONSTRAINT fk_product_raw_material_product FOREIGN KEY (product_id) REFERENCES products (id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_product_raw_material_raw_material FOREIGN KEY (raw_material_id) REFERENCES raw_materials (id) ON UPDATE CASCADE ON DELETE RESTRICT
);

-- =============================================
-- 3) Triggers for audit columns
-- =============================================
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

COMMIT;