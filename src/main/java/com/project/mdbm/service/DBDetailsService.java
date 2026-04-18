package com.project.mdbm.service;

import com.project.mdbm.dto.GenericAPIResponse;
import com.project.mdbm.entity.DBDetails;
import com.project.mdbm.repository.DBDetailsRepository;
import com.project.mdbm.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DBDetailsService {

    @Autowired
    private DBDetailsRepository dbDetailsRepository;

    public GenericAPIResponse saveDBDetails(DBDetails dbDetails) {

        GenericAPIResponse genericAPIResponse = ResponseUtils.getResponseObject();

        if (dbDetailsRepository.existsByConnectionName(dbDetails.getConnectionName())) {
            genericAPIResponse.setCode(400);
            genericAPIResponse.setMessage("DBDetails With Same Name Already Exists");
            return genericAPIResponse;
        }

        dbDetailsRepository.save(dbDetails);
        genericAPIResponse.setCode(200);
        genericAPIResponse.setMessage("DB Details saved successfully");
        return genericAPIResponse;
    }

    public GenericAPIResponse updateDBDetails(Long id, DBDetails dbDetails) {
        GenericAPIResponse genericAPIResponse = ResponseUtils.getResponseObject();

        Optional<DBDetails> existingOptional = dbDetailsRepository.findById(id);
        if (existingOptional.isEmpty()) {
            genericAPIResponse.setCode(400);
            genericAPIResponse.setMessage("DB Details not found");
            return genericAPIResponse;
        }

        DBDetails existing = existingOptional.get();
        existing.setConnectionName(dbDetails.getConnectionName());
        existing.setDbType(dbDetails.getDbType());
        existing.setUrl(dbDetails.getUrl());
        existing.setUserName(dbDetails.getUserName());
        existing.setPassword(dbDetails.getPassword());

        dbDetailsRepository.save(existing);

        genericAPIResponse.setCode(200);
        genericAPIResponse.setMessage("DB Details updated successfully");
        return genericAPIResponse;
    }

    public GenericAPIResponse deleteDBDetails(Long id) {
        GenericAPIResponse genericAPIResponse = ResponseUtils.getResponseObject();

        if (!dbDetailsRepository.existsById(id)) {
            genericAPIResponse.setCode(400);
            genericAPIResponse.setMessage("DB Details not found");
            return genericAPIResponse;
        }

        dbDetailsRepository.deleteById(id);
        genericAPIResponse.setCode(200);
        genericAPIResponse.setMessage("DB Details deleted successfully");
        return genericAPIResponse;
    }

    public List<DBDetails> getAllDBDetails() {
        return dbDetailsRepository.findAll();
    }

    public DBDetails getDBDetailsById(Long id) {
        return dbDetailsRepository.findById(id).orElse(null);
    }

}