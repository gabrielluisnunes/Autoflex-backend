package com.autoflex.backend.dto.production;

import java.math.BigDecimal;

public record ProductionSuggestionResponse(
        Long productId,
        String productCode,
        String productName,
        Long possibleQuantity,
        BigDecimal unitPrice,
        BigDecimal totalValue) {
}