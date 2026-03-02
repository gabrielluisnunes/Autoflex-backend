package com.autoflex.backend.dto.production;

import java.util.List;

public record ProductionExecutionResponse(
        Long productId,
        String productCode,
        String productName,
        Long producedQuantity,
        List<ProductionConsumptionResponse> consumedRawMaterials) {
}