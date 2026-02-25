package com.autoflex.backend.dto.production;

import java.math.BigDecimal;
import java.util.List;

public record ProductionSuggestionSummaryResponse(
        List<ProductionSuggestionResponse> suggestions,
        BigDecimal totalProductionValue) {
}