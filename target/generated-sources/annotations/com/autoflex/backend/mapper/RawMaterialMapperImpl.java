package com.autoflex.backend.mapper;

import com.autoflex.backend.dto.rawmaterial.RawMaterialRequest;
import com.autoflex.backend.entity.RawMaterial;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-25T14:46:00-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 25.0.1 (Oracle Corporation)"
)
@Component
public class RawMaterialMapperImpl implements RawMaterialMapper {

    @Override
    public RawMaterial toEntity(RawMaterialRequest request) {
        if ( request == null ) {
            return null;
        }

        RawMaterial.RawMaterialBuilder rawMaterial = RawMaterial.builder();

        rawMaterial.code( request.code() );
        rawMaterial.name( request.name() );
        rawMaterial.stockQuantity( request.stockQuantity() );

        return rawMaterial.build();
    }

    @Override
    public void updateEntity(RawMaterialRequest request, RawMaterial entity) {
        if ( request == null ) {
            return;
        }

        if ( request.code() != null ) {
            entity.setCode( request.code() );
        }
        if ( request.name() != null ) {
            entity.setName( request.name() );
        }
        if ( request.stockQuantity() != null ) {
            entity.setStockQuantity( request.stockQuantity() );
        }
    }
}
