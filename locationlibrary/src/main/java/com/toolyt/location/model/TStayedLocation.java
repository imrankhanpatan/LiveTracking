package com.toolyt.location.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class TStayedLocation {

    private String time;
    private String latitude;
    private String longitude;
    private String address;
    private String duration;

    public TStayedLocation(String time, String latitude, String longitude, String address, String duration) {
        this.time = time;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.duration = duration;
    }


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}

