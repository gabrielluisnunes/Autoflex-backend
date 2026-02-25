package com.autoflex.backend.mapper;

import com.autoflex.backend.dto.productrawmaterial.ProductRawMaterialResponse;
import com.autoflex.backend.entity.ProductRawMaterial;
import com.autoflex.backend.entity.RawMaterial;
import java.math.BigDecimal;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-25T14:46:00-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 25.0.1 (Oracle Corporation)"
)
@Component
public class ProductRawMaterialMapperImpl implements ProductRawMaterialMapper {

    @Override
    public ProductRawMaterialResponse toResponse(ProductRawMaterial entity) {
        if ( entity == null ) {
            return null;
        }

        Long rawMaterialId = null;
        String rawMaterialCode = null;
        String rawMaterialName = null;
        Long id = null;
        BigDecimal requiredQuantity = null;

        rawMaterialId = entityRawMaterialId( entity );
        rawMaterialCode = entityRawMaterialCode( entity );
        rawMaterialName = entityRawMaterialName( entity );
        id = entity.getId();
        requiredQuantity = entity.getRequiredQuantity();

        ProductRawMaterialResponse productRawMaterialResponse = new ProductRawMaterialResponse( id, rawMaterialId, rawMaterialCode, rawMaterialName, requiredQuantity );

        return productRawMaterialResponse;
    }

    private Long entityRawMaterialId(ProductRawMaterial productRawMaterial) {
        if ( productRawMaterial == null ) {
            return null;
        }
        RawMaterial rawMaterial = productRawMaterial.getRawMaterial();
        if ( rawMaterial == null ) {
            return null;
        }
        Long id = rawMaterial.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String entityRawMaterialCode(ProductRawMaterial productRawMaterial) {
        if ( productRawMaterial == null ) {
            return null;
        }
        RawMaterial rawMaterial = productRawMaterial.getRawMaterial();
        if ( rawMaterial == null ) {
            return null;
        }
        String code = rawMaterial.getCode();
        if ( code == null ) {
            return null;
        }
        return code;
    }

    private String entityRawMaterialName(ProductRawMaterial productRawMaterial) {
        if ( productRawMaterial == null ) {
            return null;
        }
        RawMaterial rawMaterial = productRawMaterial.getRawMaterial();
        if ( rawMaterial == null ) {
            return null;
        }
        String name = rawMaterial.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }
}
