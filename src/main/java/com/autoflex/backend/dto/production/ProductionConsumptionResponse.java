package com.autoflex.backend.dto.production;

import java.math.BigDecimal;

public record ProductionConsumptionResponse(
        Long rawMaterialId,
        String rawMaterialCode,
        String rawMaterialName,
        BigDecimal consumedQuantity,
        BigDecimal remainingStock) {
}