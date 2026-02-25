package com.autoflex.backend.service.impl;

import com.autoflex.backend.dto.product.ProductRequest;
import com.autoflex.backend.dto.product.ProductResponse;
import com.autoflex.backend.dto.productrawmaterial.ProductRawMaterialRequest;
import com.autoflex.backend.dto.productrawmaterial.ProductRawMaterialResponse;
import com.autoflex.backend.entity.Product;
import com.autoflex.backend.entity.ProductRawMaterial;
import com.autoflex.backend.entity.RawMaterial;
import com.autoflex.backend.exception.ConflictException;
import com.autoflex.backend.exception.ResourceNotFoundException;
import com.autoflex.backend.mapper.ProductMapper;
import com.autoflex.backend.mapper.ProductRawMaterialMapper;
import com.autoflex.backend.repository.ProductRawMaterialRepository;
import com.autoflex.backend.repository.ProductRepository;
import com.autoflex.backend.repository.RawMaterialRepository;
import com.autoflex.backend.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final RawMaterialRepository rawMaterialRepository;
    private final ProductRawMaterialRepository productRawMaterialRepository;
    private final ProductMapper productMapper;
    private final ProductRawMaterialMapper productRawMaterialMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> findAll(Pageable pageable) {
        return productRepository.findAll(Objects.requireNonNull(pageable)).map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse findById(Long id) {
        Product product = findProductById(id);
        return toResponse(product);
    }

    @Override
    @Transactional
    public ProductResponse create(ProductRequest request) {
        validateUniqueCode(request.code(), null);
        Product product = Objects.requireNonNull(productMapper.toEntity(request));
        Product saved = productRepository.save(Objects.requireNonNull(product));
        return toResponse(saved);
    }

    @Override
    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = findProductById(id);
        validateUniqueCode(request.code(), id);
        productMapper.updateEntity(request, product);
        Product saved = productRepository.save(Objects.requireNonNull(product));
        return toResponse(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Product product = findProductById(id);
        productRepository.delete(Objects.requireNonNull(product));
    }

    @Override
    @Transactional
    public ProductResponse addRawMaterial(Long productId, ProductRawMaterialRequest request) {
        Product product = findProductById(productId);
        RawMaterial rawMaterial = rawMaterialRepository.findById(Objects.requireNonNull(request.rawMaterialId()))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Raw material not found with id: " + request.rawMaterialId()));

        productRawMaterialRepository.findByProductIdAndRawMaterialId(productId, request.rawMaterialId())
                .ifPresent(existing -> {
                    throw new ConflictException("Raw material is already associated with this product");
                });

        ProductRawMaterial association = ProductRawMaterial.builder()
                .product(product)
                .rawMaterial(rawMaterial)
                .requiredQuantity(request.requiredQuantity())
                .build();

        product.getProductRawMaterials().add(association);
        productRepository.save(product);
        return toResponse(product);
    }

    @Override
    @Transactional
    public ProductResponse removeRawMaterial(Long productId, Long rawMaterialId) {
        Product product = findProductById(productId);

        ProductRawMaterial association = productRawMaterialRepository
                .findByProductIdAndRawMaterialId(productId, rawMaterialId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Association not found for product id " + productId + " and raw material id " + rawMaterialId));

        product.getProductRawMaterials().remove(association);
        productRawMaterialRepository.delete(Objects.requireNonNull(association));
        return toResponse(product);
    }

    private void validateUniqueCode(String code, Long id) {
        boolean exists = id == null
                ? productRepository.existsByCode(code)
                : productRepository.existsByCodeAndIdNot(code, id);
        if (exists) {
            throw new ConflictException("Product code already exists: " + code);
        }
    }

    private Product findProductById(Long id) {
        return productRepository.findById(Objects.requireNonNull(id))
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    private ProductResponse toResponse(Product product) {
        List<ProductRawMaterialResponse> rawMaterials = product.getProductRawMaterials().stream()
                .sorted(Comparator.comparing(ProductRawMaterial::getId, Comparator.nullsLast(Long::compareTo)))
                .map(productRawMaterialMapper::toResponse)
                .toList();

        return new ProductResponse(
                product.getId(),
                product.getCode(),
                product.getName(),
                product.getPrice(),
                rawMaterials);
    }
}