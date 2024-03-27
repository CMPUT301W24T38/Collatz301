package com.example.collatzcheckin.attendee;


import android.util.Log;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;
import androidx.annotation.NonNull;

import com.example.collatzcheckin.attendee.User;
import com.example.collatzcheckin.event.Event;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;

import java.util.HashMap;

/**
 * AttendeeDB interacts with the database collectction that holds user info
 */
public class AttendeeDB {
    private AttendeeDBConnecter userDB;
    public CollectionReference userRef;

    public interface UserCallback {
        void onUserLoaded(User user);
    }

    /**
     * This constructs instance of database and sets the collection to 'user'
     */
    public AttendeeDB() {
        this.userDB = new AttendeeDBConnecter();
        this.userRef = userDB.db.collection("user");
    }

    /**
     * This returns the object 'CollectionReference' which holds information about the
     * collection that is being interacted with
     * @return CollectionReference
     */
    public CollectionReference getUserRef() {
        return userRef;
    }

    /**
     * Query to extract user data
     * @param uuid The unique idenitfier assigned to the user using Firebase Authenticator
     */
    public HashMap<String, String> findUser(String uuid) {
        HashMap<String, String> userData = new HashMap<>();
        userRef.document(uuid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        Log.d(TAG, "DocumentSnapshot data: " + document.getString("Name"));
                        userData.put("Name", document.getString("Name"));
                        userData.put("Email", document.getString("Email"));
                        userData.put("Uid", document.getString("Uid"));
                        userData.put("Admin", document.getString("Admin"));

                        Log.d(TAG, "DocumentSnapshot data: " + userData.get("Name"));
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
        return userData;
    }

    public void loadUser(String uuid, UserCallback callback){
        userRef.document(uuid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        Log.d(TAG, "DocumentSnapshot data: " + document.getString("Name"));
                        String name = document.getString("Name");
                        String email = document.getString("Email");
                        String uid = document.getString("Uid");
                        String admin = document.getString("Admin");
                        boolean is_admin = Boolean.parseBoolean(admin);

                        Log.d("Admin Check", (is_admin? "Yes" : "No"));

                        Log.d(TAG, "DocumentSnapshot data: " + name);
                        User user = new User(uid, name, email, is_admin);
                        callback.onUserLoaded(user);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    /**
     * Query to add/update user data
     * @param user Object of type user that holds user data
     */
        public void addUser(com.example.collatzcheckin.attendee.User user) {
            HashMap<String, String> userData = new HashMap<>();
            userData.put("Name", user.getName());
            userData.put("Email", user.getEmail());
            userData.put("Uid", user.getUid());
            userData.put("Geo", (user.getGeolocation()).toString());
            userData.put("Notif", (user.getNotifications()).toString());
            userData.put("Pfp", user.getPfp());
            userData.put("Admin", (String.valueOf(user.isAdmin())));
            Log.d("Firestore", "DocumentSnapshot successfully written!");
            userRef.document(user.getUid())
                    .set(userData)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("Firestore", "DocumentSnapshot successfully written!");
                        }
                    });
    }

    public void EventsSignUp(String euid, String uuid) {
        userRef.document(uuid).update("Events", FieldValue.arrayUnion(euid));
    }
}
