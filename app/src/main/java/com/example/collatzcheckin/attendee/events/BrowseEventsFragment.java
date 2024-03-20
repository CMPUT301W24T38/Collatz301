package com.example.collatzcheckin.attendee.events;

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
import android.widget.ListView;

import com.example.collatzcheckin.R;
import com.example.collatzcheckin.authentication.AnonAuthentication;
import com.example.collatzcheckin.event.Event;
import com.example.collatzcheckin.event.EventArrayAdapter;
import com.example.collatzcheckin.event.EventDB;
import com.example.collatzcheckin.event.EventView;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;


public class BrowseEventsFragment extends Fragment {

    ListView eventList;
    ArrayAdapter<Event> eventArrayAdapter;
    private final AnonAuthentication authentication = new AnonAuthentication();
    ArrayList<Event> eventDataList;
    View view;
    EventDB db;

    public BrowseEventsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_browse_events, container, false);

        db = new EventDB();
        eventList = view.findViewById(R.id.event_list_view);
        eventDataList = new ArrayList<>();
        eventArrayAdapter = new EventArrayAdapter(getActivity(), eventDataList);
        eventList.setAdapter(eventArrayAdapter);
        String uuid = authentication.identifyUser();

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
                        String eventId = doc.getId();
                        String eventOrganizer = doc.getString("Event Organizer");
                        String eventTitle = doc.getString("Event Title");
                        String eventDate = doc.getString("Event Date");
                        String eventDescription = doc.getString("Event Description");
                        String eventPoster = doc.getString("Event Poster");
                        String eventLocation = doc.getString("Event Location");
                        String memberLimit = doc.getString("Member Limit");
                        //HashMap<String, String> attendees = (HashMap<String,String>) doc.get("Attendees");
                        int parsedMemberLimit = 0; // Default value, you can change it based on your requirements

                        if (memberLimit != null && !memberLimit.isEmpty()) {
                            parsedMemberLimit = Integer.parseInt(memberLimit);
                        }
                        eventDataList.add(new Event(eventTitle, eventOrganizer, eventDate, eventDescription, eventPoster, eventLocation, parsedMemberLimit, eventId));
                    }

                    eventArrayAdapter.notifyDataSetChanged();
                }
            }
        });

        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long arg3)
            {

                Event event = (Event)adapter.getItemAtPosition(position);
                change(event);
            }
        });

        return view;
    }

    public void change(Event event) {
        Intent myIntent = new Intent(getActivity(), EventSignUp.class);
        myIntent.putExtra("com/example/collatzcheckin/event", event);
        startActivity(myIntent);
    }
}