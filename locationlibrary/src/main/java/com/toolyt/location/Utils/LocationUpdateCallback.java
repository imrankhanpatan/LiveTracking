package com.toolyt.location.Utils;

import android.location.Location;

public interface LocationUpdateCallback {
    public void onLocation(Location location);
    public void onAddress(String address);
    public void onFailed(String error);
}
