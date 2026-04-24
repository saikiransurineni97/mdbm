package com.project.mdbm.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeRequest {

    @NotBlank(message = "First name is mandatory")
    private String firstName;

    @NotBlank(message = "Last name is mandatory")
    private String lastName;

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone is mandatory")
    private String phone;

    @NotBlank(message = "Department is mandatory")
    private String department;

    @NotBlank(message = "Designation is mandatory")
    private String designation;

}