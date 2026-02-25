package com.autoflex.backend.service.impl;

import com.autoflex.backend.dto.production.ProductionSuggestionResponse;
import com.autoflex.backend.entity.Product;
import com.autoflex.backend.entity.ProductRawMaterial;
import com.autoflex.backend.repository.ProductRepository;
import com.autoflex.backend.service.ProductionSuggestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductionSuggestionServiceImpl implements ProductionSuggestionService {

    private final ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ProductionSuggestionResponse> getSuggestions() {
        return productRepository.findAllWithGraphBy().stream()
                .map(this::toSuggestion)
                .sorted(Comparator.comparing(ProductionSuggestionResponse::totalValue).reversed())
                .toList();
    }

    private ProductionSuggestionResponse toSuggestion(Product product) {
        long possibleQuantity = calculatePossibleProduction(product);
        BigDecimal totalValue = product.getPrice().multiply(BigDecimal.valueOf(possibleQuantity));

        return new ProductionSuggestionResponse(
                product.getId(),
                product.getCode(),
                product.getName(),
                possibleQuantity,
                product.getPrice(),
                totalValue);
    }

    private long calculatePossibleProduction(Product product) {
        if (product.getProductRawMaterials().isEmpty()) {
            return 0L;
        }

        long minimum = Long.MAX_VALUE;
        for (ProductRawMaterial association : product.getProductRawMaterials()) {
            BigDecimal required = association.getRequiredQuantity();
            if (required == null || required.signum() <= 0) {
                return 0L;
            }

            BigDecimal stock = association.getRawMaterial().getStockQuantity();
            long current = stock.divide(required, 0, RoundingMode.DOWN).longValue();
            minimum = Math.min(minimum, current);
        }

        return minimum == Long.MAX_VALUE ? 0L : minimum;
    }
}