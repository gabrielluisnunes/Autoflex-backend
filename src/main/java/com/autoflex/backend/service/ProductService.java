package com.autoflex.backend.service;

import com.autoflex.backend.dto.product.ProductRequest;
import com.autoflex.backend.dto.product.ProductResponse;
import com.autoflex.backend.dto.productrawmaterial.ProductRawMaterialRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {

    Page<ProductResponse> findAll(Pageable pageable);

    ProductResponse findById(Long id);

    ProductResponse create(ProductRequest request);

    ProductResponse update(Long id, ProductRequest request);

    void delete(Long id);

    ProductResponse addRawMaterial(Long productId, ProductRawMaterialRequest request);

    ProductResponse removeRawMaterial(Long productId, Long rawMaterialId);
}