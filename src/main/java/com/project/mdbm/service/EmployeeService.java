package com.project.mdbm.service;

import com.project.mdbm.config.DataSourceContextHolder;
import com.project.mdbm.config.DynamicMongoManager;
import com.project.mdbm.dto.EmployeeRequest;
import com.project.mdbm.dto.EmployeeResponse;
import com.project.mdbm.dto.GenericAPIResponse;
import com.project.mdbm.entity.EmployeeJPA;
import com.project.mdbm.entity.EmployeeMongo;
import com.project.mdbm.mapper.EmployeeMapper;
import com.project.mdbm.repository.EmployeeRepository;
import com.project.mdbm.utils.ResponseUtils;
import jakarta.servlet.http.HttpSession;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    private static final String SESSION_CONNECTION_NAME = "connectionName";
    private static final String SESSION_DB_TYPE = "dbType";
    private static final String NON_RELATIONAL = "Non-Relational";

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;
    private final DynamicMongoManager dynamicMongoManager;

    public EmployeeService(EmployeeRepository employeeRepository,
                           EmployeeMapper employeeMapper,
                           DynamicMongoManager dynamicMongoManager) {
        this.employeeRepository = employeeRepository;
        this.employeeMapper = employeeMapper;
        this.dynamicMongoManager = dynamicMongoManager;
    }

    public GenericAPIResponse saveEmployee(EmployeeRequest request, HttpSession session) {
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
                saveEmployeeToMongo(request, connectionName);
            } else {
                saveEmployeeToJpa(request, connectionName);
            }

            response.setCode(200);
            response.setMessage("Employee saved successfully in " + connectionName);
            return response;

        } catch (DataIntegrityViolationException e) {
            response.setCode(409);
            response.setMessage("Employee with this email already exists");
            return response;

        } catch (Exception e) {
            response.setCode(500);
            response.setMessage("Failed to save employee: " + e.getMessage());
            return response;

        } finally {
            DataSourceContextHolder.clear();
        }
    }

    public GenericAPIResponse updateEmployee(String id, EmployeeRequest request, HttpSession session) {
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
                EmployeeMongo existingEmployee = mongoTemplate.findById(id, EmployeeMongo.class, "employee");

                if (existingEmployee == null) {
                    response.setCode(404);
                    response.setMessage("Employee not found with id: " + id);
                    return response;
                }

                existingEmployee.setFirstName(request.getFirstName());
                existingEmployee.setLastName(request.getLastName());
                existingEmployee.setEmail(request.getEmail());
                existingEmployee.setPhone(request.getPhone());
                existingEmployee.setDepartment(request.getDepartment());
                existingEmployee.setDesignation(request.getDesignation());

                mongoTemplate.save(existingEmployee, "employee");

            } else {
                DataSourceContextHolder.setDataSourceKey(connectionName);

                Long employeeId = Long.valueOf(id);
                Optional<EmployeeJPA> optionalEmployee = employeeRepository.findById(employeeId);

                if (optionalEmployee.isEmpty()) {
                    response.setCode(404);
                    response.setMessage("Employee not found with id: " + id);
                    return response;
                }

                EmployeeJPA existingEmployee = optionalEmployee.get();
                existingEmployee.setFirstName(request.getFirstName());
                existingEmployee.setLastName(request.getLastName());
                existingEmployee.setEmail(request.getEmail());
                existingEmployee.setPhone(request.getPhone());
                existingEmployee.setDepartment(request.getDepartment());
                existingEmployee.setDesignation(request.getDesignation());

                employeeRepository.save(existingEmployee);
            }

            response.setCode(200);
            response.setMessage("Employee updated successfully in " + connectionName);
            return response;

        } catch (NumberFormatException e) {
            response.setCode(400);
            response.setMessage("Invalid employee id: " + id);
            return response;

        } catch (DataIntegrityViolationException e) {
            response.setCode(409);
            response.setMessage("Employee with this email already exists");
            return response;

        } catch (Exception e) {
            response.setCode(500);
            response.setMessage("Failed to update employee: " + e.getMessage());
            return response;

        } finally {
            DataSourceContextHolder.clear();
        }
    }

    public GenericAPIResponse deleteEmployee(String id, HttpSession session) {
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
                EmployeeMongo existingEmployee = mongoTemplate.findById(id, EmployeeMongo.class, "employee");

                if (existingEmployee == null) {
                    response.setCode(404);
                    response.setMessage("Employee not found with id: " + id);
                    return response;
                }

                mongoTemplate.remove(existingEmployee, "employee");
            } else {
                DataSourceContextHolder.setDataSourceKey(connectionName);

                Long employeeId = Long.valueOf(id);
                if (!employeeRepository.existsById(employeeId)) {
                    response.setCode(404);
                    response.setMessage("Employee not found with id: " + id);
                    return response;
                }

                employeeRepository.deleteById(employeeId);
            }

            response.setCode(200);
            response.setMessage("Employee deleted successfully from " + connectionName);
            return response;

        } catch (NumberFormatException e) {
            response.setCode(400);
            response.setMessage("Invalid employee id: " + id);
            return response;

        } catch (Exception e) {
            response.setCode(500);
            response.setMessage("Failed to delete employee: " + e.getMessage());
            return response;

        } finally {
            DataSourceContextHolder.clear();
        }
    }

    public List<EmployeeResponse> getAllEmployees(HttpSession session) {
        String connectionName = (String) session.getAttribute(SESSION_CONNECTION_NAME);
        String dbType = (String) session.getAttribute(SESSION_DB_TYPE);

        try {
            if (!StringUtils.hasText(connectionName) || !StringUtils.hasText(dbType)) {
                return Collections.emptyList();
            }

            if (NON_RELATIONAL.equalsIgnoreCase(dbType)) {
                MongoTemplate mongoTemplate = dynamicMongoManager.getMongoTemplate(connectionName);
                List<EmployeeMongo> employees = mongoTemplate.findAll(EmployeeMongo.class, "employee");

                return employees.stream()
                        .map(employeeMapper::toResponse)
                        .toList();
            }

            DataSourceContextHolder.setDataSourceKey(connectionName);
            List<EmployeeJPA> employees = employeeRepository.findAll();

            return employees.stream()
                    .map(employeeMapper::toResponse)
                    .toList();

        } finally {
            DataSourceContextHolder.clear();
        }
    }

    public EmployeeResponse getEmployeeById(String id, HttpSession session) {
        String connectionName = (String) session.getAttribute(SESSION_CONNECTION_NAME);
        String dbType = (String) session.getAttribute(SESSION_DB_TYPE);

        try {
            if (!StringUtils.hasText(connectionName) || !StringUtils.hasText(dbType)) {
                return new EmployeeResponse();
            }

            if (NON_RELATIONAL.equalsIgnoreCase(dbType)) {
                MongoTemplate mongoTemplate = dynamicMongoManager.getMongoTemplate(connectionName);
                EmployeeMongo employee = mongoTemplate.findById(id, EmployeeMongo.class, "employee");
                return employee != null ? employeeMapper.toResponse(employee) : new EmployeeResponse();
            }

            DataSourceContextHolder.setDataSourceKey(connectionName);
            Long employeeId = Long.valueOf(id);

            return employeeRepository.findById(employeeId)
                    .map(employeeMapper::toResponse)
                    .orElse(new EmployeeResponse());

        } catch (NumberFormatException e) {
            return new EmployeeResponse();

        } finally {
            DataSourceContextHolder.clear();
        }
    }

    private void saveEmployeeToMongo(EmployeeRequest request, String connectionName) {
        MongoTemplate mongoTemplate = dynamicMongoManager.getMongoTemplate(connectionName);
        EmployeeMongo employeeMongo = employeeMapper.toMongo(request);
        mongoTemplate.save(employeeMongo, "employee");
    }

    private void saveEmployeeToJpa(EmployeeRequest request, String connectionName) {
        DataSourceContextHolder.setDataSourceKey(connectionName);
        EmployeeJPA employeeJPA = employeeMapper.toJpa(request);
        employeeRepository.save(employeeJPA);
    }
}