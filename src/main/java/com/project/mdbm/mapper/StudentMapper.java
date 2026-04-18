package com.project.mdbm.mapper;

import com.project.mdbm.dto.StudentRequest;
import com.project.mdbm.dto.StudentResponse;
import com.project.mdbm.entity.StudentJPA;
import com.project.mdbm.entity.StudentMongo;
import org.springframework.stereotype.Component;

@Component
public class StudentMapper {

    public StudentJPA toJpa(StudentRequest request) {
        return StudentJPA.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .course(request.getCourse())
                .build();
    }

    public StudentMongo toMongo(StudentRequest request) {
        return StudentMongo.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .course(request.getCourse())
                .build();
    }

    public StudentResponse toResponse(StudentJPA student) {
        return StudentResponse.builder()
                .id(student.getId() != null ? String.valueOf(student.getId()) : null)
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .email(student.getEmail())
                .phone(student.getPhone())
                .course(student.getCourse())
                .build();
    }

    public StudentResponse toResponse(StudentMongo student) {
        return StudentResponse.builder()
                .id(student.getId())
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .email(student.getEmail())
                .phone(student.getPhone())
                .course(student.getCourse())
                .build();
    }

}