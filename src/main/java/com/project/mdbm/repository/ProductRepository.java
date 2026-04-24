package com.project.mdbm.repository;

import com.project.mdbm.entity.ProductJPA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<ProductJPA, Long> {
    boolean existsByProductCode(String productCode);
}
