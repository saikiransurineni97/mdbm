package com.project.mdbm.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "db_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DBDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "connection_name")
    @NotBlank(message = "Connection name is mandatory")
    private String connectionName;

    @Column(name = "db_type")
    @NotBlank(message = "Database type is mandatory")
    private String dbType;

    @Column(name = "url")
    @NotBlank(message = "URL is mandatory")
    private String url;

    @Column(name = "user_name")
    @NotBlank(message = "Username is mandatory")
    private String userName;

    @Column(name = "password")
    @NotBlank(message = "Password is mandatory")
    private String password;

}