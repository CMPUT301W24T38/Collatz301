package com.example.collatzcheckin.attendee.events;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.collatzcheckin.R;
import com.example.collatzcheckin.attendee.AttendeeDB;
import com.example.collatzcheckin.attendee.User;
import com.example.collatzcheckin.authentication.AnonAuthentication;
import com.example.collatzcheckin.event.EventDB;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignUpConfirmationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignUpConfirmationFragment extends DialogFragment {

    private static final String ARG_PARAM1 = "euid_param";
    TextView name, email;
    AnonAuthentication authentication = new AnonAuthentication();
    AttendeeDB attendeeDB = new AttendeeDB();
    EventDB eventDB = new EventDB();
    User user;
    String euid;
    public SignUpConfirmationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment SignUpConfirmationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SignUpConfirmationFragment newInstance(String euid_param) {
        SignUpConfirmationFragment fragment = new SignUpConfirmationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, euid_param);
        fragment.setArguments(args);
        return fragment;
    }


    /**
     * OnCreateDialog create the dialog and implements functionality
     **/
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = getLayoutInflater().inflate(R.layout.fragment_sign_up_confirmation, null);
        name = view.findViewById(R.id.name_text);
        email = view.findViewById(R.id.email_text);
        euid = getArguments().getString(ARG_PARAM1);
        String uuid = authentication.identifyUser();
        getUser(uuid);


        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        AlertDialog alertDialog = builder
                .setView(view)
                .setTitle("Confirm your details")
                .setNeutralButton("Cancel", null)
                .setPositiveButton("OK", ((dialog, which) -> {
                    //add event in user table
                    attendeeDB.EventsSignUp(euid, uuid);
                    eventDB.userSignUp(euid, uuid);
                    Log.d(TAG, "message");
                    Log.d(TAG, euid);
                    //add user in event table
                }))
                .create();
        alertDialog.show();



        return alertDialog;
    }

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
                       name.setText(document.getString("Name"));
                       email.setText(document.getString("Email"));
                       user = new User(document.getString("Name"), document.getString("Email"), uuid, Boolean.parseBoolean(document.getString("Geo")), Boolean.parseBoolean(document.getString("Notif")), document.getString("Pfp"));
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