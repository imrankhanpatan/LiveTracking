package com.toolyt.location.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "RunningMode")
public class RunningMode {

    @NonNull
    @PrimaryKey
    private String time;
    private String latitude;
    private String longitude;
    private String state;


    @NonNull
    public String getTime() {
        return time;
    }

    public void setTime(@NonNull String time) {
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
