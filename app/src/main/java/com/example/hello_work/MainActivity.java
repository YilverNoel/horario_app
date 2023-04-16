package com.example.hello_work;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hello_work.infraestructure.adapter.IAttendanceTeacher;
import com.example.hello_work.infraestructure.adapter.imp.AttendanceTeacher;
import com.example.hello_work.infraestructure.repository.MiDbHelper;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.InputStream;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {
    private IAttendanceTeacher attendanceTeacher;
    private SQLiteOpenHelper dbHelper;
    private EditText code;

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
        code = findViewById(R.id.code);
    }

    public void openXls(View view){
        selectFileLauncher.launch("application/vnd.ms-excel");
    }

    public void singIn(View view){
        String nameTeacher;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {"nombre_profesor" };
        String selection = "codigo_profesor = ?";
        String[] selectionArgs = { code.getText().toString()};
        Cursor cursor = db.query(
                "horario_carrera",
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        if (cursor.moveToFirst() && !code.getText().toString().isEmpty()) {
            nameTeacher =  cursor.getString(cursor.getColumnIndexOrThrow("nombre_profesor"));
            Intent intent = new Intent(this, Asistencia.class);
            intent.putExtra("nameTeacher", nameTeacher);
            intent.putExtra("codeTeacher", code.getText().toString());
            startActivity(intent);
        }else{
            Toast.makeText(this, "El codigo ingresado no existe", Toast.LENGTH_LONG).show();
        }

    }


}