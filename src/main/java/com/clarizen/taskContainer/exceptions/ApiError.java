package com.clarizen.taskContainer.exceptions;

import org.springframework.http.HttpStatus;

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
