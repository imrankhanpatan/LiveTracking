package com.toolyt.location.sdk;

import android.Manifest;
import android.app.Activity;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.toolyt.location.Utils.App;
import com.toolyt.location.service.ToolytLocationService;

import java.util.List;

public class ToolytLocationTracker {

    private Activity activity;
    ToolytLocationService locationService;

    public ToolytLocationTracker(Activity activity) {
        this.activity = activity;
        locationService = new ToolytLocationService();
    }

    public ToolytLocationTracker setAccuracy(int priority) {
        App.getInstance().setAccuracyMode(priority);
        return this;
    }

    /**
     * Start live location updates
     */
    public ToolytManager startTracker() {
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
        return new ToolytManager(activity);
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

}
