package com.example.collatzcheckin.event;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.collatzcheckin.R;
import com.example.collatzcheckin.attendee.User;
import com.example.collatzcheckin.attendee.AttendeeDB;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.io.ByteArrayDataOutput;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class CreateEvent extends AppCompatActivity {
    View view;
    Context context = this;
    ImageView posterImage;

    TextView eventTitle;
    TextView eventLocation;
    TextView eventDate;
    TextView eventDescription;
    TextView eventLimit;
    Uri imageUri;
    String id;

    StorageReference storageReference;

    //User user;
    /**
     * Method to run on creation of the activity. Handles create event
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        Intent intent = getIntent();
        String uuid = intent.getStringExtra("uuid");
        id = generateRandomString(16);


        eventTitle = findViewById(R.id.edit_event_name);
        eventDate = findViewById(R.id.edit_event_date);
        eventLocation = findViewById(R.id.edit_event_location);
        eventDescription = findViewById(R.id.edit_event_description);
        eventLimit = findViewById(R.id.edit_event_limit);
        posterImage = findViewById(R.id.poster_image);


        Button selectPosterButton = findViewById(R.id.select_poster_button);
        Button uploadPosterButton = findViewById(R.id.upload_poster_button);
        Button backButton = findViewById(R.id.back_button_create_event);
        Button addEventButton = findViewById(R.id.add_event);

        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get values
                String title = eventTitle.getText().toString();
                String date = eventDate.getText().toString();
                String location = eventLocation.getText().toString();
                String description = eventDescription.getText().toString();
                String limit = eventLimit.getText().toString();
                //Update user and event db
                EventDB db = new EventDB();
                AttendeeDB userDb = new AttendeeDB();
                storageReference = FirebaseStorage.getInstance().getReference("posters/"+id);
                try {
                    final File localFile = File.createTempFile("images", "jpg");
                    storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            // Set the downloaded image to the ImageView
                            posterImage.setImageURI(Uri.fromFile(localFile));
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    // Handle exception
                }



                Bitmap qr = generateQR(id);
                storageReference = FirebaseStorage.getInstance().getReference("/qr");
                StorageReference imgRef = storageReference.child(id + ".jpg");
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                qr.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();

                UploadTask uploadTask = imgRef.putBytes(data);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        String uri = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
                        Event event = new Event(title, uuid, date, description, uri, location, Integer.parseInt(limit), id, new HashMap<String,String>());
                        db.addEvent(event);
                        finish();
                    }
                });


            }
        });

        //Return without creating an event
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        selectPosterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPoster();

            }
        });
        uploadPosterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadPoster();

            }
        });

    }

    private void uploadPoster() {
        //SimpleDateFormat date_formatter = new SimpleDateFormat("yyyy_MM-dd_HH_mm_ss", Locale.CANADA);
        //Date now = new Date();
        //String poster_filename = date_formatter.format(now);
        storageReference = FirebaseStorage.getInstance().getReference("posters/"+id);
        storageReference.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>(){
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot){
                        Toast.makeText(context, "Successfully Uploaded", Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener(){
                    @Override
                    public void onFailure(@NonNull Exception e){

                        Toast.makeText(context, "Failed To Upload",Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private final ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null && data.getData() != null) {
                        imageUri = data.getData();
                        posterImage.setImageURI(imageUri);
                    }
                }
            });
    private void selectPoster() {
        Intent imageIntent = new Intent();
        imageIntent.setType("image/*");
        imageIntent.setAction(Intent.ACTION_GET_CONTENT);
        Log.d("TAG", "selectPoster ");
        launcher.launch(imageIntent);
    }

    public static String generateRandomString(int length) {
        String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            char randomChar = CHARACTERS.charAt(randomIndex);
            sb.append(randomChar);
        }
        return sb.toString();
    }

    public static Bitmap generateQR(String id) {
        MultiFormatWriter writer = new MultiFormatWriter();
        try {
            BitMatrix matrix = writer.encode(id, BarcodeFormat.QR_CODE, 600, 600);
            BarcodeEncoder encoder = new BarcodeEncoder();
            Bitmap bitmap = encoder.createBitmap(matrix);
            return  bitmap;
        }
        catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }
}