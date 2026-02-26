CREATE TABLE IF NOT EXISTS products (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    price NUMERIC(19, 2) NOT NULL
);

CREATE TABLE IF NOT EXISTS raw_materials (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    stock_quantity NUMERIC(19, 4) NOT NULL
);

CREATE TABLE IF NOT EXISTS product_raw_materials (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL,
    raw_material_id BIGINT NOT NULL,
    required_quantity NUMERIC(19, 4) NOT NULL,
    CONSTRAINT uk_product_raw_material UNIQUE (product_id, raw_material_id),
    CONSTRAINT fk_product_raw_material_product FOREIGN KEY (product_id) REFERENCES products (id),
    CONSTRAINT fk_product_raw_material_raw_material FOREIGN KEY (raw_material_id) REFERENCES raw_materials (id)
);

CREATE INDEX IF NOT EXISTS idx_product_raw_materials_product_id ON product_raw_materials (product_id);

CREATE INDEX IF NOT EXISTS idx_product_raw_materials_raw_material_id ON product_raw_materials (raw_material_id);