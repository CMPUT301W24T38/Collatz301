package com.example.collatzcheckin;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import com.example.collatzcheckin.attendee.AttendeeDB;

import java.util.ArrayList;
import java.util.HashMap;

import com.example.collatzcheckin.admin.controls.events.AdminEventListFragment;
import com.example.collatzcheckin.admin.controls.events.AdminEventViewFragment;
import com.example.collatzcheckin.admin.controls.profile.UserListFragment;
import com.example.collatzcheckin.admin.controls.profile.UserViewFragment;
import com.example.collatzcheckin.attendee.User;
import com.example.collatzcheckin.attendee.events.BrowseEventsFragment;
import com.example.collatzcheckin.attendee.profile.ProfileFragment;
import com.example.collatzcheckin.attendee.profile.CreateProfileActivity;
import com.example.collatzcheckin.authentication.AnonAuthentication;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.example.collatzcheckin.event.EditEventFragment;
import com.example.collatzcheckin.event.Event;
import com.example.collatzcheckin.event.EventDB;
import com.example.collatzcheckin.event.EventList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * MainActivity of the application, handles setting up the bottom nav fragment and the user
 */
public class MainActivity extends AppCompatActivity {

    private final AnonAuthentication authentication = new AnonAuthentication();
    private User user;
    private Button viewAttendeeButton;
    private ArrayList<String> data;
    EventDB db = new EventDB();

    /**
     * Method to run on creation of the activity. Handles user authentication and creates the bottomnav fragment
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // check if user is a new or returning user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        // if new user prompt the user to create a profile
        if(currentUser == null) {
            //authentication.updateUI(this);
            Log.d(TAG, "signInAnon");
            Intent i = new Intent(MainActivity.this, CreateProfileActivity.class);
            startActivity(i);
        }

        // show home page
        replaceFragment(new BrowseEventsFragment());

        String uuid = authentication.identifyUser();
        AttendeeDB db = new AttendeeDB();
        HashMap<String,String> userData = db.findUser(uuid);
        user = new User(userData.get("Uid"), userData.get("Name"), userData.get("Email"));


        // creating the nav bar
        // adds functionality to allow attendee to navigate
        bottomNavigationView.setOnItemSelectedListener(item -> {

            int iconPressed= item.getItemId();

            // navigate to profile page
            if (iconPressed == R.id.profile) {
                replaceFragment(new ProfileFragment());
            }
            // navigate to page to browse events
            if (iconPressed == R.id.search) {
                replaceFragment(new BrowseEventsFragment());
            }

            if (iconPressed == R.id.home) {
                Intent i = new Intent(this, EventList.class);
                i.putExtra("user", user);
                startActivity(i);
            }
            //TODO: navigate to camera so users can scan QR code

            return true;
        });
    }

    /**
     * Switches fragments to correponding button on nav bar
     *
     * @param fragment Fragment to switch to
     */
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

}