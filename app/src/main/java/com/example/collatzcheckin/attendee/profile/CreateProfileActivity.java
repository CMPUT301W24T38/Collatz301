package com.example.collatzcheckin.attendee.profile;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.collatzcheckin.R;
import com.example.collatzcheckin.attendee.AttendeeCallbackManager;
import com.example.collatzcheckin.attendee.AttendeeDB;
import com.example.collatzcheckin.attendee.User;
import com.example.collatzcheckin.authentication.AnonAuthentication;
import com.example.collatzcheckin.utils.PhotoUploader;
import com.example.collatzcheckin.utils.SignInUserCallback;
import com.google.android.gms.tasks.OnSuccessListener;

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
            authentication.updateUI(this, new OnSuccessListener<String>() {
                @Override
                public void onSuccess(String s) {
                    uuid = authentication.identifyUser();
                    onCallback(false);
                }
            });
        } else {
            attendeeFirebaseManager.userCheck(uuid, this);
        }
    }

    /**
     * Ends activity if this a returing user or create a new profile for new user
     *
     * @param exists       User exists in Firebase
     **/
    @Override
    public void onCallback(boolean exists) {
        if(exists) {
            finish();
        } else {
            validation();
            finish();
        }
    }

    /**
     * Creates guest profile for new user and stores in Firebase
     **/
    private void validation() {
        user = new User(uuid, "Guest", "");
        attendeeDB.addUser(user);
        photoUploader.uploadGenProfile(uuid, "Guest", new OnSuccessListener<String>() {
            @Override
            //dummy method
            public void onSuccess(String s) {
                String test = "test";
            }
        });
    }
}