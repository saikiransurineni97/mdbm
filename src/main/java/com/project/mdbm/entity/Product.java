package com.project.mdbm.entity;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_code", nullable = false)
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
