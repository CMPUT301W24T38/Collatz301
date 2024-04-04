package com.example.collatzcheckin.event;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.example.collatzcheckin.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class QrActivity extends AppCompatActivity {
    StorageReference storageReference;
    ImageView qrImage;
    RadioButton eventQr;
    RadioButton shareQr;
    Button backButton;
    Button shareButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);
        Intent intent = getIntent();
        String eventid = intent.getStringExtra("id");
        qrImage = findViewById(R.id.qr_image_view);

        eventQr = findViewById(R.id.event_qr);
        shareQr = findViewById(R.id.share_qr);
        backButton = findViewById(R.id.back_button);
        shareButton = findViewById(R.id.share_button);
        getEventQR(eventid);

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                share();
            }
        });


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

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                finish();
            }
        });
    }

    private void share() {
        Uri imageUri = getImageUriFromImageView(qrImage);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Body");
        startActivity(Intent.createChooser(shareIntent, "Share"));
    }
    private Uri getImageUriFromImageView(ImageView imageView) {
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Image Description", null);
        return Uri.parse(path);
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