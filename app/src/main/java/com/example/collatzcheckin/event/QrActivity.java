package com.example.collatzcheckin.event;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.example.collatzcheckin.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class QrActivity extends AppCompatActivity {
    StorageReference storageReference;
    ImageView qrImage;
    RadioButton eventQr;
    RadioButton shareQr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);
        Intent intent = getIntent();
        String eventid = intent.getStringExtra("id");
        qrImage = findViewById(R.id.qr_image_view);

        eventQr = findViewById(R.id.event_qr);
        shareQr = findViewById(R.id.share_qr);

        getEventQR(eventid);

        eventQr.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                getEventQR(eventid);
            }
        });

        shareQr.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                getEventQR(eventid + "_share");
            }
        });

    }

    public void getEventQR(String eventid) {
        storageReference = FirebaseStorage.getInstance().getReference("qr/"+eventid+".jpg");
        try {
            final File localFile = File.createTempFile("qrrrrr", "jpg");
            storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    // Set the downloaded image to the ImageView
                    qrImage.setImageURI(Uri.fromFile(localFile));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                    Log.e("Firebase", "Error downloading image: " + e.getMessage());
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            // Handle exception
        }
    }
}