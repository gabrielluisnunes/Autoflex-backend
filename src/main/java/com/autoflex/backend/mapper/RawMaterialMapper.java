package com.autoflex.backend.mapper;

import com.autoflex.backend.dto.rawmaterial.RawMaterialRequest;
import com.autoflex.backend.entity.RawMaterial;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface RawMaterialMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "productRawMaterials", ignore = true)
    RawMaterial toEntity(RawMaterialRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "productRawMaterials", ignore = true)
    void updateEntity(RawMaterialRequest request, @MappingTarget RawMaterial entity);
}