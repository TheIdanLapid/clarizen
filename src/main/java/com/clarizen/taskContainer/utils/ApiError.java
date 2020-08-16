package com.clarizen.taskContainer.utils;

import org.springframework.http.HttpStatus;

import java.util.List;

public class ApiError {

    private final String message;
    private final HttpStatus httpStatus;


    public ApiError(HttpStatus httpStatus,String message) {
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

}
