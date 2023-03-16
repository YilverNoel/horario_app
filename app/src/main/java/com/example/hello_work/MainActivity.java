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
import java.util.Iterator;

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

        String[] projection = { "nombre_profesor" }; //columnas que deseas obtener
        String selection = "id = ?"; //condición para obtener el usuario con id=1
        String[] selectionArgs = { "192901A" }; //valor para la condición
        Cursor cursor = db.query(
                "horario_carrera", //nombre de la tabla
                projection, //columnas que deseas obtener
                selection, //condición para la consulta
                selectionArgs, //valores para la condición
                null, //no agrupar las filas
                null, //no filtrar las filas
                null //ordenar las filas según la consulta
        );

        String nombre = "";
        if (cursor.moveToFirst()) { //mueve el cursor al primer registro, si existe
            nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre_profesor")); //obtiene el valor de la columna "nombre"
        }
        cursor.close();
        db.close();
        System.out.println(nombre);
    }

}