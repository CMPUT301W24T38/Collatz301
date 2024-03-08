package com.example.collatzcheckin.admin.controls;

import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Class for establishing a connection to the Firebase Firestore database.
 */
public class AdministratorDBConnector {
    public FirebaseFirestore db;
    /**
     * Constructor to initialize the Firestore database instance.
     */
    public  AdministratorDBConnector() {
        this.db = FirebaseFirestore.getInstance();
    }
}
