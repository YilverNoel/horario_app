package com.example.hello_work;

import static com.example.hello_work.constan.Constant.COLLECTION_USER;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hello_work.domain.exception.PasswordIsEmptyException;
import com.example.hello_work.domain.exception.PasswordNotEqualsException;
import com.example.hello_work.domain.exception.RoleIsNotSelectedException;
import com.example.hello_work.domain.exception.SaveUserException;
import com.example.hello_work.infraestructure.repository.ConnectionFirebase;
import com.example.hello_work.utils.EncryptPassword;

import java.util.HashMap;
import java.util.Map;

public class AddUser extends AppCompatActivity {
    private Spinner spinner;
    private String nameRoleSelected;
    private EditText regUser;
    private EditText regPassword;
    private EditText regConfirmPassword;
    private static final String ROL_DEFECTO = "-Seleccione un rol-";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);
        spinner = findViewById(R.id.regRole);
        regUser = findViewById(R.id.regUser);
        regPassword = findViewById(R.id.regPassword);
        regConfirmPassword = findViewById(R.id.regConPassword);

        setSpinner();

    }

    private void setSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spinner_items, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);
    }


    public void registerUser(View view) {
        try{
            valueSelectedSpinner();
            validatePassword();
            validateRole();
            validatePasswordEmpty();
            Map<String, String> values = new HashMap<>();
            values.put("name_user", regUser.getText().toString());
            values.put("password", EncryptPassword.encryptPassword(regPassword.getText().toString()));
            values.put("role", nameRoleSelected);
            existUser(values);
        }catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    private void existUser(Map<String, String> values){
        ConnectionFirebase.connection()
                .collection(COLLECTION_USER)
                .whereEqualTo("name_user", regUser.getText().toString())
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        persistenceUser(values);
                    }else{
                        Toast.makeText(this, "usuario existente", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void persistenceUser(Map<String, String> values){
        ConnectionFirebase.connection()
                .collection(COLLECTION_USER)
                .add(values)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Registro agregado", Toast.LENGTH_LONG).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
    private void validatePassword(){
        if(!regPassword.getText().toString().equals(
                regConfirmPassword.getText().toString())){
            throw new PasswordNotEqualsException("la contraseña de confirmacion \n" +
                    "no es igual");
        }
    }
    private void validateRole(){
        if(nameRoleSelected.equals(ROL_DEFECTO)){
            throw new RoleIsNotSelectedException("No selecciono Rol");
        }
    }
    private void validatePasswordEmpty(){
        if(regPassword.getText().toString().isEmpty()){
            throw new PasswordIsEmptyException("la contraseña no puede estar\n vacia");
        }
    }
    public void valueSelectedSpinner() {
        nameRoleSelected = spinner.getSelectedItem().toString();
    }

    @Override
    public void onBackPressed(){
        //do nothing
    }
    public void back(View view){
        Intent intent = new Intent(this, Asistencia.class);
        intent.putExtra("role", "admin");
        startActivity(intent);
        finish();
    }
}