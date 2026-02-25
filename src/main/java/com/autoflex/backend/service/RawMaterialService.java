package com.autoflex.backend.service;

import com.autoflex.backend.dto.rawmaterial.RawMaterialRequest;
import com.autoflex.backend.dto.rawmaterial.RawMaterialResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RawMaterialService {

    Page<RawMaterialResponse> findAll(Pageable pageable);

    RawMaterialResponse findById(Long id);

    RawMaterialResponse create(RawMaterialRequest request);

    RawMaterialResponse update(Long id, RawMaterialRequest request);

    void delete(Long id);
}