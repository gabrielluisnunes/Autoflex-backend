package com.autoflex.backend.integration;

import com.autoflex.backend.entity.Product;
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
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
                .andExpect(jsonPath("$.page.totalElements").value(2));
    }

    @Test
    void shouldReturnBadRequestWhenPayloadIsInvalid() throws Exception {
        Map<String, Object> payload = Map.of(
                "code", "",
                "name", "",
                "price", new BigDecimal("0"));

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.validationErrors.code").exists())
                .andExpect(jsonPath("$.validationErrors.name").exists())
                .andExpect(jsonPath("$.validationErrors.price").exists());
    }

    @Test
    void shouldReturnNotFoundWhenProductDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/products/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Product not found with id: 999999"));
    }

    @Test
    void shouldReturnConflictWhenProductCodeAlreadyExists() throws Exception {
        productRepository.save(Product.builder()
                .code("PRD-001")
                .name("Existing Product")
                .price(new BigDecimal("10.00"))
                .build());

        Map<String, Object> payload = Map.of(
                "code", "PRD-001",
                "name", "New Product",
                "price", new BigDecimal("50.00"));

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Product code already exists: PRD-001"));
    }

    @Test
    void shouldAssociateRawMaterialToProduct() throws Exception {
        Product product = productRepository.save(Product.builder()
                .code("PRD-010")
                .name("Product Ten")
                .price(new BigDecimal("80.00"))
                .build());

        RawMaterial rawMaterial = rawMaterialRepository.save(RawMaterial.builder()
                .code("RM-010")
                .name("Copper")
                .stockQuantity(new BigDecimal("100.0000"))
                .build());

        Map<String, Object> payload = Map.of(
                "rawMaterialId", rawMaterial.getId(),
                "requiredQuantity", new BigDecimal("5.0000"));

        mockMvc.perform(post("/api/products/{productId}/raw-materials", product.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(product.getId()))
                .andExpect(jsonPath("$.rawMaterials.length()").value(1))
                .andExpect(jsonPath("$.rawMaterials[0].rawMaterialId").value(rawMaterial.getId()))
                .andExpect(jsonPath("$.rawMaterials[0].requiredQuantity").value(5.0000));
    }

    @Test
    void shouldRemoveRawMaterialAssociationFromProduct() throws Exception {
        Product product = productRepository.save(Product.builder()
                .code("PRD-011")
                .name("Product Eleven")
                .price(new BigDecimal("40.00"))
                .build());

        RawMaterial rawMaterial = rawMaterialRepository.save(RawMaterial.builder()
                .code("RM-011")
                .name("Nickel")
                .stockQuantity(new BigDecimal("80.0000"))
                .build());

        Map<String, Object> payload = Map.of(
                "rawMaterialId", rawMaterial.getId(),
                "requiredQuantity", new BigDecimal("2.0000"));

        mockMvc.perform(post("/api/products/{productId}/raw-materials", product.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated());

        mockMvc.perform(
                delete("/api/products/{productId}/raw-materials/{rawMaterialId}", product.getId(), rawMaterial.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(product.getId()))
                .andExpect(jsonPath("$.rawMaterials.length()").value(0));
    }

    @Test
    void shouldReturnConflictWhenAssociatingSameRawMaterialTwice() throws Exception {
        Product product = productRepository.save(Product.builder()
                .code("PRD-012")
                .name("Product Twelve")
                .price(new BigDecimal("22.00"))
                .build());

        RawMaterial rawMaterial = rawMaterialRepository.save(RawMaterial.builder()
                .code("RM-012")
                .name("Iron")
                .stockQuantity(new BigDecimal("300.0000"))
                .build());

        Map<String, Object> payload = Map.of(
                "rawMaterialId", rawMaterial.getId(),
                "requiredQuantity", new BigDecimal("3.0000"));

        mockMvc.perform(post("/api/products/{productId}/raw-materials", product.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/products/{productId}/raw-materials", product.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Raw material is already associated with this product"));
    }

    @Test
    void shouldReturnNotFoundWhenAssociatingUnknownRawMaterial() throws Exception {
        Product product = productRepository.save(Product.builder()
                .code("PRD-013")
                .name("Product Thirteen")
                .price(new BigDecimal("70.00"))
                .build());

        Map<String, Object> payload = Map.of(
                "rawMaterialId", 999999L,
                "requiredQuantity", new BigDecimal("1.0000"));

        mockMvc.perform(post("/api/products/{productId}/raw-materials", product.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Raw material not found with id: 999999"));
    }
}