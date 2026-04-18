package com.project.mdbm.repository;

import com.project.mdbm.entity.Student;
import com.project.mdbm.entity.StudentJPA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<StudentJPA, Long> {
    boolean existsByEmail(String email);
}