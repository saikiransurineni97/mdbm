package com.project.mdbm.repository;

import com.project.mdbm.entity.DBDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DBDetailsRepository extends JpaRepository<DBDetails, Long> {

    DBDetails findByConnectionName(String dbName);

    boolean existsByConnectionName(String dbName);
}


