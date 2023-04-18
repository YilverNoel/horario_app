package com.example.hello_work;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hello_work.domain.exception.DayWeekNotWorkException;
import com.example.hello_work.infraestructure.repository.MiDbHelper;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Asistencia extends AppCompatActivity {
    private TextView nameTeacher;
    private TextView codeTeacher;

    private SQLiteOpenHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new MiDbHelper(this);
        setContentView(R.layout.activity_asistencia);
        nameTeacher = findViewById(R.id.txt_nombre);
        codeTeacher = findViewById(R.id.txt_codigo);

        nameTeacher.setText("Nombre: " + getIntent().getExtras().getString("nameTeacher"));
        codeTeacher.setText("Codigo:" + getIntent().getExtras().getString("codeTeacher"));
    }

    public void verifyAttendance(View view) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Boolean isClass = false;
        LocalTime hourNow = LocalTime.now();
        LocalDate dateNow = LocalDate.now();
        try{
            String dayOfWeek = translateDayWeek(dateNow.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH));
            String[] projection = {"nombre_materia", dayOfWeek}; //columnas que deseas obtener
            String selection = "codigo_profesor = ? and " + dayOfWeek + " != ?"; //condición para obtener el usuario con id=1
            String[] selectionArgs = {codeTeacher.getText().toString(), "null"}; //valor para la condición
            Cursor cursor = db.query(
                    "horario_carrera",
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null
            );
            List<Map<String, String>> dataClass = new ArrayList<>();
            String nameSubject = "";
            Map<String, String> classTeacher;
            while (cursor.moveToNext()) {
                classTeacher = new HashMap<>();//mueve el cursor al primer registro, si existes
                classTeacher.put("nombre_materia", cursor.getString(cursor.getColumnIndexOrThrow("nombre_materia")));
                classTeacher.put(dayOfWeek, cursor.getString(cursor.getColumnIndexOrThrow(dayOfWeek)));
                dataClass.add(classTeacher);
            }
            Integer hour = hourNow.getHour();
            for (Map<String, String> arr : dataClass) {
                String hourClass = arr.get(dayOfWeek);
                nameSubject = arr.get("nombre_materia");
                String[] schedule = hourClass.split(" ");
                Integer startTime = Integer.valueOf(schedule[0].substring(0, 2));
                Integer endTime = Integer.valueOf(schedule[2].substring(0, 2));
                if (hour >= startTime && hour < endTime) {
                    isClass = true;
                    break;
                }
            }

            if(isClass){
                Toast.makeText(this,"Se ha registrado la asistencia de la \n asignatura "+nameSubject, Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(this,"No asignaturas para registrar asistencia", Toast.LENGTH_LONG).show();
            }
            cursor.close();
        }catch (DayWeekNotWorkException dayWeekNotWorkException){
            Toast.makeText(this, dayWeekNotWorkException.getMessage(),Toast.LENGTH_LONG).show();
        }catch (Exception e){
            Toast.makeText(this, e.getMessage(),Toast.LENGTH_LONG).show();
        }
        finally {
            db.close();
        }

    }

    private String translateDayWeek(String nameDay) {
        switch (nameDay) {
            case "Monday":
                return "lunes";
            case "Tuesday":
                return "martes";
            case "Wednesday":
                return "miercoles";
            case "Thursday":
                return "jueves";
            case "Friday":
                return "viernes";
            case "Saturday":
                return "sabado";
            default:
                throw new DayWeekNotWorkException("No es un dia laboral");
        }
    }
}