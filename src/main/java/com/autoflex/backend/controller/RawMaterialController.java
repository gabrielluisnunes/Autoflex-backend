package com.autoflex.backend.controller;

import com.autoflex.backend.dto.rawmaterial.RawMaterialRequest;
import com.autoflex.backend.dto.rawmaterial.RawMaterialResponse;
import com.autoflex.backend.service.RawMaterialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/raw-materials")
@RequiredArgsConstructor
@Tag(name = "Raw Materials", description = "Operations for raw material management")
public class RawMaterialController {

    private final RawMaterialService rawMaterialService;

    @GetMapping
    @Operation(summary = "List raw materials", description = "Returns paginated raw materials")
    public ResponseEntity<Page<RawMaterialResponse>> findAll(
            @ParameterObject @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        return ResponseEntity.ok(rawMaterialService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get raw material by id")
    public ResponseEntity<RawMaterialResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(rawMaterialService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Create a raw material")
    public ResponseEntity<RawMaterialResponse> create(@Valid @RequestBody RawMaterialRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(rawMaterialService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a raw material")
    public ResponseEntity<RawMaterialResponse> update(@PathVariable Long id,
            @Valid @RequestBody RawMaterialRequest request) {
        return ResponseEntity.ok(rawMaterialService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a raw material")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        rawMaterialService.delete(id);
        return ResponseEntity.noContent().build();
    }
}