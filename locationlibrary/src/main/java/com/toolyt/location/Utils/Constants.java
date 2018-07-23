package com.toolyt.location.Utils;

public class Constants {


    public interface NOTIFICATION_ID {
        public static int FOREGROUND_SERVICE = 101;
    }

    public interface ACTION {
        public static String MAIN_ACTION = "com.toolyt.livetracking.mylocationservice.action.main";
        public static String STARTFOREGROUND_ACTION = "com.toolyt.livetracking.mylocationservice.action.startforeground";
        public static String STOPFOREGROUND_ACTION = "com.toolyt.livetracking.mylocationservice.action.stopforeground";
    }

    public static final String BROADCAST_DETECTED_ACTIVITY = "activity_intent";
    public static final long DETECTION_INTERVAL_IN_MILLISECONDS = 30 * 1000;
    public static final int CONFIDENCE = 70;
    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 5000;
    public static final long LOCATION_RADIUS = 150; //in meters
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 5000;
    public static final String DATABASE_NAME = "location_db";
    public static final double MIN_LOCATION_DEVIATION = 50.0;
    public static final double MAX_LOCATION_DEVIATION = 500.0;
    public static final String FB_TABLE_NAME = "ToolytLocations";

}
