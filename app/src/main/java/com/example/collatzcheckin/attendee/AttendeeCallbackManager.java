package com.example.collatzcheckin.attendee;

import com.example.collatzcheckin.utils.FirebaseFindUserCallback;
import com.example.collatzcheckin.utils.SignInUserCallback;

public class AttendeeCallbackManager {
    private final AttendeeDB attendeeDB = new AttendeeDB();
    public void readData(String uuid, FirebaseFindUserCallback callback) {
        attendeeDB.findUser(uuid, new FirebaseFindUserCallback() {
            @Override
            public void onCallback(User user) {
                callback.onCallback(user);
            }
        });
    }

    public void readPfp(String uuid, FirebaseFindUserCallback callback) {
        attendeeDB.findUser(uuid, new FirebaseFindUserCallback() {
            @Override
            public void onCallback(User user) {
                callback.onCallback(user);
            }
        });
    }

    public void userCheck(String uuid, SignInUserCallback callback) {
        attendeeDB.isValidUser(uuid, new SignInUserCallback() {
            @Override
            public void onCallback(boolean exists) {
                callback.onCallback(exists);
            }

        });
    }
}
