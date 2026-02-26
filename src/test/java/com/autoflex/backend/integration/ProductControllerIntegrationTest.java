package com.autoflex.backend.integration;

import com.autoflex.backend.entity.Product;
import com.autoflex.backend.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class ProductControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void cleanDatabase() {
        productRepository.deleteAll();
    }

    @Test
    void shouldCreateProductSuccessfully() throws Exception {
        Map<String, Object> payload = Map.of(
                "code", "PRD-001",
                "name", "Product One",
                "price", new BigDecimal("99.90"));

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.code").value("PRD-001"))
                .andExpect(jsonPath("$.name").value("Product One"))
                .andExpect(jsonPath("$.price").value(99.90));
    }

    @Test
    void shouldReturnPaginatedProducts() throws Exception {
        productRepository.save(Product.builder()
                .code("PRD-002")
                .name("Product Two")
                .price(new BigDecimal("10.00"))
                .build());
        productRepository.save(Product.builder()
                .code("PRD-003")
                .name("Product Three")
                .price(new BigDecimal("15.00"))
                .build());

        mockMvc.perform(get("/api/products?page=0&size=1&sort=name,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.totalElements").value(2));
    }
}