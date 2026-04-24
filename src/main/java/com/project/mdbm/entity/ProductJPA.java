package com.project.mdbm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product")
public class ProductJPA {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_code", nullable = false, unique = true)
    private String productCode;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "category", nullable = false)
    private String category;

    @Column(name = "department", nullable = false)
    private String department;

    @Column(name = "price", nullable = false)
    private Long price;
}
