package com.project.mdbm.controller;

import com.project.mdbm.dto.EmployeeRequest;
import com.project.mdbm.dto.EmployeeResponse;
import com.project.mdbm.dto.GenericAPIResponse;
import com.project.mdbm.service.EmployeeService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/save")
    public ResponseEntity<GenericAPIResponse> saveEmployee(
            @Valid @RequestBody EmployeeRequest employee,
            HttpSession session) {
        return ResponseEntity.ok(employeeService.saveEmployee(employee, session));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<GenericAPIResponse> updateEmployee(
            @PathVariable String id,
            @Valid @RequestBody EmployeeRequest employee,
            HttpSession session) {
        return ResponseEntity.ok(employeeService.updateEmployee(id, employee, session));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<GenericAPIResponse> deleteEmployee(
            @PathVariable String id,
            HttpSession session) {
        return ResponseEntity.ok(employeeService.deleteEmployee(id, session));
    }

    @GetMapping("/all")
    public ResponseEntity<List<EmployeeResponse>> getAllEmployees(HttpSession session) {
        return ResponseEntity.ok(employeeService.getAllEmployees(session));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponse> getEmployeeById(
            @PathVariable String id,
            HttpSession session) {
        return ResponseEntity.ok(employeeService.getEmployeeById(id, session));
    }
}