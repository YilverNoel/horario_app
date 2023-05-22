package com.example.hello_work;

import static com.example.hello_work.constan.Constant.COLLECTION_RACE_SCHEDULE;
import static com.example.hello_work.constan.Constant.COLLECTION_USER;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hello_work.infraestructure.adapter.IAttendanceTeacher;
import com.example.hello_work.infraestructure.adapter.imp.AttendanceTeacher;
import com.example.hello_work.infraestructure.repository.ConnectionFirebase;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private IAttendanceTeacher attendanceTeacher;
    private EditText code;

    private final ActivityResultLauncher<String> selectFileLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                try (InputStream inputStream = getContentResolver().openInputStream(uri)) {
                    HSSFWorkbook workbook = new HSSFWorkbook(inputStream);
                    Sheet sheet = workbook.getSheetAt(0);

                    Iterator<Row> rowIterator = sheet.iterator();
                    rowIterator.hasNext();
                    rowIterator.next();

                    while (rowIterator.hasNext()) {
                        attendanceTeacher.insertTeacher(rowIterator.next(), ConnectionFirebase.connection());
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
        FirebaseApp.initializeApp(MainActivity.this);
        attendanceTeacher = new AttendanceTeacher();
        code = findViewById(R.id.code);
    }

    public void openXls(View view) {
        ConnectionFirebase.connection()
                .collection(COLLECTION_RACE_SCHEDULE)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Confirmación");
                    builder.setMessage("¿Estás seguro de que quieres cargar de nuevo los datos?");
                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                deleteDataAttendance();
                                selectFileLauncher.launch("application/vnd.ms-excel");
                            }else{
                                selectFileLauncher.launch("application/vnd.ms-excel");
                            }
                        }
                    });
                    builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // El usuario hizo clic en Cancelar, no hacer nada
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                })
                .addOnFailureListener(e -> {
                    // Ocurrió un error al obtener los datos
                    System.out.println(e.getMessage());
                });
    }

    private void deleteDataAttendance() {
        ConnectionFirebase.connection()
                .collection(COLLECTION_RACE_SCHEDULE)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                        documentSnapshot.getReference().delete();
                    }
                })
                .addOnFailureListener(e ->
                        System.out.println(e.getMessage())
                );
    }


    public void singIn(View view) {
        List<String> nameTeacher = new ArrayList<>();
        ConnectionFirebase.connection().collection(COLLECTION_USER)
                .whereEqualTo("name_user", code.getText().toString())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        nameTeacher.add(documentSnapshot.get("name_user").toString());
                    }
                    validateNameTeacher(nameTeacher);
                }).addOnFailureListener(
                        e -> System.out.println(e.getMessage())
                );
    }

    private void validateNameTeacher(List<String> nameTeacher) {
        if (!nameTeacher.isEmpty() && !code.getText().toString().isEmpty()) {
            Intent intent = new Intent(this, Asistencia.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "El codigo ingresado no existe", Toast.LENGTH_LONG).show();
        }
    }
}