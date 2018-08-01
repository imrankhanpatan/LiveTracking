package com.toolyt.location.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Location;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
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
import com.toolyt.location.R;
import com.toolyt.location.Utils.App;
import com.toolyt.location.Utils.Constants;
import com.toolyt.location.receiver.ToolytServiceRestartReceiver;
import com.toolyt.location.Utils.FirebaseModelCreator;
import com.toolyt.location.Utils.Utils;
import com.toolyt.location.database.LocationData;
import com.toolyt.location.database.LocationDatabase;
import com.toolyt.location.model.FilteredLocationData;
import com.toolyt.location.model.StayedLocation;
import com.toolyt.location.model.TStayedLocation;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ToolytLocationService extends Service {

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
    private ArrayList<Location> stayedLocations = new ArrayList<>();
    private String deviceId;
    private ToolytServiceRestartReceiver receiver;

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            intent = new Intent();
            intent.setAction(LOCATION_ACTION);
            deviceId = Utils.getDeviceId(getApplicationContext());
            startReceiver();
        } catch (Exception e) {

        }

    }

    private void startReceiver() {
        IntentFilter filter = new IntentFilter(Intent.ACTION_TIME_TICK);
        receiver = new ToolytServiceRestartReceiver();
        registerReceiver(receiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {

            if (intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION)) {
                Log.i(TAG, "Received Start Foreground Intent ");
                Toast.makeText(getApplicationContext(), "Service started", Toast.LENGTH_SHORT).show();
                init();
                createNotification();
                //startLocationUpdates();

            } else if (intent.getAction().equals(
                    Constants.ACTION.STOPFOREGROUND_ACTION)) {
                Log.i(TAG, "Received Stop Foreground Intent");
                stopLocationUpdates();
            }
        } catch (Exception e) {

        }
        return START_STICKY;
    }


    private void init() {
        try {
            //  Toast.makeText(getApplicationContext(), "In init", Toast.LENGTH_SHORT).show();
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            mSettingsClient = LocationServices.getSettingsClient(this);

            mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);

                    if (locationResult.getLastLocation().getAccuracy() <= 100) {
                        mCurrentLocation = locationResult.getLastLocation();
                        Log.d("ACCURACY_MODE", "2: " + mLocationRequest.getPriority());
                        Toast.makeText(getApplicationContext(),
                                "Lat: " + mCurrentLocation.getLatitude() + "\nLang: " + mCurrentLocation.getLongitude(),
                                Toast.LENGTH_SHORT).show();
                        updateLocationUI();
                    }

                }
            };

            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(Constants.UPDATE_INTERVAL_IN_MILLISECONDS);
            mLocationRequest.setFastestInterval(Constants.FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
            Log.d("ACCURACY_MODE", "" + App.getInstance().getAccuracyMode());
            mLocationRequest.setPriority(App.getInstance().getAccuracyMode());

            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
            builder.addLocationRequest(mLocationRequest);
            mLocationSettingsRequest = builder.build();

            locationDatabase = Room.databaseBuilder(getApplicationContext(),
                    LocationDatabase.class, Constants.DATABASE_NAME)
                    .fallbackToDestructiveMigration().allowMainThreadQueries()
                    .build();
            startLocationUpdates();
        } catch (Exception e) {

        }

    }

    /**
     * Update the UI displaying the location data
     * and toggling the buttons
     */
    private void updateLocationUI() {
        try {
            //  Toast.makeText(getApplicationContext(), "In update Loc", Toast.LENGTH_SHORT).show();
            if (mCurrentLocation != null) {
                //  Toast.makeText(getApplicationContext(), "Curr; " + mCurrentLocation.getLatitude(), Toast.LENGTH_SHORT).show();
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

        } catch (Exception e) {

        }
    }

    private void addFilteredLocationToDB() {
        try {
            filteredLocationData = new FilteredLocationData();
            filteredLocationData.setLatitude("" + mCurrentLocation.getLatitude());
            filteredLocationData.setLongitude("" + mCurrentLocation.getLongitude());
            filteredLocationData.setCurrentTime("" + Utils.getCurrentTime());
            filteredLocationData.setAccuracy("" + mCurrentLocation.getAccuracy());
            locationDatabase.daoLocation().insertFilteredLocation(filteredLocationData);
            writeNewLocation(mCurrentLocation);

            Log.d("FILTERED_LOCATION", "Lat: " + mCurrentLocation.getLatitude() + ", " + "Lng: " + mCurrentLocation.getLongitude()
                    + ", " + "Acc: " + mCurrentLocation.getAccuracy() + ", " + "Time: " + Utils.getCurrentTime());
            calculateTime(mCurrentLocation);

        } catch (Exception e) {

        }
    }

    private void calculateTime(Location location) {
        try {
            if (stayedLocations.size() == 0) {
                stayedLocations.add(location);
            } else {
                if (stayedLocations.get(0).distanceTo(location) <= Constants.LOCATION_RADIUS) {
                    stayedLocations.add(location);
                } else {
                    insertStayedLocation();
                }
            }
        } catch (Exception e) {

        }

    }

    private void insertStayedLocation() {
        try {
            if (stayedLocations.size() > 0) {
                Log.d("STAYED_LOCATIONS", "" + stayedLocations.size());
                Log.d("IDEAL_DATE", "" + stayedLocations.get(0).getTime());
                Date startDate = Utils.getTime("" + new Timestamp(stayedLocations.get(0).getTime()));
                Date endDate = Utils.getTime("" + new Timestamp(stayedLocations.get(stayedLocations.size() - 1).getTime()));

                Log.d("IDEAL_DATE", "" + startDate + "" + endDate);

                LatLng latLng = computeCentroid(stayedLocations);
                if (startDate != null && endDate != null) {
                    String duration = Utils.getSpentTime(startDate, endDate);
                    Log.d("STAYED_TIME", "" + duration);

                    StayedLocation stayedLocation = new StayedLocation();
                    stayedLocation.setTime("" + Utils.getCurrentTime());
                    stayedLocation.setLongitude("" + latLng.longitude);
                    stayedLocation.setLatitude("" + latLng.latitude);
                    stayedLocation.setDuration("" + duration);
                    locationDatabase.daoLocation().insertStayedLocation(stayedLocation);

                    String address = Utils.getLocalAddress(getApplicationContext(), latLng.latitude, latLng.longitude);
                    String id = Utils.getDatabase().getReference().push().getKey();
                    String time = Utils.convertDate("" + mCurrentLocation.getTime(), "hh:mm:ss aa");
                    String date = Utils.convertDate("" + mCurrentLocation.getTime(), "yyyy-MM-dd");
                    TStayedLocation location = new TStayedLocation(
                            "" + time,
                            "" + latLng.latitude,
                            "" + latLng.longitude,
                            "" + address,
                            "" + duration
                    );

                    stayedLocation.setAddress(address);
                    FirebaseModelCreator.storeStayedLocation(getApplicationContext(), mCurrentLocation);
                    //   Utils.getDatabase().getReference().child(Constants.FB_TABLE_NAME).child(Constants.FB_STAYED_TABLE_NAME).child(deviceId).child(date)
                    //         .child(id).setValue(location);

                    stayedLocations.clear();
                }
            }
        } catch (Exception e) {

        }

    }

    int n = 0;

    private LatLng computeCentroid(List<Location> locations) {
        try {
            ArrayList<LatLng> points = new ArrayList<>();
            for (int i = 0; i < locations.size(); i++) {
                points.add(new LatLng(locations.get(i).getLatitude(), locations.get(i).getLongitude()));
            }

            double latitude = 0;
            double longitude = 0;
            n = points.size();

            for (LatLng point : points) {
                latitude += point.latitude;
                longitude += point.longitude;
            }


        } catch (Exception e) {

        }
        return new LatLng(latitude / n, longitude / n);
    }

    /**
     * Starting location updates
     * Check whether location settings are satisfied and then
     * location updates will be requested
     */
    private void startLocationUpdates() {
        try {
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

        } catch (Exception e) {

        }
    }

    public void getCurrentLocation() {
        try {
            // Requesting ACCESS_FINE_LOCATION using Dexter library


        } catch (Exception e) {

        }
    }

    public void stopLocationUpdates() {
        try {
            stopForeground(true);
            stopSelf();
            // Removing location updates
            mFusedLocationClient
                    .removeLocationUpdates(mLocationCallback)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getApplicationContext(), "Location updates stopped!", Toast.LENGTH_SHORT).show();

                        }
                    });

        } catch (Exception e) {

        }

    }

    private void writeNewLocation(Location mCurrentLocation) {
        try {
            Log.d("TRACKED_LOC", ": " + mCurrentLocation.getLatitude());
            if (mCurrentLocation != null) {
                FirebaseModelCreator.storeTrackedLocation(getApplicationContext(), mCurrentLocation);
                // FirebaseModelCreator storage = new FirebaseModelCreator();

                /*Log.d("L_DATE", "" + date + "" + time);

                TrackedLocation location = new TrackedLocation("" + time,
                        "" + mCurrentLocation.getLatitude(),
                        "" + mCurrentLocation.getLongitude(),
                        "" + mCurrentLocation.getAccuracy(),
                        "" + mCurrentLocation.getSpeed());*/


                //insertStayedLocation();
            }
        } catch (Exception e) {

        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Create and push the notification
     */
    public void createNotification() {
        try {
            Intent notificationIntent = new Intent(this, ToolytLocationService.class);
            notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                    notificationIntent, 0);
            mBuilder = new Notification.Builder(this);
            mBuilder.setSmallIcon(R.drawable.app_icon);
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

        } catch (Exception e) {

        }
    }

    public void startLocationService(Context context) {
        try {
            Intent serviceIntent = new Intent(context, ToolytLocationService.class);
            serviceIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
            context.startService(serviceIntent);
        } catch (Exception e) {

        }

    }


    public void stopLocationService(Context context) {
        try {
            Intent stopIntent = new Intent(context, ToolytLocationService.class);
            stopIntent.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
            context.startService(stopIntent);
        } catch (Exception e) {

        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

}