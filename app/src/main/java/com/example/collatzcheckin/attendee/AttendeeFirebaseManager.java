package com.example.collatzcheckin.attendee;

import com.example.collatzcheckin.utils.FirebaseUserCallback;

public class AttendeeFirebaseManager {
    private final AttendeeDB attendeeDB = new AttendeeDB();
    public void readData(String uuid, FirebaseUserCallback callback) {
        attendeeDB.findUser(uuid, new FirebaseUserCallback() {
            @Override
            public void onCallback(User user) {
                callback.onCallback(user);
            }
        });
    }

    public void readPfp(String uuid, FirebaseUserCallback callback) {
        attendeeDB.findUser(uuid, new FirebaseUserCallback() {
            @Override
            public void onCallback(User user) {
                callback.onCallback(user);
            }
        });
    }
}
