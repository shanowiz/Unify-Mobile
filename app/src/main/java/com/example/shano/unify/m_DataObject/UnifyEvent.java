package com.example.shano.unify.m_DataObject;

/**
 * Created by shano on 4/6/2017.
 */

public class UnifyEvent {

    int eventCount;
    String id;
    String department;
    String name;
    String date;
    String imageUrl;
    String location;
    String details;
    String cost;
    String time;
    int attending=0;

    public UnifyEvent() {

        eventCount = 0;
        id = "";
        name = "";
        date = "";
        imageUrl = "";
        location = "";
        details = "";
        cost = "";
        time = "";
        attending = 0;
    }

    public int getEventCount() {return eventCount;}

    public void setEventCount(int eventCount) {this.eventCount = eventCount;}

    public String getDepartment() { return department; }

    public void setDepartment(String department) { this.department = department; }

    public int getAttending() {
        return attending;
    }

    public void setAttending(int attending) {
        this.attending = attending;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDetails() {
        return details;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
