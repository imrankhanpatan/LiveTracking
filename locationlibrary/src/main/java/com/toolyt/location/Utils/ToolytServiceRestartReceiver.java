package com.toolyt.location.Utils;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

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
                locationService.startLocationService(context);
            }
        }
    }
}