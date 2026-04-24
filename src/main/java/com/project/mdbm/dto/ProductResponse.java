package com.project.mdbm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {

    private String id;
    private String productCode;
    private String productName;
    private String category;
    private String department;
    private Long price;
}