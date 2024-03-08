package com.example.collatzcheckin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;

import com.example.collatzcheckin.attendee.User;
import com.example.collatzcheckin.attendee.profile.ProfileFragment;
import com.example.collatzcheckin.attendee.profile.UpdateProfileActivity;
import com.example.collatzcheckin.authentication.AnonAuthentication;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * MainActivity of the application, handles setting up the bottom nav fragment and the user
 */
public class MainActivity extends AppCompatActivity {

    private final AnonAuthentication authentication = new AnonAuthentication();
    private User user;

    /**
     * Method to run on creation of the activity. Handles user authentication and creates the bottomnav fragment
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_view_organizer);

        if(authentication.updateUI(MainActivity.this)) {
            Intent i = new Intent(MainActivity.this, UpdateProfileActivity.class);
            startActivity(i);
        }

        // creating the nav bar
        // adds functionality to allow attendee to navigate
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {

            int iconPressed= item.getItemId();

            // navigate to profile page
            if (iconPressed == R.id.profile) {
                replaceFragment(new ProfileFragment());
            }
            //TODO: navigate to home page (where users can browse events)

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