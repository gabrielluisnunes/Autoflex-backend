package com.autoflex.backend.integration;

import com.autoflex.backend.entity.Product;
import com.autoflex.backend.entity.ProductRawMaterial;
import com.autoflex.backend.entity.RawMaterial;
import com.autoflex.backend.repository.ProductRawMaterialRepository;
import com.autoflex.backend.repository.ProductRepository;
import com.autoflex.backend.repository.RawMaterialRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class ProductionControllerIntegrationTest extends AbstractIntegrationTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ProductRepository productRepository;

        @Autowired
        private RawMaterialRepository rawMaterialRepository;

        @Autowired
        private ProductRawMaterialRepository productRawMaterialRepository;

        @Autowired
        private ObjectMapper objectMapper;

        @BeforeEach
        void cleanDatabase() {
                productRawMaterialRepository.deleteAll();
                productRepository.deleteAll();
                rawMaterialRepository.deleteAll();
        }

        @Test
        void shouldReturnProductionSuggestionsSortedByTotalValueWithSummary() throws Exception {
                RawMaterial steel = rawMaterialRepository.save(RawMaterial.builder()
                                .code("RM-STEEL")
                                .name("Steel")
                                .stockQuantity(new BigDecimal("100"))
                                .build());

                RawMaterial plastic = rawMaterialRepository.save(RawMaterial.builder()
                                .code("RM-PLASTIC")
                                .name("Plastic")
                                .stockQuantity(new BigDecimal("60"))
                                .build());

                Product productA = productRepository.save(Product.builder()
                                .code("P-A")
                                .name("Product A")
                                .price(new BigDecimal("50.00"))
                                .build());

                Product productB = productRepository.save(Product.builder()
                                .code("P-B")
                                .name("Product B")
                                .price(new BigDecimal("120.00"))
                                .build());

                ProductRawMaterial associationA1 = ProductRawMaterial.builder()
                                .product(productA)
                                .rawMaterial(steel)
                                .requiredQuantity(new BigDecimal("10"))
                                .build();
                ProductRawMaterial associationA2 = ProductRawMaterial.builder()
                                .product(productA)
                                .rawMaterial(plastic)
                                .requiredQuantity(new BigDecimal("6"))
                                .build();
                productA.setProductRawMaterials(Set.of(associationA1, associationA2));

                ProductRawMaterial associationB1 = ProductRawMaterial.builder()
                                .product(productB)
                                .rawMaterial(steel)
                                .requiredQuantity(new BigDecimal("30"))
                                .build();
                productB.setProductRawMaterials(Set.of(associationB1));

                productRepository.save(productA);
                productRepository.save(productB);

                mockMvc.perform(get("/api/production/suggestions"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.suggestions.length()").value(2))
                                .andExpect(jsonPath("$.suggestions[0].productCode").value("P-A"))
                                .andExpect(jsonPath("$.suggestions[0].possibleQuantity").value(10))
                                .andExpect(jsonPath("$.suggestions[0].totalValue").value(500.00))
                                .andExpect(jsonPath("$.suggestions[1].productCode").value("P-B"))
                                .andExpect(jsonPath("$.suggestions[1].possibleQuantity").value(3))
                                .andExpect(jsonPath("$.suggestions[1].totalValue").value(360.00))
                                .andExpect(jsonPath("$.totalProductionValue").value(860.00));
        }

        @Test
        void shouldExecuteProductionAndDecrementRawMaterialStock() throws Exception {
                RawMaterial steel = rawMaterialRepository.save(RawMaterial.builder()
                                .code("RM-STEEL-EXEC")
                                .name("Steel Execution")
                                .stockQuantity(new BigDecimal("100.000"))
                                .build());

                Product product = productRepository.save(Product.builder()
                                .code("P-EXEC")
                                .name("Product Execution")
                                .price(new BigDecimal("70.00"))
                                .build());

                ProductRawMaterial association = ProductRawMaterial.builder()
                                .product(product)
                                .rawMaterial(steel)
                                .requiredQuantity(new BigDecimal("2.500"))
                                .build();

                product.setProductRawMaterials(Set.of(association));
                productRepository.save(product);

                mockMvc.perform(post("/api/production/execute")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(
                                                new ExecuteProductionPayload(product.getId(), 10L))))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.productCode").value("P-EXEC"))
                                .andExpect(jsonPath("$.producedQuantity").value(10))
                                .andExpect(jsonPath("$.consumedRawMaterials.length()").value(1))
                                .andExpect(jsonPath("$.consumedRawMaterials[0].rawMaterialCode").value("RM-STEEL-EXEC"))
                                .andExpect(jsonPath("$.consumedRawMaterials[0].consumedQuantity").value(25.000))
                                .andExpect(jsonPath("$.consumedRawMaterials[0].remainingStock").value(75.000));

                RawMaterial updated = rawMaterialRepository.findById(steel.getId()).orElseThrow();
                org.junit.jupiter.api.Assertions.assertEquals(new BigDecimal("75.000"), updated.getStockQuantity());
        }

        @Test
        void shouldReturnConflictWhenStockIsInsufficientForExecution() throws Exception {
                RawMaterial plastic = rawMaterialRepository.save(RawMaterial.builder()
                                .code("RM-PLASTIC-EXEC")
                                .name("Plastic Execution")
                                .stockQuantity(new BigDecimal("8.000"))
                                .build());

                Product product = productRepository.save(Product.builder()
                                .code("P-EXEC-CONFLICT")
                                .name("Product Execution Conflict")
                                .price(new BigDecimal("90.00"))
                                .build());

                ProductRawMaterial association = ProductRawMaterial.builder()
                                .product(product)
                                .rawMaterial(plastic)
                                .requiredQuantity(new BigDecimal("3.000"))
                                .build();

                product.setProductRawMaterials(Set.of(association));
                productRepository.save(product);

                mockMvc.perform(post("/api/production/execute")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper
                                                .writeValueAsString(new ExecuteProductionPayload(product.getId(), 3L))))
                                .andExpect(status().isConflict())
                                .andExpect(jsonPath("$.message")
                                                .value(org.hamcrest.Matchers.containsString("Insufficient stock")));

                RawMaterial unchanged = rawMaterialRepository.findById(plastic.getId()).orElseThrow();
                org.junit.jupiter.api.Assertions.assertEquals(new BigDecimal("8.000"), unchanged.getStockQuantity());
        }

        private record ExecuteProductionPayload(Long productId, Long quantity) {
        }
}