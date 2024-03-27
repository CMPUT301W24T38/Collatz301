package com.example.collatzcheckin;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.example.collatzcheckin.attendee.AttendeeDB;
import java.net.Authenticator;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.ArrayList;
import java.util.HashMap;

import com.example.collatzcheckin.admin.controls.events.AdminEventListFragment;
import com.example.collatzcheckin.admin.controls.events.AdminEventViewFragment;
import com.example.collatzcheckin.admin.controls.profile.UserListFragment;
import com.example.collatzcheckin.admin.controls.profile.UserViewFragment;
import com.example.collatzcheckin.attendee.AttendeeDBConnecter;
import com.example.collatzcheckin.attendee.User;
import com.example.collatzcheckin.attendee.events.BrowseEventsFragment;
import com.example.collatzcheckin.attendee.profile.CreateProfileActivity;
import com.example.collatzcheckin.attendee.profile.ProfileFragment;
import com.example.collatzcheckin.attendee.profile.CreateProfileActivity;
import com.example.collatzcheckin.authentication.AnonAuthentication;
import com.example.collatzcheckin.event.CameraActivity;
import com.example.collatzcheckin.event.CameraFragment;
import com.example.collatzcheckin.event.EventListFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import com.example.collatzcheckin.event.EditEventFragment;
import com.example.collatzcheckin.event.Event;
import com.example.collatzcheckin.event.EventDB;
import com.example.collatzcheckin.event.EventList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * MainActivity of the application, handles setting up the bottom nav fragment and the user
 */
public class MainActivity extends AppCompatActivity implements AttendeeDB.UserCallback {

    private final AnonAuthentication authentication = new AnonAuthentication();
    private User user;
    private Button viewAttendeeButton;
    private ArrayList<String> data;
    EventDB db = new EventDB();

    private BottomNavigationView bottomNavigationView;


    private static final int UPDATE_PROFILE_REQUEST_CODE = 1001;



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
        String uuidVerify = authentication.identifyUser();


        // Need to test with both authentication methods
        /*// check if user is a new or returning user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        // if new user prompt the user to create a profile
        if(currentUser == null) {
            //authentication.updateUI(this);
            Log.d(TAG, "signInAnon");
            Intent i = new Intent(MainActivity.this, CreateProfileActivity.class);
            startActivity(i);
        }*/
        
        
        if (authentication.updateUI(MainActivity.this) || (uuidVerify == null)) {
            Intent i = new Intent(MainActivity.this, UpdateProfileActivity.class);
            startActivityForResult(i, UPDATE_PROFILE_REQUEST_CODE);
        }
        String uuid = authentication.identifyUser();
        user = new User(uuid);
        AttendeeDB db = new AttendeeDB();
        db.loadUser(uuid, this); //Continues in onUserLoaded


        EventDB eventDB = new EventDB();
    }

    /**
     * Switches fragments to correponding button on nav bar
     *
     * @param fragment Fragment to switch to
     */
    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == UPDATE_PROFILE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            String buttonClicked = data.getStringExtra("buttonClicked");
            if ("admin".equals(buttonClicked)) {
                setContentView(R.layout.activity_main);
                BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
                String uuidVerify = authentication.identifyUser();


                String uuid = authentication.identifyUser();
                AttendeeDB db = new AttendeeDB();
                HashMap<String,String> userData = db.findUser(uuid);
                user = new User(userData.get("Uid"), userData.get("Name"), userData.get("Email"));
                user.setAdmin(true);
                Log.d("MainActivityAdmin", "User details: Name: " + user.getName() + ", UID: " + user.getUid() + ", Email: " + user.getEmail() + ", Admin: " + (user.isAdmin() ? "Yes" : "No"));

                EventDB eventDB = new EventDB();
                // creating the nav bar
                // adds functionality to allow attendee to navigate
                bottomNavigationView.setOnItemSelectedListener(item -> {

                    int iconPressed= item.getItemId();

                    // navigate to admin profile page
                    if (iconPressed == R.id.profile) {
                        Log.d("MainActivityAdmin", "User details: Name: " + user.getName() + ", UID: " + user.getUid() + ", Email: " + user.getEmail() + ", Admin: " + (user.isAdmin() ? "Yes" : "No"));
                        replaceFragment(new UserListFragment());
                    }
                    //naviagte to admin event page
                    if (iconPressed == R.id.home) {
                        Log.d("MainActivityAdmin", "User details: Name: " + user.getName() + ", UID: " + user.getUid() + ", Email: " + user.getEmail() + ", Admin: " + (user.isAdmin() ? "Yes" : "No"));
                        replaceFragment(new AdminEventListFragment());
                    }
                    //TODO: navigate to camera so users can scan QR code

                    return true;
                });
            }
        }
    }




    public void showAdminEventList() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.event_frame_view, new AdminEventListFragment())
                .addToBackStack(null)
                .commit();
    }
    public void showAdminEventView(Event e) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.event_frame_view, new AdminEventViewFragment(e))
                .addToBackStack(null)
                .commit();
    }
    public void showUserList() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container_user, new UserListFragment())
                .addToBackStack(null)
                .commit();
    }
    public void showAdminUserView(User user) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container_user, new UserViewFragment(user))
                .addToBackStack(null)
                .commit();
    }
    public void showEditEvent(Event e) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.event_frame_view, new EditEventFragment(e))
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onUserLoaded(User user) {
        if (user != null){
            if (user.isAdmin()){
                bottomNavigationView.setOnItemSelectedListener(item -> {

                    int iconPressed= item.getItemId();

                    // navigate to admin profile page
                    if (iconPressed == R.id.profile) {
                        Log.d("MainActivityAdmin", "User details: Name: " + user.getName() + ", UID: " + user.getUid() + ", Email: " + user.getEmail() + ", Admin: " + (user.isAdmin() ? "Yes" : "No"));
                        replaceFragment(new UserListFragment());
                    }
                    //navigate to admin event page
                    if (iconPressed == R.id.home) {
                        Log.d("MainActivityAdmin", "User details: Name: " + user.getName() + ", UID: " + user.getUid() + ", Email: " + user.getEmail() + ", Admin: " + (user.isAdmin() ? "Yes" : "No"));
                        replaceFragment(new AdminEventListFragment());
                    }
                    //TODO: navigate to camera so users can scan QR code

                    return true;
                });
            }
            else {
                Log.d("Not Admin", "Admin is false");
                // creating the nav bar
                // adds functionality to allow attendee to navigate
                bottomNavigationView.setOnItemSelectedListener(item -> {
                    // show home page
                    replaceFragment(new EventListFragment());
                    // navigate to profile page
                    if (iconPressed == R.id.profile) {
                        replaceFragment(new ProfileFragment());
                    }
                    // navigate to page to browse events
                    if (iconPressed == R.id.search) {
                        replaceFragment(new BrowseEventsFragment());
                    }
                    if (iconPressed == R.id.scanner) {
//                Intent i = new Intent(this, CameraActivity.class);
//                i.putExtra("uuid", uuid);
//                startActivity(i);
                        replaceFragment(new CameraFragment());
                    }

                    if (iconPressed == R.id.home) {
//                Intent i = new Intent(this,EventList.class);
//                i.putExtra("uuid", uuid);
//                startActivity(i);
                        replaceFragment(new EventListFragment());
                    }
                    //TODO: navigate to camera so users can scan QR code

                    return true;
                });
            }
        }
    }
}