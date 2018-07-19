package com.toolyt.location.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class FilteredLocationData {

    @NonNull
    @PrimaryKey
    private String currentTime;
    private String latitude;
    private String longitude;

    private String accuracy;


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


    @NonNull
    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(@NonNull String currentTime) {
        this.currentTime = currentTime;
    }

    public String getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(String accuracy) {
        this.accuracy = accuracy;
    }
}
