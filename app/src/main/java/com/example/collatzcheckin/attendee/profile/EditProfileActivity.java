package com.example.collatzcheckin.attendee.profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.collatzcheckin.R;
import com.example.collatzcheckin.attendee.AttendeeDB;
import com.example.collatzcheckin.attendee.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

/**
 * EditProfileActivity of the application, allows users to edit their existing profile
 */
public class EditProfileActivity extends AppCompatActivity {
    Button cancel;
    Button confirm;
    ShapeableImageView pfp;
    Uri imagePath;
    TextView name, username, email;
    Switch geo, notif;
    AttendeeDB attendeeDB = new AttendeeDB();
    User user;


    /**
     * Method to run on creation of the activity. Handles user profile editing abilities
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_edit);

        cancel = findViewById(R.id.cancel_button);
        confirm = findViewById(R.id.confirm_button);
        pfp = findViewById(R.id.editpfp);
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");
        name = findViewById(R.id.editName);
        username = findViewById(R.id.editUsername);
        email = findViewById(R.id.editEmail);
        geo = (Switch) findViewById(R.id.enablegeo);
        notif = (Switch) findViewById(R.id.enablenotif);

        if (!user.getName().equals("")){
            name.setText(user.getName());
        }

        if (!user.getEmail().equals("")){
            email.setText(user.getEmail());
        }
        if (user.getPfp()!=null){
            getPfp(user.getPfp(), user.getName(), user.getUid());
        }
        geo.setChecked(user.isGeolocation());
        notif.setChecked(user.isNotifications());


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String newName = name.getText().toString();
                user.setName(newName);
                String newEmail = email.getText().toString();
                user.setEmail(newEmail);
                user.setGeolocation(geo.isChecked());
                user.setNotifications(notif.isChecked());



                if (imagePath!=null) {
                    user.setPfp("UserCreated");
                    FirebaseStorage.getInstance().getReference("images/" + user.getUid()).putFile(imagePath).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(EditProfileActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                }
                Intent resultIntent = new Intent();
                resultIntent.putExtra("updatedUser", user);
                setResult(RESULT_OK, resultIntent);
                attendeeDB.addUser(user);
                finish();
            }
        });

        pfp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                photoIntent.setType("image/*");
                startActivityForResult(photoIntent,1);
            }
        });
    }
    @Override
    protected void onActivityResult(int request_Code, int resultCode, @Nullable Intent data) {
        super.onActivityResult(request_Code, resultCode, data);
        if (request_Code == 1 && resultCode == RESULT_OK && data != null) {
            imagePath = data.getData();
            getImageInImageView();
            user.setPfp("userCreated");
        }
    }
    private void getImageInImageView() {
        Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagePath);
            } catch (IOException e) {
                e.printStackTrace();
        }
        pfp.setImageBitmap(bitmap);
        user.setPfp("userCreated");

        }

    public void getPfp(String type, String name, String uuid){

        String nameLetter = String.valueOf(name.charAt(0));
        nameLetter = nameLetter.toUpperCase();

        if(type.equals("generated")) {
            StorageReference pfpRef = FirebaseStorage.getInstance().getReference().child("generatedpfp/" + nameLetter + ".png");
            try {
                final File localFile = File.createTempFile("temp", "png");
                pfpRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        // Set the downloaded image to the ImageView
                        pfp.setImageURI(Uri.fromFile(localFile));
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                // Handle exception
            }
        } else if(type.equals("userCreated")) {
            StorageReference pfpRef = FirebaseStorage.getInstance().getReference().child("images/" + uuid);
            try {
                final File localFile = File.createTempFile("images", "jpg");
                pfpRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        // Set the downloaded image to the ImageView
                        pfp.setImageURI(Uri.fromFile(localFile));
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                // Handle exception
            }
        }
    }

    }


