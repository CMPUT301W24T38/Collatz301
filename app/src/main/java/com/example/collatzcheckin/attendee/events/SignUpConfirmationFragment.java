package com.example.collatzcheckin.attendee.events;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.collatzcheckin.MainActivity;
import com.example.collatzcheckin.R;
import com.example.collatzcheckin.attendee.AttendeeDB;
import com.example.collatzcheckin.attendee.AttendeeCallbackManager;
import com.example.collatzcheckin.attendee.User;
import com.example.collatzcheckin.authentication.AnonAuthentication;
import com.example.collatzcheckin.event.EventDB;
import com.example.collatzcheckin.utils.FirebaseFindUserCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

/**
 * SignUpConfirmationFragment confirms user details before the sign up
 */
public class SignUpConfirmationFragment extends DialogFragment implements FirebaseFindUserCallback {
    private static final String ARG_PARAM1 = "euid_param";
    TextView name, email;
    AnonAuthentication authentication = new AnonAuthentication();
    AttendeeDB attendeeDB = new AttendeeDB();
    EventDB eventDB = new EventDB();
    User user;
    String euid;
    AttendeeCallbackManager attendeeFirebaseManager = new AttendeeCallbackManager();
    private SignedUp listener;

    /**
     * Required empty public constructor
     */
    public SignUpConfirmationFragment() {
        // Required empty public constructor
    }

    /**
     * Called when the fragment is attached to a context.
     *
     * @param context       The context to which the fragment is attached
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof SignedUp) {
            listener = (SignedUp) context;
        } else {
            throw new RuntimeException(context + " must implement AddBookDialogListener");
        }
    }

    /**
     * Create a new instance of SignUpConfirmationFragment using the provided parameters.
     *
     * @param euid_param       This is the event ID
     * @return                 A new instance of fragment SignUpConfirmationFragment.
     */
    public static SignUpConfirmationFragment newInstance(String euid_param) {
        SignUpConfirmationFragment fragment = new SignUpConfirmationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, euid_param);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * OnCreateDialog create the dialog and implements functionality
     *
     * @param savedInstanceState       If the fragment is being re-initialized after previously
     *                                 being shut down, this Bundle contains the data it most
     *                                 recently supplied
     * @return                         The created AlertDialog that represents the sign-up confirmation dialog
     **/
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = getLayoutInflater().inflate(R.layout.fragment_sign_up_confirmation, null);
        initViews(view);
        euid = getArguments().getString(ARG_PARAM1);
        String uuid = authentication.identifyUser();
        attendeeFirebaseManager.readData(uuid, this);


        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        AlertDialog alertDialog = builder
                .setView(view)
                .setTitle("Confirm your details")
                .setNeutralButton("Cancel", null)
                .setPositiveButton("OK", ((dialog, which) -> {
                    //add event in user table
                    attendeeDB.EventsSignUp(euid, uuid);
                    //add user in event table
                    eventDB.userSignUp(euid, uuid);
                    listener.updateText();
                    subscribeToEvent(getContext(),euid);
                }))
                .create();
        alertDialog.show();
        return alertDialog;
    }
    public void subscribeToEvent(Context context, String euid){
        FirebaseMessaging.getInstance().subscribeToTopic(euid)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = context.getString(R.string.msg_subscribed);
                        if (!task.isSuccessful()) {
                            msg = context.getString(R.string.msg_subscribe_failed);
                        }
                        Log.d("Notifications",msg);
                    }
                });

    }

    /**
     * Fill the dialog with user details when is have been retrieved from Firebase
     *
     * @param user       User data that has been retrieved from Firebase
     **/
    @Override
    public void onCallback(User user) {
        if (isAdded()) { // Check if fragment is attached
            // Handle the retrieved user data
            this.user = user;
            setData(user);
        }
    }

    /**
     * Initializes data and views
     *
     * @param view           Views in the fragment
     */
    private void initViews(View view) {
        name = view.findViewById(R.id.name_text);
        email = view.findViewById(R.id.email_text);
    }

    /**
     * Set Text in Dialog Box to user details
     *
     * @param user       User details
     **/
    private void setData(User user) {
        name.setText(user.getName());
        email.setText(user.getEmail());
    }
}