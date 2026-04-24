package com.project.mdbm.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "employee")
public class EmployeeMongo {

    @Id
    private String id;

    private String firstName;

    private String lastName;

    @Indexed(unique = true)
    private String email;

    private String phone;

    private String department;

    private String designation;
}