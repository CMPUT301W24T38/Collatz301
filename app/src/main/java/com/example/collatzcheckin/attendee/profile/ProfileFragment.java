package com.example.collatzcheckin.attendee.profile;


import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.collatzcheckin.R;
import com.example.collatzcheckin.attendee.AttendeeDB;
import com.example.collatzcheckin.attendee.AttendeeFirebaseManager;
import com.example.collatzcheckin.attendee.User;
import com.example.collatzcheckin.authentication.AnonAuthentication;
import com.example.collatzcheckin.utils.FirebaseUserCallback;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

/**
 * ProfileFragment displays user their profile information
 */
public class ProfileFragment extends Fragment implements FirebaseUserCallback {

    Button update;
    Button remove;
    User user;
    TextView name, email, geo, notification;
    ImageView pfp;
    private String uuid;
    private final AnonAuthentication authentication = new AnonAuthentication();
    private final AttendeeDB attendeeDB = new AttendeeDB();
    AttendeeFirebaseManager attendeeFirebaseManager = new AttendeeFirebaseManager();


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
        // Call readData method from FirebaseManager class
        attendeeFirebaseManager.readData(uuid, this);


        //lauches a new activity and sends user data and recives updated info
        ActivityResultLauncher<Intent> launchEditProfileActivity = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {

                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        assert data != null;
                        user = (User) data.getSerializableExtra("updatedUser");
                        attendeeFirebaseManager.readData(uuid, this);
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
                StorageReference imageRef = FirebaseStorage.getInstance().getReference().child("images/" + uuid);
                imageRef.delete();
                user.setPfp(user.getGenpfp());
                setPfp(user);
                attendeeDB.addUser(user);
            }
        });
        return view;
    }

    @Override
    public void onCallback(User user) {
        // Handle the retrieved user data
        this.user = user;
        setData(user);
    }

    private void initViews(View view) {
        update = view.findViewById(R.id.up_button);
        remove = view.findViewById(R.id.remove);
        name = view.findViewById(R.id.nameText);
        email = view.findViewById(R.id.emailText);
        pfp = view.findViewById(R.id.pfp);
        geo = view.findViewById(R.id.geotext);
        notification = view.findViewById(R.id.notiftext);
    }

    public void setData(User user) {
        name.setText(user.getName());
        email.setText(user.getEmail());
        if(user.getGeolocation()) {
            geo.setText("enabled");
        } else {
            geo.setText("disabled");
        }
        if(user.getNotifications()) {
            notification.setText("enabled");
        } else {
            notification.setText("disabled");
        }
        setPfp(user);
    }

    public void setPfp(User user) {
        Glide.with(this).load(user.getPfp()).into(pfp);
    }
}
