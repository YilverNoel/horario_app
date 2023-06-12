package com.example.hello_work.domain.exception;

public class SaveUserException extends RuntimeException{
    public SaveUserException(String msg){
        super(msg);
    }
}
