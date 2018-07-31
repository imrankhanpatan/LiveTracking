package com.toolyt.location.Utils;

import com.google.android.gms.location.LocationRequest;

import java.util.HashMap;

public class App {
    private static App mInstance;
    private String userid = "";
    private String userName = "";
    private String companyId = "";
    private String color = "";
    private HashMap<String, String> firebaseData;
    private int AccuracyMode = com.toolyt.location.Utils.AccuracyMode.PRIORITY_HIGH_ACCURACY;

    private App() {
    }  //private constructor.

    public static App getInstance() {
        if (mInstance == null) { //if there is no instance available... create new one
            mInstance = new App();
        }

        return mInstance;
    }

    public HashMap<String, String> getFirebaseData() {
        return firebaseData;
    }

    public void setFirebaseData(HashMap<String, String> firebaseData) {
        this.firebaseData = firebaseData;
    }

    public int getAccuracyMode() {
        return AccuracyMode;
    }

    public void setAccuracyMode(int accuracyMode) {
        AccuracyMode = accuracyMode;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
