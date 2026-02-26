-- MySQL 8.0+ production-ready schema for Autoflex

CREATE TABLE IF NOT EXISTS products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) NOT NULL,
    name VARCHAR(150) NOT NULL,
    price DECIMAL(12, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_products_code UNIQUE (code),
    CONSTRAINT ck_products_price_non_negative CHECK (price >= 0)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS raw_materials (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) NOT NULL,
    name VARCHAR(150) NOT NULL,
    stock_quantity DECIMAL(14, 3) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_raw_materials_code UNIQUE (code),
    CONSTRAINT ck_raw_materials_stock_non_negative CHECK (stock_quantity >= 0)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS product_raw_materials (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    raw_material_id BIGINT NOT NULL,
    required_quantity DECIMAL(14, 3) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_product_raw_material UNIQUE (product_id, raw_material_id),
    CONSTRAINT ck_product_raw_material_required_positive CHECK (required_quantity > 0),
    CONSTRAINT fk_product_raw_material_product FOREIGN KEY (product_id) REFERENCES products (id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_product_raw_material_raw_material FOREIGN KEY (raw_material_id) REFERENCES raw_materials (id) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE = InnoDB;

CREATE UNIQUE INDEX idx_products_code_unique ON products (code);

CREATE UNIQUE INDEX idx_raw_materials_code_unique ON raw_materials (code);

CREATE INDEX idx_products_price ON products (price DESC);

CREATE INDEX idx_prm_product_id ON product_raw_materials (product_id);

CREATE INDEX idx_prm_raw_material_id ON product_raw_materials (raw_material_id);

CREATE INDEX idx_prm_product_raw_material ON product_raw_materials (product_id, raw_material_id);

CREATE INDEX idx_products_created_at ON products (created_at);

CREATE INDEX idx_raw_materials_created_at ON raw_materials (created_at);