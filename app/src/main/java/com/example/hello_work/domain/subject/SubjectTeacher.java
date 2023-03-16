package com.example.hello_work.domain.subject;

public enum SubjectTeacher {
    ID("id"),
    NAME_SUBJECT("nombre_materia"),
    SEMESTER_SUBJECT("sem_mat"),
    CODE_TEACHER("codigo_profesor"),
    NAME_TEACHER("nombre_profesor"),
    NUMBER_ENROLLED("cant_matriculados");

    private String nameSubjectTeacher;

    SubjectTeacher(String nameSubjectTeacher){
        this.nameSubjectTeacher = nameSubjectTeacher;
    }

    public String getNameSubjectTeacher() {
        return nameSubjectTeacher;
    }
}
