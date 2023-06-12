package com.example.hello_work.domain.exception;

public class PasswordIsEmptyException extends RuntimeException{
    public PasswordIsEmptyException(String msg){
        super(msg);
    }
}
