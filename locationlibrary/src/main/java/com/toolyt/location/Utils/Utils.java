package com.toolyt.location.Utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Utils {

    public static String getCurrentTime() {
       /* int time = (int) (System.currentTimeMillis());
        Timestamp tsTemp = new Timestamp(time);
        String currentTime = tsTemp.toString();*/
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");//dd/MM/yyyy
        Date now = new Date();
        String strDate = sdfDate.format(now);
        return strDate;
    }

    public static Double getDistanceInKMS(Double dis) {
        String dist = "" + dis;
        String[] parts = dist.split("\\.");
        String part1 = parts[0];
        dis = Double.valueOf(part1) / 1000;
        return dis;
    }

    public static Date getTime(String timeStamp) {
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
    }

    public static String getTimeFromMillis(long millis) {
        String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
        System.out.println(hms);
        return hms;
    }

    public static String getSpentTime(Date startTime, Date endTime) {
        long mills = endTime.getTime()-startTime.getTime();
        int hours = (int) (mills / (1000 * 60 * 60));
        int mins = (int) ((mills / (1000 * 60)) % 60);
        Log.d("SPENT TIME: ", "" + hours + ":" + mins);
        return hours + "h:" + mins+"m";
    }


    public static String getLocalAddress(Context context, Double latitude, Double longitude) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());
        String address = null;
        String knownName = null;

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            knownName = addresses.get(0).getFeatureName();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (knownName.equals("") || knownName == null) {
            return address;
        } else {
            return knownName;
        }
    }
}
