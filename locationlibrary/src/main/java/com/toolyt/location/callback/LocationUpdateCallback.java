package com.toolyt.location.callback;

import android.location.Location;

public interface LocationUpdateCallback {
    public void onLocation(Location location);
    public void onAddress(String address);
    public void onError(String error);
}
