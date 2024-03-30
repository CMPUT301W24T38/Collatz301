package com.example.collatzcheckin.attendee.profile;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.collatzcheckin.MainActivity;
import com.example.collatzcheckin.R;
import com.example.collatzcheckin.attendee.AttendeeCallbackManager;
import com.example.collatzcheckin.attendee.AttendeeDB;
import com.example.collatzcheckin.attendee.User;
import com.example.collatzcheckin.authentication.AnonAuthentication;
import com.example.collatzcheckin.utils.PhotoUploader;
import com.example.collatzcheckin.utils.SignInUserCallback;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;

/**
 * UpdateProfileActivity of the application, handles creating a new user profile
 */
public class CreateProfileActivity extends AppCompatActivity implements SignInUserCallback {

    private String uuid;
    private final AnonAuthentication authentication = new AnonAuthentication();
    AttendeeCallbackManager attendeeFirebaseManager = new AttendeeCallbackManager();
    private final AttendeeDB attendeeDB = new AttendeeDB();
    private User user;
    PhotoUploader photoUploader = new PhotoUploader();

    /**
     * Method to run on creation of the activity. Handles user profile creation
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        uuid = authentication.identifyUser();
        if(!authentication.validateUser()) {
            authentication.updateUI(this);
        } else {
            attendeeFirebaseManager.userCheck(uuid, this);
        }
    }

    @Override
    public void onCallback(boolean exists) {
        if(exists) {
            finish();
        } else {
            validation();
            finish();
        }
    }

    private void validation() {
        user = new User(uuid, "Guest", "");
        attendeeDB.addUser(user);
        photoUploader.uploadGenProfile(uuid, "Guest");
    }
}