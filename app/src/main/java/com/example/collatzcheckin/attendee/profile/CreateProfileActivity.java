package com.example.collatzcheckin.attendee.profile;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.collatzcheckin.R;
import com.example.collatzcheckin.attendee.AttendeeDB;
import com.example.collatzcheckin.attendee.User;
import com.example.collatzcheckin.authentication.AnonAuthentication;
import com.example.collatzcheckin.utils.PhotoUploader;

/**
 * UpdateProfileActivity of the application, handles creating a new user profile
 */
public class CreateProfileActivity extends AppCompatActivity {

    private Button doneButton;
    private EditText userName;
    private EditText userEmail;
    private String userUuid;
    private final AnonAuthentication authentication = new AnonAuthentication();
    private final AttendeeDB attendeeDB = new AttendeeDB();
    private User user;
    private boolean isVaild = true;
    PhotoUploader photoUploader = new PhotoUploader();

    Uri imageUri;

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
        authentication.updateUI(this);

        doneButton = findViewById(R.id.done_button);
        userName = findViewById(R.id.username);
        userEmail = findViewById(R.id.email);

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameEdit = userName.getText().toString();
                String emailEdit = userEmail.getText().toString();

                // simple error checking
                if(nameEdit.length() < 1) {
                    userName.setError("Please enter your name.");
                    isVaild = false;
                } else {
                    userName.setError(null);
                }

                if(emailEdit.length() < 1) {
                    userEmail.setError("Please enter your email.");
                    isVaild = false;
                } else {
                    userEmail.setError(null);
                }
                userUuid = authentication.identifyUser();

                if(isVaild) {
                    user = new User(userUuid, nameEdit, emailEdit);
                    attendeeDB.addUser(user);
                    photoUploader.uploadGenProfile(user.getUid(), user.getName());
                    finish();
                }

            }
        });
    }

}