package com.example.hello_work.infraestructure.repository;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class MiDbHelper extends SQLiteOpenHelper {
    private static final int VERSION_BASE_DATA = 2;
    private static final String NAME_BASE_DATA = "university.db";

    public MiDbHelper(@Nullable Context context) {
        super(context, NAME_BASE_DATA, null, VERSION_BASE_DATA);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE horario_carrera (" +
                "id TEXT PRIMARY KEY, nombre_materia TEXT," +
                "sem_mat REAL, lunes TEXT, " +
                "martes TEXT, miercoles TEXT," +
                "jueves TEXT, viernes TEXT," +
                "sabado TEXT, codigo_profesor TEXT," +
                "nombre_profesor TEXT," +
                "cant_matriculados REAL" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS horario_carrera");

        onCreate(db);
    }
}
