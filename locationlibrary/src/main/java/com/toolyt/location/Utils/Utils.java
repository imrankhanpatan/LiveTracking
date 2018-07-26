package com.toolyt.location.Utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Utils {

    private static FirebaseDatabase mDatabase;

    public static FirebaseDatabase getDatabase() {
        try {
            if (mDatabase == null) {
                mDatabase = FirebaseDatabase.getInstance();
                mDatabase.setPersistenceEnabled(true);
            }
        } catch (Exception e) {

        }

        return mDatabase;
    }


    public static String getCurrentTime() {
        String strDate = null;
        try {
       /* int time = (int) (System.currentTimeMillis());
        Timestamp tsTemp = new Timestamp(time);
        String currentTime = tsTemp.toString();*/
            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");//dd/MM/yyyy
            Date now = new Date();
            strDate = sdfDate.format(now);
        } catch (Exception e) {

        }

        return strDate;
    }

    public static Double getDistanceInKMS(Double dis) {
        try {
            String dist = "" + dis;
            String[] parts = dist.split("\\.");
            String part1 = parts[0];
            dis = Double.valueOf(part1) / 1000;
            return dis;
        } catch (Exception e) {

        }
        return 0.0;
    }

    public static Date getTime(String timeStamp) {

        try {
            Date testDate = null;
            try {
                if (timeStamp != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    testDate = sdf.parse(timeStamp);
                }
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Log.d("DATE", "Milliseconds==" + testDate.getTime());
            return testDate;
        } catch (Exception e) {

        }

        return null;
    }

    public static String getTimeFromMillis(long millis) {
        String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
        System.out.println(hms);
        return hms;
    }

    public static String getSpentTime(Date startTime, Date endTime) {
        try {
            long millis = endTime.getTime() - startTime.getTime();

            /*int hours = (int) (mills / (1000 * 60 * 60));
            int mins = (int) ((mills / (1000 * 60)) % 60);
            Log.d("SPENT TIME: ", "" + hours + ":" + mins);
            return hours + "h:" + mins + "m";*/
            String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                    TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                    TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
            System.out.println(hms);
            return hms;
        } catch (Exception e) {

        }
        return null;
    }

    public static String convertDate(String dateInMilliseconds, String dateFormat) {
        return DateFormat.format(dateFormat, Long.parseLong(dateInMilliseconds)).toString();
    }

    public static String getLocalAddress(Context context, Double latitude, Double longitude) {
        try {
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(context, Locale.getDefault());
            String address = null;
            String knownName = null;


            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            knownName = addresses.get(0).getFeatureName();
            if (knownName.equals("") || knownName == null) {
                return address + ", " + city + ", " + state + ", " + country + ", " + postalCode;
            } else {
                return knownName + ", " + address + ", " + city + ", " + state + ", " + country + ", " + postalCode;
            }

        } catch (Exception e) {

        }
        return null;
    }


    public static String getDeviceId(Context context) {
        try {
            String deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            return deviceId;
        } catch (Exception e) {

        }
        return null;
    }
}
