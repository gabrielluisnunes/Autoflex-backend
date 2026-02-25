package com.autoflex.backend.mapper;

import com.autoflex.backend.dto.productrawmaterial.ProductRawMaterialResponse;
import com.autoflex.backend.entity.ProductRawMaterial;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductRawMaterialMapper {

    @Mapping(target = "rawMaterialId", source = "rawMaterial.id")
    @Mapping(target = "rawMaterialCode", source = "rawMaterial.code")
    @Mapping(target = "rawMaterialName", source = "rawMaterial.name")
    ProductRawMaterialResponse toResponse(ProductRawMaterial entity);
}