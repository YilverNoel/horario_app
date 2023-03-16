package com.example.hello_work.infraestructure.adapter.imp;

import static com.example.hello_work.domain.subject.SubjectTeacher.CODE_TEACHER;
import static com.example.hello_work.domain.subject.SubjectTeacher.ID;
import static com.example.hello_work.domain.subject.SubjectTeacher.NAME_SUBJECT;
import static com.example.hello_work.domain.subject.SubjectTeacher.NAME_TEACHER;
import static com.example.hello_work.domain.subject.SubjectTeacher.NUMBER_ENROLLED;
import static com.example.hello_work.domain.subject.SubjectTeacher.SEMESTER_SUBJECT;
import static com.example.hello_work.domain.weekdays.Weekdays.FRIDAY;
import static com.example.hello_work.domain.weekdays.Weekdays.MONDAY;
import static com.example.hello_work.domain.weekdays.Weekdays.SATURDAY;
import static com.example.hello_work.domain.weekdays.Weekdays.THURSDAY;
import static com.example.hello_work.domain.weekdays.Weekdays.TUESDAY;
import static com.example.hello_work.domain.weekdays.Weekdays.WEDNESDAY;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.example.hello_work.infraestructure.adapter.IAttendanceTeacher;

import org.apache.poi.ss.usermodel.Row;

public class AttendanceTeacher implements IAttendanceTeacher {
    private final static String RACE_SCHEDULE = "horario_carrera";

    @Override
    public void insertTeacher(Row row, SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        String[] propertiesTeacher = separateString(String.valueOf(row.getCell(12)));
        values.put(ID.getNameSubjectTeacher(), String.valueOf(row.getCell(3)));
        values.put(NAME_SUBJECT.getNameSubjectTeacher(), String.valueOf(row.getCell(4)));
        values.put(SEMESTER_SUBJECT.getNameSubjectTeacher(), Double.valueOf(String.valueOf(row.getCell(5))));
        values.put(MONDAY.getNameDayWeek(), String.valueOf(row.getCell(6)));
        values.put(TUESDAY.getNameDayWeek(), String.valueOf(row.getCell(7)));
        values.put(WEDNESDAY.getNameDayWeek(), String.valueOf(row.getCell(8)));
        values.put(THURSDAY.getNameDayWeek(), String.valueOf(row.getCell(9)));
        values.put(FRIDAY.getNameDayWeek(), String.valueOf(row.getCell(10)));
        values.put(SATURDAY.getNameDayWeek(), String.valueOf(row.getCell(11)));
        values.put(CODE_TEACHER.getNameSubjectTeacher(), propertiesTeacher[0].trim());
        values.put(NAME_TEACHER.getNameSubjectTeacher(), propertiesTeacher[1].trim());
        values.put(NUMBER_ENROLLED.getNameSubjectTeacher(), Double.valueOf(String.valueOf(row.getCell(13))));
        db.insert(RACE_SCHEDULE,null, values);
    }

    private String[] separateString(String row){
        return row.split("-");
    }
}
