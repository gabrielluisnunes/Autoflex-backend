package com.autoflex.backend.controller;

import com.autoflex.backend.dto.production.ProductionSuggestionResponse;
import com.autoflex.backend.dto.production.ProductionSuggestionSummaryResponse;
import com.autoflex.backend.service.ProductionSuggestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/production")
@RequiredArgsConstructor
public class ProductionController {

    private final ProductionSuggestionService productionSuggestionService;

    @GetMapping("/suggestions")
    public ResponseEntity<ProductionSuggestionSummaryResponse> getSuggestions() {
        return ResponseEntity.ok(productionSuggestionService.getSuggestionsSummary());
    }
}