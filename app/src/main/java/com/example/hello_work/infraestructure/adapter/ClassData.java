package com.example.hello_work.infraestructure.adapter;

public class ClassData {
    private String nameTeacher;
    private String nameSubject;
    private String schedule;
    private String codeTeacher;


    public ClassData(String nameTeacher, String nameSubject, String schedule, String codeTeacher) {
        this.nameTeacher = nameTeacher;
        this.nameSubject = nameSubject;
        this.schedule = schedule;
        this.codeTeacher = codeTeacher;
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

    public String getCodeTeacher() {
        return codeTeacher;
    }

    public void setCodeTeacher(String codeTeacher) {
        this.codeTeacher = codeTeacher;
    }
}
