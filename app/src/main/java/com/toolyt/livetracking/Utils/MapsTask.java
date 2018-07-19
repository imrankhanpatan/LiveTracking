package com.toolyt.livetracking.Utils;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.toolyt.livetracking.database.LocationDatabase;
import com.toolyt.livetracking.model.FilteredLocationData;

import java.util.ArrayList;

public class MapsTask extends AsyncTask<Void, Void, String> {

    private LocationDatabase locationDatabase;
    private ArrayList<FilteredLocationData> locationDataArrayList;
    private ArrayList<LatLng> latLngs;
    private LatLng latLng;
    private Location currentLocation;
    private Location nextLocation;
    private double distance;
    private Context context;

    public MapsTask(Context context) {
        this.context = context;
        locationDatabase = Room.databaseBuilder(context,
                LocationDatabase.class, Constants.DATABASE_NAME)
                .fallbackToDestructiveMigration().allowMainThreadQueries()
                .build();
        currentLocation = new Location("");
        nextLocation = new Location("");
        latLngs = new ArrayList<>();
        locationDataArrayList = new ArrayList<>();
    }

    @Override
    protected String doInBackground(Void... voids) {
        distance = 0.0;
        latLngs.clear();
        locationDataArrayList.addAll(locationDatabase.daoLocation().loadFilteredLocations());

        for (int i = 0; i < locationDataArrayList.size(); i++) {
            latLng = new LatLng(Double.valueOf(locationDataArrayList.get(i).getLatitude()),
                    Double.valueOf(locationDataArrayList.get(i).getLongitude()));
            latLngs.add(latLng);


            currentLocation.setLatitude(Double.valueOf(locationDataArrayList.get(i).getLatitude()));
            currentLocation.setLongitude(Double.valueOf(locationDataArrayList.get(i).getLongitude()));
            Log.d("LIST_LOCATION", i + "   " + Utils.getLocalAddress(context, currentLocation.getLatitude(),
                    currentLocation.getLongitude()));
            if (i < locationDataArrayList.size() - 1) {
                nextLocation.setLatitude(Double.valueOf(locationDataArrayList.get(i + 1).getLatitude()));
                nextLocation.setLongitude(Double.valueOf(locationDataArrayList.get(i + 1).getLongitude()));
            }

            Log.d("MY_Distance", "" + distance);
            distance = distance + currentLocation.distanceTo(nextLocation);
        }

        return "Distance: " + Utils.getDistanceInKMS(distance);
    }

    @Override
    protected void onPostExecute(String res) {
        Log.d("RESULT: ",res);
        super.onPostExecute(res);
    }
}
