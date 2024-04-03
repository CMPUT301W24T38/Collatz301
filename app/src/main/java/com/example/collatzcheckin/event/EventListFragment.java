package com.example.collatzcheckin.event;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;

import com.example.collatzcheckin.R;
import com.example.collatzcheckin.attendee.AttendeeDB;
import com.example.collatzcheckin.authentication.AnonAuthentication;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class EventListFragment extends Fragment {

    View view;
    ListView eventList;
    ArrayAdapter<Event> eventArrayAdapter;
    ArrayList<Event> eventDataList;
    RadioButton organizerButton;
    RadioButton attendeeButton;
    EventDB db;
    private final AnonAuthentication authentication = new AnonAuthentication();


    public EventListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.event_view_organizer, container, false);
        organizerButton = view.findViewById(R.id.organizer_button);
        attendeeButton  = view.findViewById(R.id.attendee_button);
        String uuid = authentication.identifyUser();


        organizerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getOrganizedEvents(uuid);
            }
        });

        attendeeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               getAttendeeEvent(uuid);
            }
        });


        db = new EventDB();
        eventList = view.findViewById(R.id.event_list_view);
        eventDataList = new ArrayList<>();
        eventArrayAdapter = new EventArrayAdapter(getContext(), eventDataList);
        eventList.setAdapter(eventArrayAdapter);
        getAttendeeEvent(uuid);

        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long arg3)
            {

                Event event = (Event)adapter.getItemAtPosition(position);
                change(event);
            }
        });


        Button createEventButton = view.findViewById(R.id.create_event);
        createEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                changeCreateEvent(uuid);
            }
        });

        return view;
    }


    private void getOrganizedEvents(String uuid) {
        db = new EventDB();
        db.eventRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore", error.toString());
                    return;
                }
                if (querySnapshots != null) {
                    eventDataList.clear();

                    for (QueryDocumentSnapshot doc : querySnapshots) {
                        String organizer = doc.getString("Event Organizer");
                        if (organizer.matches(uuid)) {
                            String eventId = doc.getId();
                            String eventOrganizer = doc.getString("Event Organizer");
                            String eventTitle = doc.getString("Event Title");
                            String eventDate = doc.getString("Event Date");
                            String eventDescription = doc.getString("Event Description");
                            String eventPoster = doc.getString("Event Poster");
                            String eventLocation = doc.getString("Event Location");
                            String memberLimit = doc.getString("Member Limit");
                            HashMap<String, String> attendees = (HashMap<String,String>) doc.get("Attendees");
                            int parsedMemberLimit = 0; // Default value, you can change it based on your requirements

                            if (memberLimit != null && !memberLimit.isEmpty()) {
                                parsedMemberLimit = Integer.parseInt(memberLimit);
                            }
                            eventDataList.add(new Event(eventTitle, eventOrganizer, eventDate, eventDescription, eventPoster, eventLocation, parsedMemberLimit, eventId, attendees));
                        }
                    }

                    eventArrayAdapter.notifyDataSetChanged();
                }
            }
        });
    }



    private void getAttendeeEvent(String uuid) {
        EventDB eventDb = new EventDB();
        AttendeeDB attendeeDB = new AttendeeDB();
        attendeeDB.userRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore", error.toString());
                    return;
                }
                if (querySnapshots != null) {
                    eventDataList.clear();

                    for (QueryDocumentSnapshot doc : querySnapshots) {

                        if (doc.getId().equals(uuid)) {
                            List<String> eventIds = (List<String>) doc.get("Events");

                            db.eventRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                                    if (error != null) {
                                        Log.e("Firestore", error.toString());
                                        return;
                                    }
                                    if (querySnapshots != null) {
                                        for (QueryDocumentSnapshot doc : querySnapshots) {
                                            if(eventIds != null) {
                                                if (eventIds.contains(doc.getId())) {
                                                    String eventId = doc.getId();
                                                    String eventOrganizer = doc.getString("Event Organizer");
                                                    String eventTitle = doc.getString("Event Title");
                                                    String eventDate = doc.getString("Event Date");
                                                    String eventDescription = doc.getString("Event Description");
                                                    String eventPoster = doc.getString("Event Poster");
                                                    String eventLocation = doc.getString("Event Location");
                                                    String memberLimit = doc.getString("Member Limit");
                                                    HashMap<String, String> attendees = (HashMap<String,String>) doc.get("Attendees");
                                                    int parsedMemberLimit = 0; // Default value, you can change it based on your requirements

                                                    if (memberLimit != null && !memberLimit.isEmpty()) {
                                                        parsedMemberLimit = Integer.parseInt(memberLimit);
                                                    }
                                                    eventDataList.add(new Event(eventTitle, eventOrganizer, eventDate, eventDescription, eventPoster, eventLocation, parsedMemberLimit, eventId, attendees));
                                                }
                                                eventArrayAdapter.notifyDataSetChanged();
                                            }

                                        }
                                    }
                                }
                            });
                        }
                    }

                }
            }
        });
    }


    public void change(Event event) {
        Intent myIntent = new Intent(getContext(), EventView.class);
        myIntent.putExtra("event", event);
        startActivity(myIntent);
    }

    public void changeCreateEvent(String uuid) {
        Intent myIntent = new Intent(getContext(), CreateEvent.class);
        myIntent.putExtra("uuid", uuid);
        startActivity(myIntent);
    }
}