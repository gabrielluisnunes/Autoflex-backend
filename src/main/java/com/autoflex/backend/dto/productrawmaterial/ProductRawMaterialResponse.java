package com.autoflex.backend.dto.productrawmaterial;

import java.math.BigDecimal;

public record ProductRawMaterialResponse(
        Long id,
        Long rawMaterialId,
        String rawMaterialCode,
        String rawMaterialName,
        BigDecimal requiredQuantity) {
}