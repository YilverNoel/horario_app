package com.example.hello_work;

import static com.example.hello_work.constan.Constant.COLLECTION_USER;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hello_work.infraestructure.repository.ConnectionFirebase;
import com.example.hello_work.utils.EncryptPassword;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private EditText code, password;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(MainActivity.this);
        code = findViewById(R.id.code);
        password = findViewById(R.id.password);
        progressDialog = new ProgressDialog(this, R.style.CustomProgressDialog);
    }

    public void singIn(View view) {
        if (!code.getText().toString().isEmpty() && !password.getText().toString().isEmpty()) {
            progressDialog.setProgressStyle(ProgressDialog.BUTTON_NEUTRAL);
            progressDialog.show();
        }

        List<String> nameRole = new ArrayList<>();
        ConnectionFirebase.connection().collection(COLLECTION_USER)
                .whereEqualTo("name_user", code.getText().toString())
                .whereEqualTo("password", EncryptPassword.encryptPassword(password.getText().toString()))
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        nameRole.add(documentSnapshot.get("role").toString());
                    }
                    validateNameTeacher(nameRole);
                }).addOnFailureListener(
                        e -> {
                            System.out.println(e.getMessage());
                            progressDialog.dismiss();
                        }
                );
    }

    private void validateNameTeacher(List<String> nameTeacher) {
        progressDialog.dismiss();
        if (!nameTeacher.isEmpty() && !code.getText().toString().isEmpty()) {
            Intent intent = new Intent(this, Asistencia.class);
            intent.putExtra("role", nameTeacher.get(0));
            startActivity(intent);
        } else {
            Toast.makeText(this, "El usuario ingresado no existe", Toast.LENGTH_LONG).show();
        }
    }

}