package com.autoflex.backend.controller;

import com.autoflex.backend.dto.production.ProductionSuggestionSummaryResponse;
import com.autoflex.backend.service.ProductionSuggestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/production")
@RequiredArgsConstructor
@Tag(name = "Production", description = "Production suggestion calculations")
public class ProductionController {

    private final ProductionSuggestionService productionSuggestionService;

    @GetMapping("/suggestions")
    @Operation(summary = "Get production suggestions ordered by total value")
    public ResponseEntity<ProductionSuggestionSummaryResponse> getSuggestions() {
        return ResponseEntity.ok(productionSuggestionService.getSuggestionsSummary());
    }
}