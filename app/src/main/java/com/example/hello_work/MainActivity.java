package com.example.hello_work;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hello_work.infraestructure.adapter.IAttendanceTeacher;
import com.example.hello_work.infraestructure.adapter.imp.AttendanceTeacher;
import com.example.hello_work.infraestructure.repository.MiDbHelper;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.InputStream;
import java.sql.SQLOutput;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private IAttendanceTeacher attendanceTeacher;
    private SQLiteOpenHelper dbHelper;

    private final ActivityResultLauncher<String> selectFileLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                try(InputStream inputStream = getContentResolver().openInputStream(uri);
                    SQLiteDatabase db = dbHelper.getWritableDatabase()) {
                    HSSFWorkbook workbook = new HSSFWorkbook(inputStream);
                    Sheet sheet = workbook.getSheetAt(0);

                    Iterator<Row> rowIterator = sheet.iterator();
                    rowIterator.hasNext();
                    rowIterator.next();

                    while (rowIterator.hasNext()) {
                        attendanceTeacher.insertTeacher(rowIterator.next(), db);
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                    Toast.makeText(MainActivity.this, "Error al procesar el archivo", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new MiDbHelper(this);
        attendanceTeacher = new AttendanceTeacher();
    }

    public void openXls(View view){
        selectFileLauncher.launch("application/vnd.ms-excel");
    }

    public void obtainValue(View view){
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        LocalTime hourNow = LocalTime.now();
        LocalDate dateNow = LocalDate.now();
        String dayOfWeek = translateDayWeek(dateNow.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH));
        String[] projection = { "nombre_materia", dayOfWeek }; //columnas que deseas obtener
        String selection = "codigo_profesor = ? and "+dayOfWeek+" != ?"; //condición para obtener el usuario con id=1
        String[] selectionArgs = { "01501", "null" }; //valor para la condición
        Cursor cursor = db.query(
                "horario_carrera", //nombre de la tabla
                projection, //columnas que deseas obtener
                selection, //condición para la consulta
                selectionArgs, //valores para la condición
                null, //no agrupar las filas
                null, //no filtrar las filas
                null //ordenar las filas según la consulta
        );
        List<Map<String, String>> dataClass = new ArrayList<>();
        String nombre = "";
        Map<String, String> classTeacher;
        while (cursor.moveToNext()) {
            classTeacher = new HashMap<>();//mueve el cursor al primer registro, si existes
            classTeacher.put("nombre_materia", cursor.getString(cursor.getColumnIndexOrThrow("nombre_materia")));
            classTeacher.put(dayOfWeek, cursor.getString(cursor.getColumnIndexOrThrow(dayOfWeek)));
            dataClass.add(classTeacher);
            nombre = cursor.getString(cursor.getColumnIndexOrThrow(dayOfWeek)); //obtiene el valor de la columna "nombre"
        }
        Integer hour = hourNow.getHour();
        for (Map<String, String> arr : dataClass){
            String hourClass = arr.get(dayOfWeek);
            String[] schedule = hourClass.split(" ");
            Integer startTime = Integer.valueOf(schedule[0].substring(0,2));
            Integer endTime = Integer.valueOf(schedule[2].substring(0,2));
            if(hour >= startTime && hour < endTime ){
                System.out.println("esta en clase");
            }
        }


        cursor.close();
        db.close();
        System.out.println(nombre);
    }

    private String translateDayWeek(String nameDay){
        switch (nameDay){
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
                throw new RuntimeException("No es un dia laboral");
        }
    }

}