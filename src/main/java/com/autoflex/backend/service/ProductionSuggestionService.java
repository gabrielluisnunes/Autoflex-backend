package com.autoflex.backend.service;

import com.autoflex.backend.dto.production.ProductionExecutionRequest;
import com.autoflex.backend.dto.production.ProductionExecutionResponse;
import com.autoflex.backend.dto.production.ProductionSuggestionResponse;
import com.autoflex.backend.dto.production.ProductionSuggestionSummaryResponse;

import java.util.List;

public interface ProductionSuggestionService {

    List<ProductionSuggestionResponse> getSuggestions();

    ProductionSuggestionSummaryResponse getSuggestionsSummary();

    ProductionExecutionResponse executeProduction(ProductionExecutionRequest request);
}