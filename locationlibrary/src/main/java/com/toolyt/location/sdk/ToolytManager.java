package com.toolyt.location.sdk;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.toolyt.location.location.CurrentLocationListener;
import com.toolyt.location.location.LocationUpdateCallback;
import com.toolyt.location.database.LocationDatabase;
import com.toolyt.location.service.ToolytLocationService;

import java.util.HashMap;
import java.util.List;

import static com.toolyt.location.Utils.Constants.DATABASE_NAME;

public class ToolytManager extends Application {
    private Context appContext;
    private LocationDatabase locationDatabase;
    private boolean isUpdateStarted = false;
    private ToolytLocationService locationService;
    private CurrentLocationListener currentLocationListener;
    private Activity activity;

    public ToolytManager(Context context) {
        try {
            this.appContext = context;
            locationService = new ToolytLocationService();
        } catch (Exception e) {

        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            locationDatabase = Room.databaseBuilder(getApplicationContext(),
                    LocationDatabase.class, DATABASE_NAME)
                    .fallbackToDestructiveMigration().allowMainThreadQueries()
                    .build();

        } catch (Exception e) {

        }
    }
/*

    */
/**
     * Start live location updates
     *
     * @param activity
     *//*

    public void startLocationUpdates(final Activity activity) {
        try {
            this.activity = activity;
            Dexter.withActivity(activity)
                    .withPermissions(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport report) {
                            // check if all permissions are granted
                            if (report.areAllPermissionsGranted()) {
                                locationService.startLocationService(activity);
                            }


                            // check for permanent denial of any permission
                            if (report.isAnyPermissionPermanentlyDenied()) {
                                // permission is denied permenantly, navigate user to app settings
                                openSettings();
                            }
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                            token.continuePermissionRequest();
                        }
                    })
                    .onSameThread()
                    .check();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
*/


    /**
     * store user details in shred preferences
     *
     * @param userDetails
     */
    public void registerUser(Context context, HashMap<String, String> userDetails) {
        try {
            //convert to string using gson
            Gson gson = new Gson();
            String hashMapString = gson.toJson(userDetails);
            Log.d("USER_DEATILS", "1: " + userDetails);
            //save in shared prefs
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("USER_DETAILS", hashMapString).commit();
            Toast.makeText(context, "User registered", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {

        }
    }

    /**
     * Getting current location on user request
     *
     * @param activity
     * @param updatedLocationListener
     */

    public void getCurrentLocation(final Activity activity, final LocationUpdateCallback updatedLocationListener) {
        try {
            this.activity = activity;
            Dexter.withActivity(activity)
                    .withPermissions(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport report) {
                            // check if all permissions are granted
                            if (report.areAllPermissionsGranted()) {
                                currentLocationListener = new CurrentLocationListener(activity);
                                currentLocationListener.getLocation(updatedLocationListener);
                            }


                            // check for permanent denial of any permission
                            if (report.isAnyPermissionPermanentlyDenied()) {
                                // permission is denied permenantly, navigate user to app settings

                            }
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                            token.continuePermissionRequest();
                        }
                    })
                    .onSameThread()
                    .check();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
