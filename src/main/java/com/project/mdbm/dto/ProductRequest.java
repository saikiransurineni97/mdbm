package com.project.mdbm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {

    @NotBlank(message = "Product code is mandatory")
    private String productCode;

    @NotBlank(message = "Product name is mandatory")
    private String productName;

    @NotBlank(message = "Category is mandatory")
    private String category;

    @NotBlank(message = "Department is mandatory")
    private String department;

    @NotNull(message = "Price is mandatory")
    private Long price;
}