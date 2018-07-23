package com.toolyt.location.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class TLocation {

    private String time;
    private String latitude;
    private String longitude;
    private String accuracy;

    public TLocation(String time, String latitude, String longitude, String accuracy) {
        this.time = time;
        this.latitude = latitude;
        this.longitude = longitude;
        this.accuracy = accuracy;
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

    public String getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(String accuracy) {
        this.accuracy = accuracy;
    }
}
