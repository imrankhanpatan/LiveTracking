package com.toolyt.location.Utils;

import com.toolyt.location.callback.LocationUpdateCallback;

public class App {
    private static App mInstance;
    private int AccuracyMode = com.toolyt.location.location.AccuracyMode.PRIORITY_HIGH_ACCURACY;
    private LocationUpdateCallback locationUpdateCallback;

    private App() {
    }  //private constructor.

    public static App getInstance() {
        if (mInstance == null) { //if there is no instance available... create new one
            mInstance = new App();
        }

        return mInstance;
    }

    public int getAccuracyMode() {
        return AccuracyMode;
    }

    public void setAccuracyMode(int accuracyMode) {
        AccuracyMode = accuracyMode;
    }

    public LocationUpdateCallback getLocationUpdateCallback() {
        return locationUpdateCallback;
    }

    public void setLocationUpdateCallback(LocationUpdateCallback locationUpdateCallback) {
        this.locationUpdateCallback = locationUpdateCallback;
    }
}
