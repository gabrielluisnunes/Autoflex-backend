package com.autoflex.backend.integration;

import com.autoflex.backend.entity.Product;
import com.autoflex.backend.entity.ProductRawMaterial;
import com.autoflex.backend.entity.RawMaterial;
import com.autoflex.backend.repository.ProductRawMaterialRepository;
import com.autoflex.backend.repository.ProductRepository;
import com.autoflex.backend.repository.RawMaterialRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
}