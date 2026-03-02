package com.autoflex.backend.dto.production;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ProductionExecutionRequest(
        @NotNull Long productId,
        @NotNull @Min(1) Long quantity) {
}