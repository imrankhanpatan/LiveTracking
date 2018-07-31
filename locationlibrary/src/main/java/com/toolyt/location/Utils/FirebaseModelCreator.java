package com.toolyt.location.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class FirebaseModelCreator {

    /*public void storeTrackedLocation(Context context, Location location) {
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

    }*/


    public static void storeTrackedLocation(Context context, Location location) {
        try {
            String id = Utils.getDatabase().getReference().push().getKey();
            String time = Utils.convertDate("" + location.getTime(), "hh:mm:ss aa");
            String date = Utils.convertDate("" + location.getTime(), "yyyy-MM-dd");
            String address = Utils.getLocalAddress(context, location.getLatitude(), location.getLongitude());
            HashMap<String, String> userDetails = getDataFromPreferences(context);

            DatabaseReference ref = Utils.getDatabase().getReference().child(Constants.FB_TABLE_NAME)
                    .child(Constants.FB_TRACKED_LOCATION).child(Utils.getDeviceId(context)).child(date)
                    .child(id);
            ref.child("time").setValue(time);
            ref.child("date").setValue(date);
            ref.child("latitude").setValue(location.getLatitude());
            ref.child("longitude").setValue(location.getLongitude());
            ref.child("address").setValue(address);

            if (userDetails != null) {
                for (Map.Entry map : userDetails.entrySet()) {
                    ref.child(map.getKey().toString()).setValue(map.getValue());
                }
            }
        } catch (Exception e) {

        }
    }

    public static void storeStayedLocation(Context context, Location location) {
        try {


            String id = Utils.getDatabase().getReference().push().getKey();
            String time = Utils.convertDate("" + location.getTime(), "hh:mm:ss aa");
            String date = Utils.convertDate("" + location.getTime(), "yyyy-MM-dd");
            String address = Utils.getLocalAddress(context, location.getLatitude(), location.getLongitude());
            HashMap<String, String> userDetails = getDataFromPreferences(context);
            Log.d("HASH_MAP", "3: " + userDetails);
            DatabaseReference ref = Utils.getDatabase().getReference().child(Constants.FB_TABLE_NAME)
                    .child(Constants.FB_STAYED_TABLE_NAME).child(Utils.getDeviceId(context)).child(date)
                    .child(id);
            ref.child("time").setValue(time);
            ref.child("date").setValue(date);
            ref.child("latitude").setValue(location.getLatitude());
            ref.child("longitude").setValue(location.getLongitude());
            ref.child("address").setValue(address);

            if (userDetails != null) {
                for (Map.Entry map : userDetails.entrySet()) {
                    ref.child(map.getKey().toString()).setValue(map.getValue());
                }
            }
        } catch (Exception e) {

        }
    }

    private static HashMap<String, String> getDataFromPreferences(Context context) {
        try {
            //get from shared prefs
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            Gson gson = new Gson();

            String storedHashMapString = prefs.getString("USER_DETAILS", null);
            java.lang.reflect.Type type = new TypeToken<HashMap<String, String>>() {
            }.getType();
            HashMap<String, String> userDetails = gson.fromJson(storedHashMapString, type);
            Log.d("USER_DETAILS", "2: " + userDetails);
            return userDetails;
        } catch (Exception e) {

        }
        return null;
    }

}
