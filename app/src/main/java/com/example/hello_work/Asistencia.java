package com.example.hello_work;

import static com.example.hello_work.constan.Constant.COLLECTION_RACE_SCHEDULE;

import android.app.Dialog;
import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hello_work.domain.exception.DayWeekNotWorkException;
import com.example.hello_work.infraestructure.adapter.ClassData;
import com.example.hello_work.infraestructure.adapter.ListAdapter.DataListAdapter;
import com.example.hello_work.infraestructure.adapter.listener.Listener;
import com.example.hello_work.infraestructure.repository.ConnectionFirebase;
import com.example.hello_work.infraestructure.repository.MiDbHelper;
import com.google.firebase.firestore.DocumentSnapshot;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Asistencia extends AppCompatActivity implements Listener {
    private TextView nameTeacher;
    private TextView codeTeacher;

    private SQLiteOpenHelper dbHelper;

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asistencia);
        //nameTeacher = findViewById(R.id.txt_nombre);
       // codeTeacher = findViewById(R.id.txt_codigo);

        recyclerView = findViewById(R.id.recycler);
        List<ClassData> itemList = new ArrayList<>();
        itemList.add(new ClassData("Eduard Bayona Ibañez", "Introduccion a la ingenieria", "10:00 - 12:00 i205"));
        itemList.add(new ClassData("Freddy Alonso Alvarez", "Fisica mecanica", "08:00 - 10:00 i106"));
        DataListAdapter adapter = new DataListAdapter(itemList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);


    }

    public void verifyAttendance(View view) {
        LocalTime hourNow = LocalTime.now();
        LocalDate dateNow = LocalDate.now();
        String dayOfWeek = translateDayWeek(dateNow.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH));
        ConnectionFirebase.connection().collection(COLLECTION_RACE_SCHEDULE)
                .whereEqualTo("codigo_profesor", codeTeacher.getText().toString().split(":")[1])
                .whereNotEqualTo(dayOfWeek, "null")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Map<String, String>> dataClass = new ArrayList<>();
                    String nameSubject = "";
                    Map<String, String> classTeacher;
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        classTeacher = new HashMap<>();//mueve el cursor al primer registro, si existes
                        classTeacher.put("nombre_materia", documentSnapshot.get("nombre_materia").toString());
                        classTeacher.put(dayOfWeek, documentSnapshot.get(dayOfWeek).toString());
                        dataClass.add(classTeacher);
                    }
                    Integer hour = hourNow.getHour();
                    Boolean isClass = false;
                    for (Map<String, String> arr : dataClass) {
                        String hourClass = arr.get(dayOfWeek);
                        nameSubject = arr.get("nombre_materia");
                        String[] schedule = hourClass.split(" ");
                        Integer startTime = Integer.valueOf(schedule[0].substring(0, 2));
                        Integer endTime = Integer.valueOf(schedule[2].substring(0, 2));
                        System.out.println("Hora actual " + hour);
                        System.out.println("startTime " + startTime);
                        System.out.println("endTime " + endTime);
                        if (hour >= startTime && hour < endTime) {
                            isClass = true;
                            break;
                        }
                    }
                    if (isClass) {
                        Toast.makeText(this, "Se ha registrado la asistencia de la \n asignatura " + nameSubject, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "No asignaturas para registrar asistencia", Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(
                        e -> System.out.println(e.getMessage())
                );
        listHourTeacherDayWeek();
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
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        classTeacher = new HashMap<>();//mueve el cursor al primer registro, si existes
                        classTeacher.put("nombre_materia", documentSnapshot.get("nombre_materia").toString());
                        classTeacher.put(dayOfWeek, documentSnapshot.get(dayOfWeek).toString());
                        classTeacher.put("nombre_profesor", documentSnapshot.get("nombre_profesor").toString());
                        dataClass.add(classTeacher);
                    }
                    Integer endTime=0;
                    String[] hourClass;
                    for (Map<String, String> listclass : dataClass) {
                        hourClass = obtainHourClass(listclass.get(dayOfWeek));
                        int j = 2;
                        for(int i=0; i<hourClass.length/4;i++){
                            endTime = Integer.valueOf(hourClass[j].substring(0, 2));
                            j +=4;
                            if (hourNow.getHour() <= endTime) {
                                break;
                            }
                        }
                        if (hourNow.getHour() < endTime) {
                            System.out.println(listclass.get("nombre_materia"));
                            System.out.println(listclass.get(dayOfWeek));
                            System.out.println(listclass.get("nombre_profesor"));
                            System.out.println("****************************************************");
                        }

                    }
                }).addOnFailureListener(e->
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
    public void callback(String item) {
        Toast.makeText(this, item, Toast.LENGTH_LONG).show();
    }
}