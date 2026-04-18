package com.project.mdbm.dto;

import lombok.Data;

@Data
public class GenericAPIResponse {

    private int code;
    private String message;
    private String token;

}
