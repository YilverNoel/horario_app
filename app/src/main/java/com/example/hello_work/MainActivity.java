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
    private EditText code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(MainActivity.this);
        code = findViewById(R.id.code);
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