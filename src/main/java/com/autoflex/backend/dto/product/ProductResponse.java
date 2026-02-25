package com.autoflex.backend.dto.product;

import com.autoflex.backend.dto.productrawmaterial.ProductRawMaterialResponse;

import java.math.BigDecimal;
import java.util.List;

public record ProductResponse(
        Long id,
        String code,
        String name,
        BigDecimal price,
        List<ProductRawMaterialResponse> rawMaterials) {
}