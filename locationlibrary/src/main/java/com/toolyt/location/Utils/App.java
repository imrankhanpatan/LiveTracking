package com.toolyt.location.Utils;

public class App {
    private static App mInstance;
    private String userid = "";
    private String userName = "";
    private String companyId = "";
    private String color = "";

    private App() {
    }  //private constructor.

    public static App getInstance() {
        if (mInstance == null) { //if there is no instance available... create new one
            mInstance = new App();
        }

        return mInstance;
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
