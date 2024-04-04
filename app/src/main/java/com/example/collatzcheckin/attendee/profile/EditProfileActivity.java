package com.example.collatzcheckin.attendee.profile;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.collatzcheckin.MainActivity;
import com.example.collatzcheckin.R;
import com.example.collatzcheckin.attendee.AttendeeDB;
import com.example.collatzcheckin.attendee.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.example.collatzcheckin.utils.PhotoUploader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * EditProfileActivity of the application, allows users to edit their existing profile
 */
public class EditProfileActivity extends AppCompatActivity {
    Button cancel;
    Button confirm;
    ImageView pfp;
    Uri imagePath;
    TextView name, email;
    Switch geo, notif;
    AttendeeDB attendeeDB = new AttendeeDB();
    User user;
    PhotoUploader photoUploader = new PhotoUploader();
    private FusedLocationProviderClient fusedLocationClient;
    private final static int REQUEST_CODE = 100;


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

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        geo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    requestLocationUpdates();
                } else {
                    stopLocationUpdates();
                }
            }
        });



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

    private void requestLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(new LocationRequest(), locationCallback, null);
        } else {
            askPermission();
        }
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    private final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            Location location = locationResult.getLastLocation();
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                user.setLatitude(latitude);
                user.setLongitude(longitude);
            }
        }
    };




    private void askPermission(){
        ActivityCompat.requestPermissions(EditProfileActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode==REQUEST_CODE){
            if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                requestLocationUpdates();
            }
            else{
                Toast.makeText(this,"Required Permission",Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
        Bitmap bitmap = null;
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
        pfp = findViewById(R.id.pfp);
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


