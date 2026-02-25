package com.autoflex.backend.repository;

import com.autoflex.backend.entity.ProductRawMaterial;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRawMaterialRepository extends JpaRepository<ProductRawMaterial, Long> {

    Optional<ProductRawMaterial> findByProductIdAndRawMaterialId(Long productId, Long rawMaterialId);
}