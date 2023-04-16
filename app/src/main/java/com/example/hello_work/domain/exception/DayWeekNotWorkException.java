package com.example.hello_work.domain.exception;

public class DayWeekNotWorkException extends RuntimeException{
    public DayWeekNotWorkException(String msg){
        super(msg);
    }
}
