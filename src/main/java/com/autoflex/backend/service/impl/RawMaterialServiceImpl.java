package com.autoflex.backend.service.impl;

import com.autoflex.backend.dto.rawmaterial.RawMaterialRequest;
import com.autoflex.backend.dto.rawmaterial.RawMaterialResponse;
import com.autoflex.backend.entity.RawMaterial;
import com.autoflex.backend.exception.ConflictException;
import com.autoflex.backend.exception.ResourceNotFoundException;
import com.autoflex.backend.mapper.RawMaterialMapper;
import com.autoflex.backend.repository.RawMaterialRepository;
import com.autoflex.backend.service.RawMaterialService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RawMaterialServiceImpl implements RawMaterialService {

    private final RawMaterialRepository rawMaterialRepository;
    private final RawMaterialMapper rawMaterialMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<RawMaterialResponse> findAll(Pageable pageable) {
        return rawMaterialRepository.findAll(Objects.requireNonNull(pageable)).map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public RawMaterialResponse findById(Long id) {
        RawMaterial rawMaterial = findRawMaterialById(id);
        return toResponse(rawMaterial);
    }

    @Override
    @Transactional
    public RawMaterialResponse create(RawMaterialRequest request) {
        validateUniqueCode(request.code(), null);
        RawMaterial rawMaterial = Objects.requireNonNull(rawMaterialMapper.toEntity(request));
        RawMaterial saved = rawMaterialRepository.save(Objects.requireNonNull(rawMaterial));
        return toResponse(saved);
    }

    @Override
    @Transactional
    public RawMaterialResponse update(Long id, RawMaterialRequest request) {
        RawMaterial rawMaterial = findRawMaterialById(id);
        validateUniqueCode(request.code(), id);
        rawMaterialMapper.updateEntity(request, rawMaterial);
        RawMaterial saved = rawMaterialRepository.save(Objects.requireNonNull(rawMaterial));
        return toResponse(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        RawMaterial rawMaterial = findRawMaterialById(id);
        rawMaterialRepository.delete(Objects.requireNonNull(rawMaterial));
    }

    private void validateUniqueCode(String code, Long id) {
        boolean exists = id == null
                ? rawMaterialRepository.existsByCode(code)
                : rawMaterialRepository.existsByCodeAndIdNot(code, id);
        if (exists) {
            throw new ConflictException("Raw material code already exists: " + code);
        }
    }

    private RawMaterial findRawMaterialById(Long id) {
        return rawMaterialRepository.findById(Objects.requireNonNull(id))
                .orElseThrow(() -> new ResourceNotFoundException("Raw material not found with id: " + id));
    }

    private RawMaterialResponse toResponse(RawMaterial entity) {
        return new RawMaterialResponse(
                entity.getId(),
                entity.getCode(),
                entity.getName(),
                entity.getStockQuantity());
    }
}