package com.autoflex.backend.service.impl;

import com.autoflex.backend.dto.production.ProductionConsumptionResponse;
import com.autoflex.backend.dto.production.ProductionExecutionRequest;
import com.autoflex.backend.dto.production.ProductionExecutionResponse;
import com.autoflex.backend.dto.production.ProductionSuggestionResponse;
import com.autoflex.backend.dto.production.ProductionSuggestionSummaryResponse;
import com.autoflex.backend.entity.Product;
import com.autoflex.backend.entity.ProductRawMaterial;
import com.autoflex.backend.entity.RawMaterial;
import com.autoflex.backend.exception.ConflictException;
import com.autoflex.backend.exception.ResourceNotFoundException;
import com.autoflex.backend.repository.ProductRepository;
import com.autoflex.backend.repository.RawMaterialRepository;
import com.autoflex.backend.service.ProductionSuggestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductionSuggestionServiceImpl implements ProductionSuggestionService {

    private final ProductRepository productRepository;
    private final RawMaterialRepository rawMaterialRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ProductionSuggestionResponse> getSuggestions() {
        return productRepository.findAllWithGraphBy().stream()
                .map(this::toSuggestion)
                .sorted(Comparator.comparing(ProductionSuggestionResponse::totalValue).reversed())
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductionSuggestionSummaryResponse getSuggestionsSummary() {
        List<ProductionSuggestionResponse> suggestions = getSuggestions();
        BigDecimal totalProductionValue = suggestions.stream()
                .map(ProductionSuggestionResponse::totalValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new ProductionSuggestionSummaryResponse(suggestions, totalProductionValue);
    }

    @Override
    @Transactional
    public ProductionExecutionResponse executeProduction(ProductionExecutionRequest request) {
        Product product = productRepository.findWithGraphById(Objects.requireNonNull(request.productId()))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + request.productId()));

        if (product.getProductRawMaterials().isEmpty()) {
            throw new ConflictException("Product has no raw materials configured for production");
        }

        BigDecimal productionQuantity = BigDecimal.valueOf(request.quantity());
        Map<Long, BigDecimal> requiredByRawMaterial = new HashMap<>();

        for (ProductRawMaterial association : product.getProductRawMaterials()) {
            BigDecimal required = association.getRequiredQuantity();
            if (required == null || required.signum() <= 0) {
                throw new ConflictException(
                        "Invalid required quantity for raw material association id: " + association.getId());
            }

            Long rawMaterialId = association.getRawMaterial().getId();
            BigDecimal totalRequired = required.multiply(productionQuantity);
            requiredByRawMaterial.merge(rawMaterialId, totalRequired, BigDecimal::add);
        }

        Set<Long> rawMaterialIds = requiredByRawMaterial.keySet();
        List<RawMaterial> lockedMaterials = rawMaterialRepository.findAllByIdInForUpdate(rawMaterialIds);

        if (lockedMaterials.size() != rawMaterialIds.size()) {
            throw new ResourceNotFoundException("One or more raw materials configured for this product were not found");
        }

        Map<Long, RawMaterial> lockedById = lockedMaterials.stream()
                .collect(Collectors.toMap(RawMaterial::getId, rawMaterial -> rawMaterial));

        for (Map.Entry<Long, BigDecimal> entry : requiredByRawMaterial.entrySet()) {
            RawMaterial rawMaterial = lockedById.get(entry.getKey());
            BigDecimal available = rawMaterial.getStockQuantity();
            BigDecimal required = entry.getValue();

            if (available.compareTo(required) < 0) {
                throw new ConflictException("Insufficient stock for raw material "
                        + rawMaterial.getCode()
                        + ": required="
                        + required
                        + ", available="
                        + available);
            }
        }

        List<ProductionConsumptionResponse> consumed = new ArrayList<>();

        for (Map.Entry<Long, BigDecimal> entry : requiredByRawMaterial.entrySet()) {
            RawMaterial rawMaterial = lockedById.get(entry.getKey());
            BigDecimal consumedQuantity = entry.getValue();
            BigDecimal remainingStock = rawMaterial.getStockQuantity().subtract(consumedQuantity);
            rawMaterial.setStockQuantity(remainingStock);

            consumed.add(new ProductionConsumptionResponse(
                    rawMaterial.getId(),
                    rawMaterial.getCode(),
                    rawMaterial.getName(),
                    consumedQuantity,
                    remainingStock));
        }

        rawMaterialRepository.saveAll(lockedMaterials);
        consumed.sort(Comparator.comparing(ProductionConsumptionResponse::rawMaterialCode));

        return new ProductionExecutionResponse(
                product.getId(),
                product.getCode(),
                product.getName(),
                request.quantity(),
                consumed);
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