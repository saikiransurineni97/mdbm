package com.project.mdbm.service;

import com.mongodb.DuplicateKeyException;
import com.project.mdbm.config.DataSourceContextHolder;
import com.project.mdbm.config.DynamicMongoManager;
import com.project.mdbm.dto.GenericAPIResponse;
import com.project.mdbm.dto.StudentRequest;
import com.project.mdbm.dto.StudentResponse;
import com.project.mdbm.entity.StudentJPA;
import com.project.mdbm.entity.StudentMongo;
import com.project.mdbm.mapper.StudentMapper;
import com.project.mdbm.repository.StudentRepository;
import com.project.mdbm.utils.ResponseUtils;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StudentService {

    private static final String SESSION_CONNECTION_NAME = "connectionName";
    private static final String SESSION_DB_TYPE = "dbType";
    private static final String NON_RELATIONAL = "Non-Relational";

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;
    private final DynamicMongoManager dynamicMongoManager;

    public GenericAPIResponse saveStudent(StudentRequest request, HttpSession session) {
        GenericAPIResponse response = ResponseUtils.getResponseObject();

        String connectionName = (String) session.getAttribute(SESSION_CONNECTION_NAME);
        String dbType = (String) session.getAttribute(SESSION_DB_TYPE);

        if (!StringUtils.hasText(connectionName) || !StringUtils.hasText(dbType)) {
            response.setCode(400);
            response.setMessage("No database connected. Please call /api/db/connect first");
            return response;
        }

        try {
            if (NON_RELATIONAL.equalsIgnoreCase(dbType)) {
                saveStudentToMongo(request, connectionName);
            } else {
                saveStudentToJpa(request, connectionName);
            }

            response.setCode(200);
            response.setMessage("Student saved successfully in " + connectionName);
            return response;

        } catch (DuplicateKeyException | DataIntegrityViolationException e) {
            response.setCode(409);
            response.setMessage("Student with this email already exists");
            return response;

        } catch (Exception e) {
            response.setCode(500);
            response.setMessage("Failed to save student: " + e.getMessage());
            return response;

        } finally {
            DataSourceContextHolder.clear();
        }
    }

    public GenericAPIResponse updateStudent(String id, StudentRequest request, HttpSession session) {
        GenericAPIResponse response = ResponseUtils.getResponseObject();

        try {
            String connectionName = (String) session.getAttribute(SESSION_CONNECTION_NAME);
            String dbType = (String) session.getAttribute(SESSION_DB_TYPE);

            if (!StringUtils.hasText(connectionName) || !StringUtils.hasText(dbType)) {
                response.setCode(400);
                response.setMessage("No database connected. Please call /api/db/connect first");
                return response;
            }

            if (NON_RELATIONAL.equalsIgnoreCase(dbType)) {
                MongoTemplate mongoTemplate = dynamicMongoManager.getMongoTemplate(connectionName);
                StudentMongo existingStudent = mongoTemplate.findById(id, StudentMongo.class, "student");

                if (existingStudent == null) {
                    response.setCode(404);
                    response.setMessage("Student not found with id: " + id);
                    return response;
                }

                existingStudent.setFirstName(request.getFirstName());
                existingStudent.setLastName(request.getLastName());
                existingStudent.setEmail(request.getEmail());
                existingStudent.setPhone(request.getPhone());
                existingStudent.setCourse(request.getCourse());

                mongoTemplate.save(existingStudent, "student");

            } else {
                DataSourceContextHolder.setDataSourceKey(connectionName);

                Long studentId = Long.valueOf(id);
                Optional<StudentJPA> optionalStudent = studentRepository.findById(studentId);

                if (optionalStudent.isEmpty()) {
                    response.setCode(404);
                    response.setMessage("Student not found with id: " + id);
                    return response;
                }

                StudentJPA existingStudent = optionalStudent.get();
                existingStudent.setFirstName(request.getFirstName());
                existingStudent.setLastName(request.getLastName());
                existingStudent.setEmail(request.getEmail());
                existingStudent.setPhone(request.getPhone());
                existingStudent.setCourse(request.getCourse());

                studentRepository.save(existingStudent);
            }

            response.setCode(200);
            response.setMessage("Student updated successfully in " + connectionName);
            return response;

        } catch (NumberFormatException e) {
            response.setCode(400);
            response.setMessage("Invalid student id: " + id);
            return response;

        } catch (DuplicateKeyException | DataIntegrityViolationException e) {
            response.setCode(409);
            response.setMessage("Student with this email already exists");
            return response;

        } catch (Exception e) {
            response.setCode(500);
            response.setMessage("Failed to update student: " + e.getMessage());
            return response;

        } finally {
            DataSourceContextHolder.clear();
        }
    }

    public GenericAPIResponse deleteStudent(String id, HttpSession session) {
        GenericAPIResponse response = ResponseUtils.getResponseObject();

        try {
            String connectionName = (String) session.getAttribute(SESSION_CONNECTION_NAME);
            String dbType = (String) session.getAttribute(SESSION_DB_TYPE);

            if (!StringUtils.hasText(connectionName) || !StringUtils.hasText(dbType)) {
                response.setCode(400);
                response.setMessage("No database connected. Please call /api/db/connect first");
                return response;
            }

            if (NON_RELATIONAL.equalsIgnoreCase(dbType)) {
                MongoTemplate mongoTemplate = dynamicMongoManager.getMongoTemplate(connectionName);
                StudentMongo existingStudent = mongoTemplate.findById(id, StudentMongo.class, "student");

                if (existingStudent == null) {
                    response.setCode(404);
                    response.setMessage("Student not found with id: " + id);
                    return response;
                }

                mongoTemplate.remove(existingStudent, "student");
            } else {
                DataSourceContextHolder.setDataSourceKey(connectionName);

                Long studentId = Long.valueOf(id);
                if (!studentRepository.existsById(studentId)) {
                    response.setCode(404);
                    response.setMessage("Student not found with id: " + id);
                    return response;
                }

                studentRepository.deleteById(studentId);
            }

            response.setCode(200);
            response.setMessage("Student deleted successfully from " + connectionName);
            return response;

        } catch (NumberFormatException e) {
            response.setCode(400);
            response.setMessage("Invalid student id: " + id);
            return response;

        } catch (Exception e) {
            response.setCode(500);
            response.setMessage("Failed to delete student: " + e.getMessage());
            return response;

        } finally {
            DataSourceContextHolder.clear();
        }
    }

    public List<StudentResponse> getAllStudents(HttpSession session) {
        String connectionName = (String) session.getAttribute(SESSION_CONNECTION_NAME);
        String dbType = (String) session.getAttribute(SESSION_DB_TYPE);

        try {
            if (!StringUtils.hasText(connectionName) || !StringUtils.hasText(dbType)) {
                return Collections.emptyList();
            }

            if (NON_RELATIONAL.equalsIgnoreCase(dbType)) {
                MongoTemplate mongoTemplate = dynamicMongoManager.getMongoTemplate(connectionName);
                List<StudentMongo> students = mongoTemplate.findAll(StudentMongo.class, "student");

                return students.stream()
                        .map(studentMapper::toResponse)
                        .toList();
            }

            DataSourceContextHolder.setDataSourceKey(connectionName);
            List<StudentJPA> students = studentRepository.findAll();

            return students.stream()
                    .map(studentMapper::toResponse)
                    .toList();

        } finally {
            DataSourceContextHolder.clear();
        }
    }

    public StudentResponse getStudentById(String id, HttpSession session) {
        String connectionName = (String) session.getAttribute(SESSION_CONNECTION_NAME);
        String dbType = (String) session.getAttribute(SESSION_DB_TYPE);

        try {
            if (!StringUtils.hasText(connectionName) || !StringUtils.hasText(dbType)) {
                return new StudentResponse();
            }

            if (NON_RELATIONAL.equalsIgnoreCase(dbType)) {
                MongoTemplate mongoTemplate = dynamicMongoManager.getMongoTemplate(connectionName);
                StudentMongo student = mongoTemplate.findById(id, StudentMongo.class, "student");
                return student != null ? studentMapper.toResponse(student) : new StudentResponse();
            }

            DataSourceContextHolder.setDataSourceKey(connectionName);
            Long studentId = Long.valueOf(id);

            return studentRepository.findById(studentId)
                    .map(studentMapper::toResponse)
                    .orElse(new StudentResponse());

        } catch (NumberFormatException e) {
            return new StudentResponse();

        } finally {
            DataSourceContextHolder.clear();
        }
    }

    private void saveStudentToMongo(StudentRequest request, String connectionName) {
        MongoTemplate mongoTemplate = dynamicMongoManager.getMongoTemplate(connectionName);
        StudentMongo studentMongo = studentMapper.toMongo(request);
        mongoTemplate.save(studentMongo, "student");
    }

    private void saveStudentToJpa(StudentRequest request, String connectionName) {
        DataSourceContextHolder.setDataSourceKey(connectionName);
        StudentJPA studentJPA = studentMapper.toJpa(request);
        studentRepository.save(studentJPA);
    }
}