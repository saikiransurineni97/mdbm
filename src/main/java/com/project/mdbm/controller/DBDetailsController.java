package com.project.mdbm.controller;

import com.project.mdbm.dto.GenericAPIResponse;
import com.project.mdbm.entity.DBDetails;
import com.project.mdbm.service.DBDetailsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dbdetails")
@CrossOrigin(origins = "http://localhost:3000")
public class DBDetailsController {

    @Autowired
    private DBDetailsService dbDetailsService;

    @PostMapping("/save")
    public ResponseEntity<GenericAPIResponse> saveDBDetails(@Valid @RequestBody DBDetails dbDetails) {
        return ResponseEntity.ok(dbDetailsService.saveDBDetails(dbDetails));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<GenericAPIResponse> updateDBDetails(@PathVariable Long id, @Valid @RequestBody DBDetails dbDetails) {
        return ResponseEntity.ok(dbDetailsService.updateDBDetails(id, dbDetails));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<GenericAPIResponse> deleteDBDetails(@PathVariable Long id) {
        return ResponseEntity.ok(dbDetailsService.deleteDBDetails(id));
    }


    @GetMapping("/all")
    public ResponseEntity<List<DBDetails>> getAllDBDetails() {
        return ResponseEntity.ok(dbDetailsService.getAllDBDetails());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DBDetails> getDBDetailsById(@PathVariable Long id) {
        return ResponseEntity.ok(dbDetailsService.getDBDetailsById(id));
    }

}