package com.example.hello_work.infraestructure.adapter;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.firebase.firestore.FirebaseFirestore;

import org.apache.poi.ss.usermodel.Row;

public interface IAttendanceTeacher {

    void insertTeacher(Row row, FirebaseFirestore db);
}
