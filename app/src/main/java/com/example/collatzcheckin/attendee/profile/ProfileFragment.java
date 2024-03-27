package com.example.collatzcheckin.attendee.profile;


import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.collatzcheckin.R;
import com.example.collatzcheckin.attendee.AttendeeDB;
import com.example.collatzcheckin.attendee.User;
import com.example.collatzcheckin.authentication.AnonAuthentication;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Objects;

/**
 * ProfileFragment displays user their profile information
 */
public class ProfileFragment extends Fragment {

    Button update;
    Button remove;
    User user;
    TextView name, username, email, geo, notification;
    String pfpType;
    ImageView pfp;
    private String uuid;
    private final AnonAuthentication authentication = new AnonAuthentication();
    private final AttendeeDB attendeeDB = new AttendeeDB();
    HashMap<String, String> userData = new HashMap<>();


    /**
     * This constructs an instance of ProfileFragment
     */
    public ProfileFragment(){
    }

    /**
     * Called to create the user profile fragment's Inflates the fragment layout from
     * the specified XML resource, populates the item list from the bundle arguments, and sets up UI
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in
     *                           the fragment.
     * @param container          If non-null, this is the parent view that the fragment's UI should be
     *                           attached to. The fragment should not add the view itself, but this can
     *                           be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous
     *                           saved state as given here.
     * @return The root view of the fragment's layout hierarchy.
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_fragment, container, false);

        initViews(view);

        uuid = authentication.identifyUser();
        getUser(uuid);

        //lauches a new activity and sends user data and recives updated info
        ActivityResultLauncher<Intent> launchEditProfileActivity = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {

                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        assert data != null;
                        User user = (User) data.getSerializableExtra("updatedUser");
                        getUser(uuid);
                    }
                }
        );



        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                intent.putExtra("user", (Serializable) user);
                launchEditProfileActivity.launch(intent);

            }
        });
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w(TAG, user.getPfp());
                if(user.getPfp().equals("userCreated")) {
                    StorageReference imageRef = FirebaseStorage.getInstance().getReference().child("images/" + uuid);
                    imageRef.delete();
                    pfpType = "generated";
                    user.setPfp("generated");
                    getPfp(pfpType, name.getText().toString(), uuid);
                    attendeeDB.addUser(user);
                }
            }
        });
        return view;
    }

    public void initViews(View view) {
        update = view.findViewById(R.id.up_button);
        remove = view.findViewById(R.id.remove);
        name = view.findViewById(R.id.nameText);
        username = view.findViewById(R.id.usernameText);
        email = view.findViewById(R.id.emailText);
        pfp = view.findViewById(R.id.pfp);
        geo = view.findViewById(R.id.geotext);
        notification = view.findViewById(R.id.notiftext);
    }

    public void setData(String nameText, String emailText, String notifText, String geoText) {
        name.setText(nameText);
        email.setText(emailText);

        if(geoText.equals("true")) {
            geo.setText("enabled");
        } else {
            geo.setText("disabled");
        }

        if(notifText.equals("true")) {
            notification.setText("enabled");
        } else {
            notification.setText("disabled");
        }

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

    /**
     * Query to extract user data
     * @param uuid The unique idenitfier assigned to the user using Firebase Authenticator
     */
    private void getUser(String uuid) {
        if (uuid == null) {
            // Handle the case where uuid is null (e.g., log an error, throw an exception, or return)
            Log.e(TAG, "UUID is null in getUser");
            return;
        }
        CollectionReference ref = attendeeDB.getUserRef();
        DocumentReference docRef = ref.document(uuid);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        pfpType = document.getString("Pfp");
                        setData(document.getString("Name"), document.getString("Email"), document.getString("Notif"), document.getString("Geo"));
                        getPfp(pfpType, document.getString("Name"), uuid);
                        user = new User(document.getString("Name"), document.getString("Email"), uuid, Boolean.parseBoolean(document.getString("Geo")), Boolean.parseBoolean(document.getString("Notif")), pfpType);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }
}
