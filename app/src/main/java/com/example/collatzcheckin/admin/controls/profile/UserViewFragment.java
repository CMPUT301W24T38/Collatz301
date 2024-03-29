package com.example.collatzcheckin.admin.controls.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.collatzcheckin.MainActivity;
import com.example.collatzcheckin.R;
import com.example.collatzcheckin.admin.controls.AdministratorDB;
import com.example.collatzcheckin.attendee.AttendeeDB;
import com.example.collatzcheckin.attendee.User;
import com.example.collatzcheckin.authentication.AnonAuthentication;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

public class UserViewFragment extends Fragment {
    private User user;
    private View view;

    Button backButton;
    Button deleteButton;
    Button deleteImageButton;
    ShapeableImageView pfp;
    Uri imagePath;
    TextView name, username, email;
    Switch geo, notif;
    AttendeeDB attendeeDB = new AttendeeDB();

    public UserViewFragment(User user) {
        this.user = user;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_admin_user_view, container, false);


        pfp = view.findViewById(R.id.editpfp);
        Intent intent = requireActivity().getIntent();
        name = view.findViewById(R.id.editName);
        username = view.findViewById(R.id.editUsername);
        email = view.findViewById(R.id.editEmail);
        geo = (Switch) view.findViewById(R.id.enablegeo);
        notif = (Switch) view.findViewById(R.id.enablenotif);

        if (user != null) {
            if (user.getName() != null && !user.getName().isEmpty()) {
                name.setText(user.getName());
            }
            if (user.getUsername() != null && !user.getUsername().isEmpty()) {
                username.setText(user.getUsername());
            }
            if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                email.setText(user.getEmail());
            }
            if (user.getPfp() != null) {
                pfp.setImageURI(Uri.parse(user.getPfp()));
            }
            geo.setChecked(user.isGeolocation());
            notif.setChecked(user.isNotifications());
        }


        deleteButton = view.findViewById(R.id.delete_user);

        deleteButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                AdministratorDB administratorDB = new AdministratorDB();

                // Call the removeEvent method passing event as parameter
                administratorDB.removeProfile(user);
                UserListFragment userListFragment = new UserListFragment();
                ((MainActivity) requireActivity()).replaceFragment(userListFragment);
            }
        });
        deleteImageButton = view.findViewById(R.id.remove_image);
        deleteImageButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                pfp.setImageResource(R.drawable.baseline_person_24);
                StorageReference imageRef = FirebaseStorage.getInstance().getReference().child("images/" + user.getUsername());
                imageRef.delete();
            }
        });
        backButton = view.findViewById(R.id.back_button_user_view);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to the user list
                UserListFragment userListFragment = new UserListFragment();
                ((MainActivity) requireActivity()).replaceFragment(userListFragment);
            }
        });

        return view;
    }

}
