package com.example.hello_work;

import static com.example.hello_work.constan.Constant.COLLECTION_ATTENDANCE;
import static com.example.hello_work.constan.Constant.COLLECTION_RACE_SCHEDULE;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hello_work.domain.exception.DayWeekNotWorkException;
import com.example.hello_work.infraestructure.adapter.ClassData;
import com.example.hello_work.infraestructure.adapter.IAttendanceTeacher;
import com.example.hello_work.infraestructure.adapter.ListAdapter.DataListAdapter;
import com.example.hello_work.infraestructure.adapter.imp.AttendanceTeacher;
import com.example.hello_work.infraestructure.adapter.listener.Listener;
import com.example.hello_work.infraestructure.repository.ConnectionFirebase;
import com.google.firebase.firestore.DocumentSnapshot;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Asistencia extends AppCompatActivity implements Listener {

    private RecyclerView recyclerView;
    private IAttendanceTeacher attendanceTeacher;
    private ImageView buttonAddAttendance;
    private ImageView buttonAddUser;
    private String roleLogin;
    private final static String ROLE_ADMIN = "admin";
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asistencia);
        buttonAddAttendance = findViewById(R.id.apenAtt);
        buttonAddUser = findViewById(R.id.addUser);
        roleLogin = getIntent().getExtras().getString("role");
        progressDialog = new ProgressDialog(this, R.style.CustomProgressDialog);

        hideButtonsIsNotAdmin();

        attendanceTeacher = new AttendanceTeacher();
        recyclerView = findViewById(R.id.recycler);
        try {
            listHourTeacherDayWeek();
        } catch (DayWeekNotWorkException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }


    }

    private void hideButtonsIsNotAdmin() {
        if (!roleLogin.equals(ROLE_ADMIN)) {
            buttonAddAttendance.setVisibility(View.INVISIBLE);
            buttonAddUser.setVisibility(View.INVISIBLE);
        }
    }


    private void listHourTeacherDayWeek() {
        LocalTime hourNow = LocalTime.now();
        LocalDate dateNow = LocalDate.now();
        String dayOfWeek = translateDayWeek(dateNow.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH));
        ConnectionFirebase.connection()
                .collection(COLLECTION_RACE_SCHEDULE)
                .whereNotEqualTo(dayOfWeek, "null")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Map<String, String>> dataClass = new ArrayList<>();
                    Map<String, String> classTeacher;
                    List<ClassData> itemList = new ArrayList<>();
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        classTeacher = new HashMap<>();//mueve el cursor al primer registro, si existes
                        classTeacher.put("nombre_materia", documentSnapshot.get("nombre_materia").toString());
                        classTeacher.put(dayOfWeek, documentSnapshot.get(dayOfWeek).toString());
                        classTeacher.put("nombre_profesor", documentSnapshot.get("nombre_profesor").toString());
                        classTeacher.put("codigo_profesor", documentSnapshot.get("codigo_profesor").toString());
                        dataClass.add(classTeacher);
                    }
                    Integer endTime = 0;
                    String[] hourClass;
                    for (Map<String, String> listClass : dataClass) {
                        hourClass = obtainHourClass(listClass.get(dayOfWeek));
                        int j = 2;
                        for (int i = 0; i < hourClass.length / 4; i++) {
                            endTime = Integer.valueOf(hourClass[j].substring(0, 2));
                            j += 4;
                            if (hourNow.getHour() <= endTime) {
                                break;
                            }
                        }
                        if (hourNow.getHour() < endTime) {
                            itemList.add(new ClassData(
                                    listClass.get("nombre_profesor"), listClass.get("nombre_materia"),
                                    listClass.get(dayOfWeek), listClass.get("codigo_profesor")
                            ));
                        }

                    }
                    DataListAdapter adapter = new DataListAdapter(itemList, this);
                    recyclerView.setLayoutManager(new LinearLayoutManager(this));
                    recyclerView.setAdapter(adapter);
                }).addOnFailureListener(e ->
                        System.out.println(e.getMessage())
                );
    }

    private String[] obtainHourClass(String rangeClass) {
        String[] hourClass = rangeClass
                .replace("   ", " ")
                .split(",");

        String[] hoursClass = new String[hourClass.length * 4];
        int i = 0;
        for (String hour : hourClass) {
            for (String hourCheck : hour.trim().split(" ")) {
                hoursClass[i] = hourCheck;
                i++;
            }
        }

        return hoursClass;
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


    @Override
    public void callback(ClassData item, Boolean isPresent) {
        Map<String, Object> values = new HashMap<>();
        values.put("fecha", LocalDate.now().toString());
        values.put("codigo_profesor", item.getCodeTeacher());
        values.put("horario", item.getSchedule());
        values.put("asistencia", isPresent);
        existAttendance(values);
    }

    private void saveAttendance(Map<String, Object> values) {

        ConnectionFirebase.connection()
                .collection(COLLECTION_ATTENDANCE)
                .add(values)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Registro agregado", Toast.LENGTH_LONG).show();
                })
                .addOnFailureListener(e -> {
                    System.out.println("error -> " + e.getMessage());
                });
    }

    private void existAttendance(Map<String, Object> values) {
        ConnectionFirebase.connection()
                .collection(COLLECTION_ATTENDANCE)
                .whereEqualTo("codigo_profesor", values.get("codigo_profesor"))
                .whereEqualTo("fecha", values.get("fecha"))
                .whereEqualTo("horario", values.get("horario"))
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        saveAttendance(values);
                    } else {
                        Toast.makeText(this, "Registro existente", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(e ->
                        System.out.println(e.getMessage())
                );
    }

    public void openXls(View view) {
        progressDialog.setProgressStyle(ProgressDialog.BUTTON_NEUTRAL);
        progressDialog.show();
        progressDialog.setCancelable(false);
        ConnectionFirebase.connection()
                .collection(COLLECTION_RACE_SCHEDULE)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Confirmación");
                    builder.setMessage("¿Estás seguro de que quieres cargar de nuevo los datos?");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                deleteDataAttendance();
                            }
                            selectFileLauncher.launch("application/vnd.ms-excel");
                            progressDialog.dismiss();
                        }
                    });
                    builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            progressDialog.dismiss();
                            // El usuario hizo clic en Cancelar, no hacer nada
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                })
                .addOnFailureListener(e -> {
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
                    Toast.makeText(Asistencia.this, "Se han cargado \nlos datos", Toast.LENGTH_LONG);
                } catch (Exception e) {
                    e.printStackTrace();

                    Toast.makeText(Asistencia.this, "Error al procesar el archivo", Toast.LENGTH_SHORT).show();
                }
            });


    public void addUser(View view) {
        Intent intent = new Intent(this, AddUser.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed(){
        //do nothing
    }

    public void logout(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}