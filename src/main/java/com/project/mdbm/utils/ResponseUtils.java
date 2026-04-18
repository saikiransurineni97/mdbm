package com.project.mdbm.utils;

import com.project.mdbm.dto.GenericAPIResponse;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ResponseUtils {

    public static GenericAPIResponse getResponseObject() {
        return new GenericAPIResponse();
    }
}
