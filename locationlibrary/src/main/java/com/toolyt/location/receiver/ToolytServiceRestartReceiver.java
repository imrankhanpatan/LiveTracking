package com.toolyt.location.receiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;

import com.toolyt.location.callback.LocationUpdateCallback;
import com.toolyt.location.service.ToolytLocationService;

public class ToolytServiceRestartReceiver extends BroadcastReceiver {
    boolean isServiceRunning = false;

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(Intent.ACTION_TIME_TICK)) {
            ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if ("com.toolyt.location.service.ToolytLocationService".equals(service.service.getClassName())) {
                    isServiceRunning = true;
                }
            }
            if (!isServiceRunning) {
                ToolytLocationService locationService = new ToolytLocationService();
                locationService.startLocationService(context, new LocationUpdateCallback() {
                    @Override
                    public void onLocation(Location location) {

                    }

                    @Override
                    public void onAddress(String address) {

                    }

                    @Override
                    public void onError(String error) {

                    }
                });
            }
        }
    }
}