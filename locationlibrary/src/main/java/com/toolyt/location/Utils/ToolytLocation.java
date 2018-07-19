package com.toolyt.location.Utils;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.toolyt.location.service.MyLocationService;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

public class ToolytLocation extends Application {

    private static ToolytLocation mInstance;
    private Context appContext;

    private ToolytLocation(Context context) {
        this.appContext = context;
    }

    public static ToolytLocation getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new ToolytLocation(context);
        }
        return mInstance;
    }


    public void startLocationUpdates(Activity activity) {
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
                            appContext.startService(new Intent(appContext, MyLocationService.class));
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


    }

    public void stopLocationUpdates() {
        appContext.stopService(new Intent(appContext, MyLocationService.class));
    }


}
