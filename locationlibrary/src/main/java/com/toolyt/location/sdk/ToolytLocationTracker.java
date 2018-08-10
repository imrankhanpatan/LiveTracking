package com.toolyt.location.sdk;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.toolyt.location.Utils.App;
import com.toolyt.location.callback.LocationUpdateCallback;
import com.toolyt.location.service.ToolytLocationService;

import java.util.List;

import static android.content.Context.ACTIVITY_SERVICE;

public class ToolytLocationTracker {

    private Activity activity;
    private static ToolytLocationTracker mInstance;
    ToolytLocationService locationService;

    public ToolytLocationTracker(Activity activity) {
        locationService = new ToolytLocationService();
        this.activity = activity;
    }


    public static ToolytLocationTracker getInstance(Activity activity) {
        if (mInstance == null) { //if there is no instance available... create new one
            mInstance = new ToolytLocationTracker(activity);
        }
        return mInstance;
    }

    public ToolytLocationTracker setAccuracyPriority(int priority) {
        App.getInstance().setAccuracyMode(priority);
        return this;
    }

    /**
     * Start live location updates
     */
    public ToolytSDKManager startTracker() {
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
                                locationService.startLocationService(activity);
                            }

                            // check for permanent denial of any permission
                            if (report.isAnyPermissionPermanentlyDenied()) {
                                // permission is denied permenantly, navigate user to app settings
                                Toast.makeText(activity, "Pls enable permission in app settings", Toast.LENGTH_SHORT).show();
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
        return new ToolytSDKManager(activity);
    }

    /**
     * stop live location updates
     */
    public void stopTracker() {
        try {
            locationService.stopLocationService(activity);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static boolean isServiceRunning(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.toolyt.location.service.ToolytLocationService"
                    .equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


}
