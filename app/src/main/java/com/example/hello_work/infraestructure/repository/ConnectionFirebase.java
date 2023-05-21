package com.example.hello_work.infraestructure.repository;

import com.google.firebase.firestore.FirebaseFirestore;

public class ConnectionFirebase {
    public static FirebaseFirestore connection(){
        return FirebaseFirestore.getInstance();
    }


}
