package com.example.hello_work.infraestructure.adapter;

public class ClassData {
    private String nameTeacher;
    private String nameSubject;
    private String schedule;

    public ClassData(String nameTeacher, String nameSubject, String schedule) {
        this.nameTeacher = nameTeacher;
        this.nameSubject = nameSubject;
        this.schedule = schedule;
    }

    public String getNameTeacher() {
        return nameTeacher;
    }

    public void setNameTeacher(String nameTeacher) {
        this.nameTeacher = nameTeacher;
    }

    public String getNameSubject() {
        return nameSubject;
    }

    public void setNameSubject(String nameSubject) {
        this.nameSubject = nameSubject;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }
}
