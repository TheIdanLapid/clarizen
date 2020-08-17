package com.clarizen.taskContainer.exceptions;

//custom exception created for app
public class DataNotFoundInDBException extends Exception {
    public DataNotFoundInDBException(String errorMsg){
        super(errorMsg);
    }

}