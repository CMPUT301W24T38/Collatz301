package com.example.collatzcheckin;


public class Event {
    private String eventTitle;
    private String eventID;
    private String eventOrganizer;
    private String eventDate;
    private String eventDescription;
    private String eventPoster;
    private String eventLocation;
    private int memberLimit;
    public Event(String eventID, String eventTitle, String eventOrganizer, String eventDate, String eventDescription, String eventPoster, String eventLocation, int memberLimit) {
        this.eventTitle = eventTitle;
        this.eventOrganizer = eventOrganizer;
        this.eventDate = eventDate;
        this.eventDescription = eventDescription;
        this.eventPoster = eventPoster;
        this.eventLocation = eventLocation;
        this.memberLimit = memberLimit;
        this.eventID = eventID;
    }
    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public String getEventOrganizer() {
        return eventOrganizer;
    }

    public void setEventOrganizer(String eventOrganizer) {
        this.eventOrganizer = eventOrganizer;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public String getEventPoster() {
        return eventPoster;
    }

    public void setEventPoster(String eventPoster) {
        this.eventPoster = eventPoster;
    }

    public int getMemberLimit() {
        return memberLimit;
    }

    public void setMemberLimit(int memberLimit) {
        this.memberLimit = memberLimit;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }
    public String getId() {
        return eventID;
    }

    public void setId(String eventID) {
        this.eventID = eventID;
    }
}
