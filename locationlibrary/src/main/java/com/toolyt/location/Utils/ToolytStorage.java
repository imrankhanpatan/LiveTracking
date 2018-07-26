package com.toolyt.location.Utils;

import android.content.Context;
import android.location.Location;

import com.google.firebase.database.DatabaseReference;

public class ToolytStorage {

    public void storeTrackedLocation(Context context, Location location) {
        String id = Utils.getDatabase().getReference().push().getKey();
        String time = Utils.convertDate("" + location.getTime(), "hh:mm:ss aa");
        String date = Utils.convertDate("" + location.getTime(), "yyyy-MM-dd");
        DatabaseReference ref = Utils.getDatabase().getReference().child(Constants.FB_TABLE_NAME)
                .child(Constants.FB_TRACKED_LOCATION).child(Utils.getDeviceId(context)).child(date)
                .child(id);
        ref.child("color").setValue(App.getInstance().getColor());
        ref.child("company_id").setValue(App.getInstance().getCompanyId());
        ref.child("date").setValue(date);
        ref.child("details").setValue(null);
        ref.child("latitude").setValue(location.getLatitude());
        ref.child("longitude").setValue(location.getLongitude());
        ref.child("time").setValue(time);
        ref.child("total_distance").setValue(0);
        ref.child("user_id").setValue(App.getInstance().getUserid());
        ref.child("user_name").setValue(App.getInstance().getUserName());

    }
}
