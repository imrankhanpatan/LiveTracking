package com.toolyt.location.callback;

import android.location.Location;

public interface UserRegistrationCallback {
    public void onSuccess(String success);

    public void onFailed(String error);
}
