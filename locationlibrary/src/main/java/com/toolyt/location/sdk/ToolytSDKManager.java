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
import com.toolyt.location.callback.UserRegistrationCallback;
import com.toolyt.location.location.CurrentLocationListener;
import com.toolyt.location.callback.LocationUpdateCallback;
import com.toolyt.location.database.LocationDatabase;

import java.util.HashMap;
import java.util.List;

import static com.toolyt.location.Utils.Constants.DATABASE_NAME;

public class ToolytSDKManager extends Application {
    private static Context appContext;
    private LocationDatabase locationDatabase;
    private CurrentLocationListener currentLocationListener;
    private Activity activity;

    public ToolytSDKManager(Context context) {
        try {
            this.appContext = context;
        } catch (Exception e) {

        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    public static void initialize(Context context) {
        try {
            appContext = context;
        } catch (Exception e) {

        }
    }

    /**
     * store user details in shred preferences
     *
     * @param metadata
     */
    public static void registerUser(HashMap<String, String> metadata, UserRegistrationCallback userRegistrationCallback) {
        try {
            Log.d("USER_DEATILS", "1: " + metadata);
            if (!metadata.isEmpty()) {
                //convert to string using gson
                Gson gson = new Gson();
                String hashMapString = gson.toJson(metadata);
                Log.d("USER_DEATILS", "1: " + metadata);
                //save in shared prefs
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(appContext);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("USER_DETAILS", hashMapString).commit();
                userRegistrationCallback.onSuccess("Success");
            } else {
                userRegistrationCallback.onFailed("User details are empty");
            }

        } catch (Exception e) {

        }
    }

    /**
     * Getting current location on user request
     *
     * @param activity
     * @param updatedLocationListener
     */

    public static void getCurrentLocation(final Activity activity, final LocationUpdateCallback updatedLocationListener) {
        try {
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
                                CurrentLocationListener currentLocationListener = new CurrentLocationListener(activity);
                                currentLocationListener.getLocation(updatedLocationListener);
                            }


                            // check for permanent denial of any permission
                            if (report.isAnyPermissionPermanentlyDenied()) {
                                // permission is denied permenantly, navigate user to app settings
                                updatedLocationListener.onError("Enable permissions in App settings");
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
