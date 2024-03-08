package com.example.collatzcheckin.attendee;

import android.net.Uri;

import com.example.collatzcheckin.Event;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {
    private String name;
    private String  pfp;
    private String username;
    private String email;
    private List<Event> events;
    private boolean notifications;
    private boolean geolocation;
    private String uid;


    public User() {
    }
    public User(String uid) {
        this.uid = uid;
    }

    public User(String uid, String name, String contactInformation) {
        this.name = name;
        this.email = contactInformation;
        this.uid = uid;
        this.events = new ArrayList<Event>();
        this.geolocation = false;
        this.notifications = false;

    }

    public User( String name, String username, String contactInformation, String uid) {
        this.name = name;
        this.email = contactInformation;
        this.uid = uid;
        this.events = new ArrayList<Event>();
        this.username = username;
        this.geolocation = false;
        this.notifications = false;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPfp() {
        return pfp;
    }

    public void setPfp(String pfp) {
        this.pfp = pfp;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void addEvent(Event event) {
        events.add(event);
    }

    public boolean isGeolocation() {
        return geolocation;
    }

    public void setGeolocation(boolean geolocation) {
        this.geolocation = geolocation;
    }

    public boolean isNotifications() {
        return notifications;
    }

    public void setNotifications(boolean notifications) {
        this.notifications = notifications;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}