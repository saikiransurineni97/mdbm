package com.project.mdbm.repository;

import com.project.mdbm.entity.EmployeeJPA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeJPA, Long> {
    boolean existsByEmail(String email);
}