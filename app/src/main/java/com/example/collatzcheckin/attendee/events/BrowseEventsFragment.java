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


/**
 * BrowseEventsFragment displays all the events that the user can sign up for
 */
public class BrowseEventsFragment extends Fragment {

    ListView eventList;
    ArrayAdapter<Event> eventArrayAdapter;
    private final AnonAuthentication authentication = new AnonAuthentication();
    ArrayList<Event> eventDataList;
    View view;
    EventDB db = new EventDB();

    /**
     * Required empty public constructor
     */
    public BrowseEventsFragment() {
        // Required empty public constructor
    }


    /**
     * Called to create the BrowseEventsFragment's Inflates the fragment layout from
     * the specified XML resource, populates the item list from the bundle arguments, and sets up UI
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in
     *                           the fragment.
     * @param container          If non-null, this is the parent view that the fragment's UI should be
     *                           attached to. The fragment should not add the view itself, but this can
     *                           be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous
     *                           saved state as given here.
     * @return The root view of the fragment's layout hierarchy.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_browse_events, container, false);
        initViews(view);
        String uuid = authentication.identifyUser();

        // gettung data to display in Listview for user to browse
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

    /**
     * Starts an activity with the view of the event details
     *
     * @param event           Event the user wants to view
     */
    public void change(Event event) {
        Intent myIntent = new Intent(getActivity(), EventSignUp.class);
        myIntent.putExtra("event", event);
        startActivity(myIntent);
    }

    /**
     * Initializes data and views
     *
     * @param view           Views in the fragment
     */
    private void initViews(View view) {
        eventList = view.findViewById(R.id.event_list_view);
        eventDataList = new ArrayList<>();
        eventArrayAdapter = new EventArrayAdapter(getActivity(), eventDataList);
        eventList.setAdapter(eventArrayAdapter);
    }
}