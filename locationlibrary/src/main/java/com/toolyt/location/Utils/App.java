package com.toolyt.location.Utils;

public class App {
    private static App mInstance;
    private int AccuracyMode = com.toolyt.location.location.AccuracyMode.PRIORITY_HIGH_ACCURACY;

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

}
