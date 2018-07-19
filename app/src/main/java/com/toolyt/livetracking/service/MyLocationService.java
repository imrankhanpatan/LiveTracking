package com.toolyt.livetracking.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.arch.persistence.room.Room;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Location;
import android.os.Binder;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.toolyt.livetracking.R;
import com.toolyt.livetracking.Utils.Constants;
import com.toolyt.livetracking.Utils.Utils;
import com.toolyt.livetracking.activity.MainActivity;
import com.toolyt.livetracking.database.LocationData;
import com.toolyt.livetracking.database.LocationDatabase;
import com.toolyt.livetracking.model.FilteredLocationData;
import com.toolyt.livetracking.model.StayedLocation;
import com.toolyt.livetracking.model.UserActivity;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MyLocationService extends Service {

    public final static String LOCATION_ACTION = "LOCATION_ACTION";
    private static final String TAG = "MY_LOCATION";
    private LocationDatabase locationDatabase;
    private LocationData locationData;
    private FilteredLocationData filteredLocationData;
    private NotificationManager mNotificationManager;
    private Notification.Builder mBuilder;
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;
    private Intent intent;
    private double latitude = 0.0;
    private double longitude = 0.0;
    private Location prevLocation = null;
    private Location pLocation = null;
    private Intent mIntentService;
    private PendingIntent mPendingIntent;
    private ActivityRecognitionClient mActivityRecognitionClient;
    private BroadcastReceiver broadcastReceiver;
    private String activity = "unknown";
    private ArrayList<Location> stayedLocations = new ArrayList<>();
    private UserActivity userActivity;

    @Override
    public void onCreate() {
        super.onCreate();
        intent = new Intent();
        intent.setAction(LOCATION_ACTION);
        mActivityRecognitionClient = new ActivityRecognitionClient(this);
        mIntentService = new Intent(this, DetectedActivitiesIntentService.class);
        mPendingIntent = PendingIntent.getService(this, 1, mIntentService, PendingIntent.FLAG_UPDATE_CURRENT);
        requestActivityUpdatesButtonHandler();
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Constants.BROADCAST_DETECTED_ACTIVITY)) {
                    int type = intent.getIntExtra("type", -1);
                    int confidence = intent.getIntExtra("confidence", 0);
                    handleUserActivity(type, confidence);
                }
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter(Constants.BROADCAST_DETECTED_ACTIVITY));
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            if (intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION)) {
                Log.i(TAG, "Received Start Foreground Intent ");
                createNotification();
                init();
                getCurrentLocation();
            }
        } catch (Exception e) {

        }
        return START_STICKY;
    }

    /**
     * Create and push the notification
     */
    public void createNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);
        mBuilder = new Notification.Builder(this);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setContentTitle("Searching for GPS")
                .setContentText("Toolyt")
                .setAutoCancel(false)
                .setOngoing(true)
                .setContentIntent(pendingIntent);

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(Constants.NOTIFICATION_CHANNEL_ID, "Live Tracker Notifications", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setShowBadge(true);
            assert mNotificationManager != null;
            mBuilder.setChannelId(Constants.NOTIFICATION_CHANNEL_ID);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        assert mNotificationManager != null;
        startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, mBuilder.build());

    }

    private void init() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                if (locationResult.getLastLocation().getAccuracy() <= 100) {
                    mCurrentLocation = locationResult.getLastLocation();
                    updateLocationUI();
                }


// Log.d("SPEED", "" + mCurrentLocation.getSpeed());

            }
        };

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(Constants.UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(Constants.FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();

        locationDatabase = Room.databaseBuilder(getApplicationContext(),
                LocationDatabase.class, Constants.DATABASE_NAME)
                .fallbackToDestructiveMigration().allowMainThreadQueries()
                .build();
    }

    /**
     * Update the UI displaying the location data
     * and toggling the buttons
     */
    private void updateLocationUI() {
        if (mCurrentLocation != null) {

            Log.d(TAG, "Lat: " + mCurrentLocation.getLatitude() + ", " + "Lng: " + mCurrentLocation.getLongitude()
                    + ", " + "Acc: " + mCurrentLocation.getAccuracy() + ", " + "Time: " + Utils.getCurrentTime());
            latitude = mCurrentLocation.getLatitude();
            longitude = mCurrentLocation.getLongitude();
            // Toast.makeText(getApplicationContext(), "SPEED: " + mCurrentLocation.hasSpeed(), Toast.LENGTH_SHORT).show();

            locationData = new LocationData();
            locationData.setLatitude("" + latitude);
            locationData.setLongitude("" + longitude);
            locationData.setTime("" + Utils.getCurrentTime());
            locationData.setAccuracy("" + mCurrentLocation.getAccuracy());

            if (prevLocation == null) {
                prevLocation = mCurrentLocation;
                addFilteredLocationToDB();
            }

            if (pLocation == null) {
                pLocation = mCurrentLocation;
            }

            double dis = pLocation.distanceTo(mCurrentLocation);

            locationData.setDiff("" + dis);
            Log.d("LOCATION_DIFFERENCE", "" + prevLocation.distanceTo(mCurrentLocation));
            locationDatabase.daoLocation().insertLocation(locationData);

//            pLocation = mCurrentLocation;

            if (prevLocation.distanceTo(mCurrentLocation) >= Constants.MIN_LOCATION_DEVIATION) {
                addFilteredLocationToDB();
                prevLocation = mCurrentLocation;
            }
            sendBroadcast(intent);
        }
    }

    private void addFilteredLocationToDB() {
        filteredLocationData = new FilteredLocationData();
        filteredLocationData.setLatitude("" + latitude);
        filteredLocationData.setLongitude("" + longitude);
        filteredLocationData.setCurrentTime("" + Utils.getCurrentTime());
        filteredLocationData.setAccuracy("" + mCurrentLocation.getAccuracy());
        locationDatabase.daoLocation().insertFilteredLocation(filteredLocationData);
        Log.d("FILTERED_LOCATION", "Lat: " + mCurrentLocation.getLatitude() + ", " + "Lng: " + mCurrentLocation.getLongitude()
                + ", " + "Acc: " + mCurrentLocation.getAccuracy() + ", " + "Time: " + Utils.getCurrentTime());
        calculateTime(mCurrentLocation);
    }

    private void calculateTime(Location location) {
        if (stayedLocations.size() == 0) {
            stayedLocations.add(location);
        } else {
            if (stayedLocations.get(0).distanceTo(location) <= Constants.LOCATION_RADIUS) {
                stayedLocations.add(location);
            } else {
                insertStayedLocation();
            }
        }
    }

    private void insertStayedLocation() {
        if (stayedLocations.size() > 0) {
            Log.d("STAYED_LOCATIONS", "" + stayedLocations.size());
            Log.d("IDEAL_DATE", "" + stayedLocations.get(0).getTime());
            Date startDate = Utils.getTime("" + new Timestamp(stayedLocations.get(0).getTime()));
            Date endDate = Utils.getTime("" + new Timestamp(stayedLocations.get(stayedLocations.size() - 1).getTime()));

            Log.d("IDEAL_DATE", "" + startDate + "" + endDate);

            LatLng latLng = computeCentroid(stayedLocations);
            if (startDate != null && endDate != null) {
                String time = Utils.getSpentTime(startDate, endDate);
                Log.d("STAYED_TIME", "" + time);

                StayedLocation stayedLocation = new StayedLocation();
                stayedLocation.setTime("" + Utils.getCurrentTime());
                stayedLocation.setLongitude("" + latLng.longitude);
                stayedLocation.setLatitude("" + latLng.latitude);
                stayedLocation.setDuration("" + time);
                locationDatabase.daoLocation().insertStayedLocation(stayedLocation);
                stayedLocations.clear();
            }

        }

    }

    private LatLng computeCentroid(List<Location> locations) {
        ArrayList<LatLng> points = new ArrayList<>();
        for (int i = 0; i < locations.size(); i++) {
            points.add(new LatLng(locations.get(i).getLatitude(), locations.get(i).getLongitude()));
        }

        double latitude = 0;
        double longitude = 0;
        int n = points.size();

        for (LatLng point : points) {
            latitude += point.latitude;
            longitude += point.longitude;
        }

        return new LatLng(latitude / n, longitude / n);
    }

    /**
     * Starting location updates
     * Check whether location settings are satisfied and then
     * location updates will be requested
     */
    private void startLocationUpdates() {

        mSettingsClient
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i(TAG, "All location settings are satisfied.");

                        Toast.makeText(getApplicationContext(), "Started location updates!", Toast.LENGTH_SHORT).show();

                        //noinspection MissingPermission
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());

                        updateLocationUI();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                Toast.makeText(getApplicationContext(), "Please enable GPS", Toast.LENGTH_LONG).show();
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);

                                Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
                        }

                        updateLocationUI();
                    }
                });
    }

    public void getCurrentLocation() {
        // Requesting ACCESS_FINE_LOCATION using Dexter library
        startLocationUpdates();
    }

    public void stopLocationUpdates() {
        // Removing location updates
        mFusedLocationClient
                .removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(), "Location updates stopped!", Toast.LENGTH_SHORT).show();

                    }
                });
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void requestActivityUpdatesButtonHandler() {
        Task<Void> task = mActivityRecognitionClient.requestActivityUpdates(
                Constants.DETECTION_INTERVAL_IN_MILLISECONDS,
                mPendingIntent);

        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void result) {
                Toast.makeText(getApplicationContext(),
                        "Successfully requested activity updates" + result,
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),
                        "Requesting activity updates failed to start",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    public void removeActivityUpdatesButtonHandler() {
        Task<Void> task = mActivityRecognitionClient.removeActivityUpdates(
                mPendingIntent);
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void result) {
                Toast.makeText(getApplicationContext(),
                        "Removed activity updates successfully!",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Failed to remove activity updates!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeActivityUpdatesButtonHandler();
    }

    private void handleUserActivity(int type, int confidence) {
        String label = "unknown";
        activity = label;
        switch (type) {
            case DetectedActivity.IN_VEHICLE: {
                label = "invehicle";
                break;
            }
            case DetectedActivity.ON_BICYCLE: {
                label = "on bicycle";
                break;
            }
            case DetectedActivity.ON_FOOT: {
                label = "onfoot";
                break;
            }
            case DetectedActivity.RUNNING: {
                label = "running";
                break;
            }
            case DetectedActivity.STILL: {
                label = "still";
                break;
            }
            case DetectedActivity.TILTING: {
                label = "tilting";
                break;
            }
            case DetectedActivity.WALKING: {
                label = "walking";
                break;
            }
            case DetectedActivity.UNKNOWN: {
                label = "unknown";
                break;
            }
        }

        activity = label;
        if (confidence >= Constants.CONFIDENCE && mCurrentLocation != null) {
            String res = "User activity: " + label + ", Confidence: " + confidence;
            Log.e(TAG, res);

            if (activityType == null) {
                activityType = activity;
            }

            if (previousActivity == null) {
                previousActivity = activityType;
            }

            if (activityType.equalsIgnoreCase("walking")) {
                Log.d("COMPARE_ACTIVITY: 0", " " + activityType + "  " + previousActivity);
                if (!previousActivity.equals(activityType)) {
                    stateId++;
                }
                previousActivity = activityType;
            } else if (activityType.equalsIgnoreCase("invehicle")) {
                Log.d("COMPARE_ACTIVITY: 1", " " + activityType + "  " + previousActivity);

                if (!previousActivity.equals(activityType)) {
                    stateId++;
                }
                previousActivity = activityType;
            } else if (activityType.equalsIgnoreCase("still")) {
                Log.d("COMPARE_ACTIVITY: 2", " " + activityType + "  " + previousActivity);
                if (!previousActivity.equals(activityType)) {
                    stateId++;
                }
                previousActivity = activityType;
            }

            userActivity = new UserActivity();
            userActivity.setActivity(activityType);
            userActivity.setLatitude("" + mCurrentLocation.getLatitude());
            userActivity.setLongitude("" + mCurrentLocation.getLongitude());
            userActivity.setTime(Utils.getCurrentTime());
            userActivity.setStateId(stateId);
            locationDatabase.daoLocation().insertActivityData(userActivity);
        }

    }

    private long stateId = 0;
    private String previousActivity = null;
    private String activityType = null;

}