package com.example.collatzcheckin.event;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.collatzcheckin.R;
import com.example.collatzcheckin.authentication.AnonAuthentication;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CameraFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CameraFragment extends Fragment {
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.activity_camera, container, false);
        Button scanButton = view.findViewById(R.id.scan_button);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                scan();
            }
        });
        return view;
    }

    private void scan() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Press volume up to turn on flash");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(Capture.class);
        barLauncher.launch(options);

    }

    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() != null) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Result");

            EventDB db = new EventDB();
            final AnonAuthentication authentication = new AnonAuthentication();
            String uuid = authentication.identifyUser();

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
                                builder.setMessage("You have successfully checked in to the event!");
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).show();
                            }
                            else {
                                builder.setMessage("You have not signed up for this event.");
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