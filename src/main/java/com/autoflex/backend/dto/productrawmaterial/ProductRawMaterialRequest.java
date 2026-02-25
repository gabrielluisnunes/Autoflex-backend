package com.autoflex.backend.dto.productrawmaterial;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ProductRawMaterialRequest(
        @NotNull Long rawMaterialId,
        @NotNull @DecimalMin(value = "0.0001", inclusive = true) BigDecimal requiredQuantity) {
}