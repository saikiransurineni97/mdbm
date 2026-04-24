package com.project.mdbm.mapper;

import com.project.mdbm.dto.ProductRequest;
import com.project.mdbm.dto.ProductResponse;
import com.project.mdbm.entity.ProductJPA;
import com.project.mdbm.entity.ProductMongo;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public ProductJPA toJpa(ProductRequest request) {
        if (request == null) {
            return null;
        }

        return ProductJPA.builder()
                .productCode(request.getProductCode())
                .productName(request.getProductName())
                .category(request.getCategory())
                .department(request.getDepartment())
                .price(request.getPrice())
                .build();
    }

    public ProductMongo toMongo(ProductRequest request) {
        if (request == null) {
            return null;
        }

        return ProductMongo.builder()
                .productCode(request.getProductCode())
                .productName(request.getProductName())
                .category(request.getCategory())
                .department(request.getDepartment())
                .price(request.getPrice())
                .build();
    }

    public ProductResponse toResponse(ProductJPA entity) {
        if (entity == null) {
            return null;
        }

        return ProductResponse.builder()
                .id(entity.getId() != null ? String.valueOf(entity.getId()) : null)
                .productCode(entity.getProductCode())
                .productName(entity.getProductName())
                .category(entity.getCategory())
                .department(entity.getDepartment())
                .price(entity.getPrice())
                .build();
    }

    public ProductResponse toResponse(ProductMongo entity) {
        if (entity == null) {
            return null;
        }

        return ProductResponse.builder()
                .id(entity.getId())
                .productCode(entity.getProductCode())
                .productName(entity.getProductName())
                .category(entity.getCategory())
                .department(entity.getDepartment())
                .price(entity.getPrice())
                .build();
    }
}
