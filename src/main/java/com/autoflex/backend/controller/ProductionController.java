package com.autoflex.backend.controller;

import com.autoflex.backend.dto.production.ProductionSuggestionResponse;
import com.autoflex.backend.service.ProductionSuggestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/production")
@RequiredArgsConstructor
public class ProductionController {

    private final ProductionSuggestionService productionSuggestionService;

    @GetMapping("/suggestions")
    public ResponseEntity<List<ProductionSuggestionResponse>> getSuggestions() {
        return ResponseEntity.ok(productionSuggestionService.getSuggestions());
    }
}