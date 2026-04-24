package com.project.mdbm.controller;

import com.project.mdbm.dto.GenericAPIResponse;
import com.project.mdbm.dto.StudentRequest;
import com.project.mdbm.dto.StudentResponse;
import com.project.mdbm.service.StudentService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @PostMapping("/save")
    public ResponseEntity<GenericAPIResponse> saveStudent(
            @Valid @RequestBody StudentRequest student,
            HttpSession session) {
        return ResponseEntity.ok(studentService.saveStudent(student, session));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<GenericAPIResponse> updateStudent(
            @PathVariable String id,
            @Valid @RequestBody StudentRequest student,
            HttpSession session) {
        return ResponseEntity.ok(studentService.updateStudent(id, student, session));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<GenericAPIResponse> deleteStudent(
            @PathVariable String id,
            HttpSession session) {
        return ResponseEntity.ok(studentService.deleteStudent(id, session));
    }

    @GetMapping("/all")
    public ResponseEntity<List<StudentResponse>> getAllStudents(HttpSession session) {
        return ResponseEntity.ok(studentService.getAllStudents(session));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentResponse> getStudentById(
            @PathVariable String id,
            HttpSession session) {
        return ResponseEntity.ok(studentService.getStudentById(id, session));
    }
}