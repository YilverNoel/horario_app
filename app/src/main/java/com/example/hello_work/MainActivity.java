package com.example.hello_work;

import static com.example.hello_work.constan.Constant.COLLECTION_USER;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hello_work.infraestructure.repository.ConnectionFirebase;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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
        List<String> nameRole = new ArrayList<>();
        ConnectionFirebase.connection().collection(COLLECTION_USER)
                .whereEqualTo("name_user", code.getText().toString())
                .whereEqualTo("password", encryptPassword(code.getText().toString()))
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        nameRole.add(documentSnapshot.get("role").toString());
                    }
                    validateNameTeacher(nameRole);
                }).addOnFailureListener(
                        e -> System.out.println(e.getMessage())
                );
    }

    private void validateNameTeacher(List<String> nameTeacher) {
        if (!nameTeacher.isEmpty() && !code.getText().toString().isEmpty()) {
            Intent intent = new Intent(this, Asistencia.class);
            intent.putExtra("role", nameTeacher.get(0));
            startActivity(intent);
        } else {
            Toast.makeText(this, "El codigo ingresado no existe", Toast.LENGTH_LONG).show();
        }
    }
    private String encryptPassword(String pass){
        StringBuilder passEncoding = new StringBuilder();
        for(byte encoding: pass.getBytes(StandardCharsets.UTF_8)){
            passEncoding.append(encoding);
        }
        return passEncoding.toString();
    }

}