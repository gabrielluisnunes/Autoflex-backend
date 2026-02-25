package com.autoflex.backend.service;

import com.autoflex.backend.dto.production.ProductionSuggestionResponse;

import java.util.List;

public interface ProductionSuggestionService {

    List<ProductionSuggestionResponse> getSuggestions();
}