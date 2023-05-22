package com.example.hello_work.infraestructure.adapter.listener;

import com.example.hello_work.infraestructure.adapter.ClassData;
import com.google.firebase.firestore.FirebaseFirestore;

import org.apache.poi.ss.usermodel.Row;

public interface Listener {
    void callback(ClassData item, Boolean isPresent);
}
