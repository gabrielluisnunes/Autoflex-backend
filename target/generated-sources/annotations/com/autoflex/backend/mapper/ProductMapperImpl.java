package com.autoflex.backend.mapper;

import com.autoflex.backend.dto.product.ProductRequest;
import com.autoflex.backend.entity.Product;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-25T14:46:00-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 25.0.1 (Oracle Corporation)"
)
@Component
public class ProductMapperImpl implements ProductMapper {

    @Override
    public Product toEntity(ProductRequest request) {
        if ( request == null ) {
            return null;
        }

        Product.ProductBuilder product = Product.builder();

        product.code( request.code() );
        product.name( request.name() );
        product.price( request.price() );

        return product.build();
    }

    @Override
    public void updateEntity(ProductRequest request, Product entity) {
        if ( request == null ) {
            return;
        }

        if ( request.code() != null ) {
            entity.setCode( request.code() );
        }
        if ( request.name() != null ) {
            entity.setName( request.name() );
        }
        if ( request.price() != null ) {
            entity.setPrice( request.price() );
        }
    }
}
