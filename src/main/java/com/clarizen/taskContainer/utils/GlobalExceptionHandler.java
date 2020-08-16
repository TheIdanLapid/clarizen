package com.clarizen.taskContainer.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.management.BadAttributeValueExpException;
import javax.validation.ConstraintViolationException;

@ControllerAdvice
@RestController
@Validated
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {


    @ExceptionHandler({DataIntegrityViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public final ResponseEntity<Object> handleEntityNotFoundException(DataIntegrityViolationException ex, WebRequest request)
         {
        System.out.println(ex.getMessage());
        return new ResponseEntity<>(new ApiError(HttpStatus.BAD_REQUEST, "input violets database rules: " ), HttpStatus.BAD_REQUEST);

    }



    @ExceptionHandler({NullPointerException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public final ResponseEntity<Object> handleNullPointerException(NullPointerException ex, WebRequest request) {
        return new ResponseEntity<>(new ApiError(HttpStatus.NOT_FOUND, "requested data was not found in database: " + ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseEntity<String> IllegalArgumentException(IllegalArgumentException e) {
        return new ResponseEntity<>("Illegal input: " + e.getMessage(), HttpStatus.BAD_REQUEST);
}

}
