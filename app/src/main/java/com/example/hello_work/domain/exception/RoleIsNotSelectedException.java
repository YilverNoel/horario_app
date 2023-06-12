package com.example.hello_work.domain.exception;

public class RoleIsNotSelectedException extends RuntimeException{
    public RoleIsNotSelectedException(String msg){
        super(msg);
    }
}
