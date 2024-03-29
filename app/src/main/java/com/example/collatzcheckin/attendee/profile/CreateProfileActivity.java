package com.example.collatzcheckin.attendee.profile;

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
import com.example.collatzcheckin.attendee.AttendeeDB;
import com.example.collatzcheckin.attendee.User;
import com.example.collatzcheckin.authentication.AnonAuthentication;
import com.example.collatzcheckin.utils.PhotoUploader;
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
public class CreateProfileActivity extends AppCompatActivity {

    private Button doneButton;
    private Button adminButton;
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
        adminButton = findViewById(R.id.admin_button);
        adminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create and show a popup window for verification
                AlertDialog.Builder builder = new AlertDialog.Builder(CreateProfileActivity.this);
                builder.setTitle("Verification");
                builder.setMessage("Enter your verification code:");

                final EditText input = new EditText(CreateProfileActivity.this);
                builder.setView(input);

                builder.setPositiveButton("Verify", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String verificationCode = input.getText().toString();
                        // Add verification logic here, currently a placeholder
                        if (verificationCode.equals("1234")) {
                            setResultAndFinish(Activity.RESULT_OK, "admin");
                        } else {
                            // Verification failed, show an error message or handle accordingly
                            Toast.makeText(CreateProfileActivity.this, "Verification failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });
    }

    private void setResultAndFinish(int resultCode, String buttonClicked) {
        String nameEdit = userName.getText().toString();
        String emailEdit = userEmail.getText().toString();
        if (nameEdit.length() < 1) {
            userName.setError("Please enter your name.");
            isVaild = false;
        } else {
            userName.setError(null);
        }

        if (emailEdit.length() < 1) {
            userEmail.setError("Please enter your email.");
            isVaild = false;
        } else {
            userEmail.setError(null);
        }

        if (isVaild) {
            user = new User(userUuid, nameEdit, emailEdit);
            user.setAdmin(true);
            attendeeDB.addUser(user);
            Intent resultIntent = new Intent();
            resultIntent.putExtra("buttonClicked", buttonClicked);
            setResult(resultCode, resultIntent);
            finish();
        }
    }

}