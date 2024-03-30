package com.example.collatzcheckin.attendee.profile;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.collatzcheckin.R;
import com.example.collatzcheckin.attendee.AttendeeDB;
import com.example.collatzcheckin.attendee.User;
import com.example.collatzcheckin.utils.PhotoUploader;
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
    TextView name, email;
    Switch geo, notif;
    AttendeeDB attendeeDB = new AttendeeDB();
    User user;
    PhotoUploader photoUploader = new PhotoUploader();
    Bitmap bitmap = null;


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

        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");
        initViews();
        setData();


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
                //user.setBitmap(bitmap);
                if (imagePath!=null) {
                    photoUploader.uploadProfile(user.getUid(), imagePath, new OnSuccessListener<String>() {
                        @Override
                        public void onSuccess(String uri) {
                            user.setPfp(uri);
                            attendeeDB.addUser(user);
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("updatedUser", user);
                            setResult(RESULT_OK, resultIntent);
                            finish();
                        }
                    });
                } else {
                    attendeeDB.addUser(user);
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("updatedUser", user);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                }

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
        }
    }

    private void getImageInImageView() {
        bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagePath);
            } catch (IOException e) {
                e.printStackTrace();
        }
        pfp.setImageBitmap(bitmap);
    }

    private void initViews() {
        cancel = findViewById(R.id.cancel_button);
        confirm = findViewById(R.id.confirm_button);
        pfp = findViewById(R.id.editpfp);
        name = findViewById(R.id.editName);
        email = findViewById(R.id.editEmail);
        geo = (Switch) findViewById(R.id.enablegeo);
        notif = (Switch) findViewById(R.id.enablenotif);
    }

    private void setData() {
        if (!user.getName().equals("")){
            name.setText(user.getName());
        }

        if (!user.getEmail().equals("")){
            email.setText(user.getEmail());
        }

        geo.setChecked(user.isGeolocation());
        notif.setChecked(user.isNotifications());
        setPfp(user);
    }

    public void setPfp(User user) {
        Glide.with(this).load(user.getPfp()).into(pfp);
    }

    }


