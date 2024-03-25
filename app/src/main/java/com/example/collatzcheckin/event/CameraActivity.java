package com.example.collatzcheckin.event;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Instrumentation;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.collatzcheckin.R;
import com.example.collatzcheckin.attendee.AttendeeDB;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.HashMap;
import java.util.List;

public class CameraActivity extends AppCompatActivity {
    boolean userFound = false;
    String eventId;
    HashMap<String, String> attendees;
    FusedLocationProviderClient fusedLocationProviderClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        Button scanButton = findViewById(R.id.scan_button);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {

                scan();
                if (userFound) {
                    EventDB db = new EventDB();
                }
            }
        });
    }

    private void scan() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Press volume up to turn on flash");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(Capture.class);
        barLauncher.launch(options);

    }
    private void updateUserLocation(String userId, double latitude, double longitude) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("user")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String geo = documentSnapshot.getString("Geo");
                        if (geo != null && geo.equals("true")) {
                            db.collection("user")
                                    .document(userId)
                                    .update("Latitude", latitude,"Longitude",longitude)
                                    .addOnSuccessListener(aVoid -> Log.d("UserLocation", "Location updated successfully"))
                                    .addOnFailureListener(e -> Log.e("UserLocation", "Error updating location", e));
                        }
                    }
                }).addOnFailureListener(e -> {
                    Log.e("ERRLOLOLOL","lol noooo",e);
                });
    }


    @SuppressLint("MissingPermission")
    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() != null) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Result");



            EventDB db = new EventDB();
            Intent intent = getIntent();
            String uuid = intent.getStringExtra("uuid");

            db.eventRef.document(result.getContents())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Document found

                            HashMap<String,String> attendees = (HashMap<String,String>) documentSnapshot.get("Attendees");
                            if (attendees.containsKey(uuid)) {
                                Log.d("TAG", "FOUND");
                                String count = attendees.get(uuid);
                                int parsedCount = Integer.parseInt(count) + 1;
                                attendees.put(uuid, Integer.toString(parsedCount));
                                db.eventRef.document(documentSnapshot.getId()).update("Attendees", attendees);
                                builder.setMessage("You have successfully checked in to the event!");// Example value, replace with actual longitude

                                fusedLocationProviderClient.getLastLocation()
                                        .addOnSuccessListener(location -> {
                                                    if (location != null) {
                                                        // Get latitude and longitude
                                                        double latitude = location.getLatitude();
                                                        double longitude = location.getLongitude();
                                                        // Update user location in Firestore
                                                        updateUserLocation(uuid, latitude, longitude);
                                                    }
                                                });
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).show();
                            }
                            else {
                                builder.setMessage("You have not signed up for this event!");
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).show();
                            }
                        } else {
                            // Document does not exist
                            Log.d("TAG", "No such document");
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Handle any errors
                        Log.d("TAG", "Error getting document", e);
                    });

        }
    });
}
