package com.example.hello_work.domain.weekdays;

public enum Weekdays {
    MONDAY("lunes"),
    TUESDAY("martes"),
    WEDNESDAY("miercoles"),
    THURSDAY("jueves"),
    FRIDAY("viernes"),
    SATURDAY("sabado");
     private String nameDayWeek;
     Weekdays(String nameDayWeek){
         this.nameDayWeek = nameDayWeek;
     }

    public String getNameDayWeek() {
        return nameDayWeek;
    }
}
