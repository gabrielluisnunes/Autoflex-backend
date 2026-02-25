package com.autoflex.backend.repository;

import com.autoflex.backend.entity.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsByCode(String code);

    boolean existsByCodeAndIdNot(String code, Long id);

    @EntityGraph(attributePaths = { "productRawMaterials", "productRawMaterials.rawMaterial" })
    List<Product> findAllWithGraphBy();
}