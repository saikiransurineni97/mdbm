package com.project.mdbm.mapper;

import com.project.mdbm.dto.EmployeeRequest;
import com.project.mdbm.dto.EmployeeResponse;
import com.project.mdbm.entity.EmployeeJPA;
import com.project.mdbm.entity.EmployeeMongo;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {

    public EmployeeJPA toJpa(EmployeeRequest request) {
        return EmployeeJPA.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .department(request.getDepartment())
                .designation(request.getDesignation())
                .build();
    }

    public EmployeeMongo toMongo(EmployeeRequest request) {
        return EmployeeMongo.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .department(request.getDepartment())
                .designation(request.getDesignation())
                .build();
    }

    public EmployeeResponse toResponse(EmployeeJPA employee) {
        return EmployeeResponse.builder()
                .id(employee.getId() != null ? String.valueOf(employee.getId()) : null)
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .email(employee.getEmail())
                .phone(employee.getPhone())
                .department(employee.getDepartment())
                .designation(employee.getDesignation())
                .build();
    }

    public EmployeeResponse toResponse(EmployeeMongo employee) {
        return EmployeeResponse.builder()
                .id(employee.getId())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .email(employee.getEmail())
                .phone(employee.getPhone())
                .department(employee.getDepartment())
                .designation(employee.getDesignation())
                .build();
    }
}