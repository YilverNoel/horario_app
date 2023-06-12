package com.example.hello_work.domain.exception;

public class PasswordNotEqualsException extends RuntimeException{
    public PasswordNotEqualsException(String msg){
        super(msg);
    }
}
