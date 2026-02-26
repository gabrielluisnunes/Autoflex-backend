package com.autoflex.backend.integration;

import com.autoflex.backend.entity.RawMaterial;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class RawMaterialControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RawMaterialRepository rawMaterialRepository;

    @BeforeEach
    void cleanDatabase() {
        rawMaterialRepository.deleteAll();
    }

    @Test
    void shouldCreateRawMaterialSuccessfully() throws Exception {
        Map<String, Object> payload = Map.of(
                "code", "RM-001",
                "name", "Steel",
                "stockQuantity", new BigDecimal("120.5000"));

        mockMvc.perform(post("/api/raw-materials")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.code").value("RM-001"))
                .andExpect(jsonPath("$.name").value("Steel"))
                .andExpect(jsonPath("$.stockQuantity").value(120.5000));
    }

    @Test
    void shouldReturnPaginatedRawMaterials() throws Exception {
        rawMaterialRepository.save(RawMaterial.builder()
                .code("RM-002")
                .name("Plastic")
                .stockQuantity(new BigDecimal("50.0000"))
                .build());
        rawMaterialRepository.save(RawMaterial.builder()
                .code("RM-003")
                .name("Aluminum")
                .stockQuantity(new BigDecimal("70.0000"))
                .build());

        mockMvc.perform(get("/api/raw-materials?page=0&size=1&sort=name,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.page.totalElements").value(2));
    }

    @Test
    void shouldReturnBadRequestWhenRawMaterialPayloadIsInvalid() throws Exception {
        Map<String, Object> payload = Map.of(
                "code", "",
                "name", "",
                "stockQuantity", new BigDecimal("-1"));

        mockMvc.perform(post("/api/raw-materials")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.validationErrors.code").exists())
                .andExpect(jsonPath("$.validationErrors.name").exists())
                .andExpect(jsonPath("$.validationErrors.stockQuantity").exists());
    }

    @Test
    void shouldReturnNotFoundWhenRawMaterialDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/raw-materials/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Raw material not found with id: 999999"));
    }

    @Test
    void shouldReturnConflictWhenRawMaterialCodeAlreadyExists() throws Exception {
        rawMaterialRepository.save(RawMaterial.builder()
                .code("RM-001")
                .name("Existing RM")
                .stockQuantity(new BigDecimal("1.0000"))
                .build());

        Map<String, Object> payload = Map.of(
                "code", "RM-001",
                "name", "New RM",
                "stockQuantity", new BigDecimal("10.0000"));

        mockMvc.perform(post("/api/raw-materials")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Raw material code already exists: RM-001"));
    }
}