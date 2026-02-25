package com.autoflex.backend.service.impl;

import com.autoflex.backend.dto.production.ProductionSuggestionResponse;
import com.autoflex.backend.entity.Product;
import com.autoflex.backend.entity.ProductRawMaterial;
import com.autoflex.backend.entity.RawMaterial;
import com.autoflex.backend.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductionSuggestionServiceImplTest {

        @Mock
        private ProductRepository productRepository;

        @InjectMocks
        private ProductionSuggestionServiceImpl productionSuggestionService;

        private Product productA;
        private Product productB;

        @BeforeEach
        void setUp() {
                RawMaterial steel = RawMaterial.builder()
                                .id(1L)
                                .code("RM-STEEL")
                                .name("Steel")
                                .stockQuantity(new BigDecimal("100"))
                                .build();

                RawMaterial plastic = RawMaterial.builder()
                                .id(2L)
                                .code("RM-PLASTIC")
                                .name("Plastic")
                                .stockQuantity(new BigDecimal("60"))
                                .build();

                productA = Product.builder()
                                .id(1L)
                                .code("P-A")
                                .name("Product A")
                                .price(new BigDecimal("50.00"))
                                .build();

                ProductRawMaterial associationA1 = ProductRawMaterial.builder()
                                .id(1L)
                                .product(productA)
                                .rawMaterial(steel)
                                .requiredQuantity(new BigDecimal("10"))
                                .build();
                ProductRawMaterial associationA2 = ProductRawMaterial.builder()
                                .id(2L)
                                .product(productA)
                                .rawMaterial(plastic)
                                .requiredQuantity(new BigDecimal("6"))
                                .build();
                productA.setProductRawMaterials(Set.of(associationA1, associationA2));

                productB = Product.builder()
                                .id(2L)
                                .code("P-B")
                                .name("Product B")
                                .price(new BigDecimal("120.00"))
                                .build();

                ProductRawMaterial associationB1 = ProductRawMaterial.builder()
                                .id(3L)
                                .product(productB)
                                .rawMaterial(steel)
                                .requiredQuantity(new BigDecimal("30"))
                                .build();
                productB.setProductRawMaterials(Set.of(associationB1));
        }

        @Test
        void shouldCalculatePossibleProductionAndSortByTotalValueDesc() {
                when(productRepository.findAllWithGraphBy()).thenReturn(List.of(productA, productB));

                List<ProductionSuggestionResponse> suggestions = productionSuggestionService.getSuggestions();

                assertThat(suggestions).hasSize(2);
                assertThat(suggestions.getFirst().productCode()).isEqualTo("P-A");
                assertThat(suggestions.getFirst().possibleQuantity()).isEqualTo(10L);
                assertThat(suggestions.getFirst().totalValue()).isEqualByComparingTo("500.00");

                assertThat(suggestions.get(1).productCode()).isEqualTo("P-B");
                assertThat(suggestions.get(1).possibleQuantity()).isEqualTo(3L);
                assertThat(suggestions.get(1).totalValue()).isEqualByComparingTo("360.00");
        }

        @Test
        void shouldReturnZeroWhenProductHasNoRawMaterialAssociation() {
                Product productWithoutMaterials = Product.builder()
                                .id(3L)
                                .code("P-C")
                                .name("Product C")
                                .price(new BigDecimal("20.00"))
                                .build();

                when(productRepository.findAllWithGraphBy()).thenReturn(List.of(productWithoutMaterials));

                List<ProductionSuggestionResponse> suggestions = productionSuggestionService.getSuggestions();

                assertThat(suggestions).hasSize(1);
                assertThat(suggestions.getFirst().possibleQuantity()).isEqualTo(0L);
                assertThat(suggestions.getFirst().totalValue()).isEqualByComparingTo("0.00");
        }
}