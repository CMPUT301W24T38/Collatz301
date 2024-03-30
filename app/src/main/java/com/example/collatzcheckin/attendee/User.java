package com.example.collatzcheckin.attendee;

import android.graphics.Bitmap;

import com.example.collatzcheckin.event.Event;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * User holds user information
 */
public class User implements Serializable {
    private String name;
    private String pfp;
    private String genpfp;
    private String email;
    private List<Event> events;
    private List<String> attendingEvents;
    private List<String> organizingEvents;
    private boolean notifications;
    private boolean geolocation;
    private boolean isOrganizer = false;
    private String uid;
    private boolean is_admin = false;


    /**
     * This constructs a  user class where atrributes are not set
     * This is used for the Firebase Authentication
     */
    public User() {
    }

    /**
     * This constructs a user class where unique identifier is set
     * This is used for the Firebase Authentication
     * @param uid The unique identifier for this user to reference in firestore to find their
     *              item collection
     */
    public User(String uid) {
        this.uid = uid;
    }

    /**
     * This constructs a user class
     * @param uid The unique identifier for this user to reference in firestore to find their
     *             item collection
     * @param name name of user
     * @param contactInformation user email
     */
    public User(String uid, String name, String contactInformation, String genpfp) {
        this.name = name;
        this.email = contactInformation;
        this.uid = uid;
        this.events = new ArrayList<Event>();
        this.organizingEvents = new ArrayList<String>();
        this.attendingEvents = new ArrayList<String>();
        this.geolocation = false;
        this.notifications = false;
        this.pfp = "generated";
        this.genpfp = genpfp;
        this.is_admin = false;
    }
    /**
     * This constructs a user class
     * @param uid The unique identifier for this user to reference in firestore to find their
     *             item collection
     * @param name name of user
     * @param contactInformation user email
     * @param admin does user have administrator permissions
     */
    public User(String uid, String name, String contactInformation, boolean admin) {
        this.name = name;
        this.email = contactInformation;
        this.uid = uid;
        this.events = new ArrayList<Event>();
        this.organizingEvents = new ArrayList<String>();
        this.attendingEvents = new ArrayList<String>();
        this.geolocation = false;
        this.notifications = false;
        this.is_admin = admin; /// Need to add pfp stuff
    }

    /**
     * This constructs a user class
     * @param name name of user
     * @param contactInformation user email
     * @param uid The unique identifier for this user to reference in firestore to find their
     *              item collection
     */
    public User(String uid, String name, String contactInformation) {
        this.name = name;
        this.email = contactInformation;
        this.uid = uid;
        this.events = new ArrayList<Event>();
        this.organizingEvents = new ArrayList<String>();
        this.attendingEvents = new ArrayList<String>();
        this.geolocation = false;
        this.notifications = false;
        this.pfp = "generated";
        this.genpfp = "generated";
    }

    /**
     * This constructs a user class
     * @param name name of user
     * @param contactInformation user email
     * @param uid The unique identifier for this user to reference in firestore to find their
     *              item collection
     * @param geolocation Geolocation perferences ('true' for enabled, 'false' for disabled)
     * @param notifications Notifications perferences ('true' for enabled, 'false' for disabled)
     */
    public User(String name, String contactInformation, String uid, boolean geolocation, boolean notifications) {
        this.name = name;
        this.email = contactInformation;
        this.uid = uid;
        this.events = new ArrayList<Event>();
        this.organizingEvents = new ArrayList<String>();
        this.attendingEvents = new ArrayList<String>();
        this.geolocation = geolocation;
        this.notifications = notifications;
        this.pfp = "generated";
        this.genpfp = "generated";
    }

    public User(String name, String contactInformation, String uid, boolean geolocation, boolean notifications, String pfp, String genpfp) {
        this.name = name;
        this.email = contactInformation;
        this.uid = uid;
        this.events = new ArrayList<Event>();
        this.organizingEvents = new ArrayList<String>();
        this.attendingEvents = new ArrayList<String>();
        this.geolocation = geolocation;
        this.notifications = notifications;
        this.pfp = pfp;
        this.genpfp = genpfp;
    }

    /**
     * Getter for name
     * @return users preferred name
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for name
     * @param name users preferred name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for profile picture url
     * @return profile picture url
     */
    public String getPfp() {
        return pfp;
    }

    /**
     * Setter for profile picture url
     * @param pfp profile picture url
     */
    public void setPfp(String pfp) {
        this.pfp = pfp;
    }

    /**
     * Getter for user email
     * @return email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Setter for user email
     * @param email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Getter for a list of events that the user has signed up to attend
     * @return email
     */
    public List<Event> getEvents() {
        return events;
    }

    /**
     * Getter for a list of events that the user has signed up to attend
     * @return email
     */
    public List<String> getAttendingEvents() {
        return this.attendingEvents;
    }
    public List<String> getOrganizingEvents() {
        return this.organizingEvents;
    }
    /**
     * Add an event to the list of events that the user is attending
     * @param event event the user will be attending
     */
    public void addOrganizingEvent(Event event) {

        organizingEvents.add(event.getEventTitle());
    }
    public void AddAttendingEvent(Event event) {
        attendingEvents.add(event.getEventTitle());}
    /**
     * Add an event to the list of events that the user is attending
     * @param event event the user will be attending
     */
    public void addEvent(Event event) {
        events.add(event);
    }

    /**
     * Getter for if user has geolocation enabled
     * @return boolean value represents if geolocation is enabled or disabled
     */
    public boolean isGeolocation() {
        return geolocation;
    }

    /**
     * Convert boolean value to string equivalent
     * @return string equivalent of geolocation perferences
     */
    public Boolean getGeolocation() {
        if(geolocation) {
            return true;
        } else {
            return false;
        }
    }

    public void setGeolocation(boolean geolocation) {
        this.geolocation = geolocation;
    }

    /**
     * Getter for if user has notifications enabled
     * @return boolean value represents if notifications is enabled or disabled
     */
    public boolean isNotifications() {
        return notifications;
    }

    /**
     * Convert boolean value to string equivalent
     * @return string equivalent of notification perferences
     */
    public Boolean getNotifications() {
        if(notifications) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Setter for user to change notifcation perferences
     * @param notifications boolean value represents if notifications is enabled or disabled
     */
    public void setNotifications(boolean notifications) {
        this.notifications = notifications;
    }

    /**
     * Getter for unique idenifier generated by Firebase Authenicator which is then used to query for data
     * @return unique idenifier
     */
    public String getUid() {
        return uid;
    }

    /**
     * Setter for unique idenifier generated by Firebase Authenicator which is then used to query for data
     * @param uid unique idenifier
     */
    public void setUid(String uid) {
        this.uid = uid;
    }
    public boolean isOrganizer() {
        return isOrganizer;
    }

    public void setOrganizer(boolean organizer) {
        isOrganizer = organizer;
    }

    public String getGenpfp() {
        return genpfp;
    }

    public void setGenpfp(String genpfp) {
        this.genpfp = genpfp;
    }

    /**
     * Getter for if user is an administrator
     * @return boolean value represents if user is an administrator or not
     */
    public boolean isAdmin() {
        return is_admin;
    }
    /**
     * Setter for admin permissions
     * @param is_admin boolean to distinguish users from administrators
     */
    public void setAdmin(boolean is_admin) {
        this.is_admin = is_admin;
    }

}