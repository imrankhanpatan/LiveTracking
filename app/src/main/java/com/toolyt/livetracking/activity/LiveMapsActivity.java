package com.toolyt.livetracking.activity;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.toolyt.livetracking.Utils.Constants;
import com.toolyt.livetracking.database.LocationDatabase;
import com.toolyt.livetracking.R;
import com.toolyt.livetracking.Utils.Utils;
import com.toolyt.livetracking.model.FilteredLocationData;
import com.toolyt.livetracking.model.StayedLocation;

import java.util.ArrayList;

public class LiveMapsActivity extends FragmentActivity implements OnMapReadyCallback {


    private static final String TAG = "live_maps";
    private GoogleMap mMap;
    private GoogleMap gMap;
    private Polyline line;
    /* private LocationDatabase locationDatabase;
     private ArrayList<LatLng> latLngs;
     private ArrayList<LatLng> stayedLatLngs;
     private ArrayList<FilteredLocationData> locationDataArrayList;
     private ArrayList<StayedLocation> stayedLocations;
     private LatLng latLng;*/
    MarkerOptions markerOptions = new MarkerOptions();
    //private Location currentLocation;
    // private Location nextLocation;
    // private double distance = 0.0;
    private TextView distanceText;
    private Button btnRefresh;
    private ProgressBar progressBar;


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_maps);
        distanceText = findViewById(R.id.show_distance);
        btnRefresh = findViewById(R.id.refresh_button);
        progressBar = findViewById(R.id.progressBar);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        startTask();
        progressBar.setVisibility(View.VISIBLE);

        /*locationDatabase = Room.databaseBuilder(getApplicationContext(),
                LocationDatabase.class, Constants.DATABASE_NAME)
                .fallbackToDestructiveMigration().allowMainThreadQueries()
                .build();*/

        /*latLngs = new ArrayList<>();
        stayedLatLngs = new ArrayList<>();
        locationDataArrayList = new ArrayList<>();
        stayedLocations = new ArrayList<>();
        currentLocation = new Location("");
        nextLocation = new Location("");*/
        // updateMap();

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                startTask();
            }
        });
    }

    private void startTask() {
        new MapsTask(LiveMapsActivity.this).execute();
    }

    /*private void updateMap() {
        try {
            distance = 0.0;
            locationDataArrayList.clear();
            latLngs.clear();
            locationDataArrayList.addAll(locationDatabase.daoLocation().loadFilteredLocations());

            for (int i = 0; i < locationDataArrayList.size(); i++) {
                latLng = new LatLng(Double.valueOf(locationDataArrayList.get(i).getLatitude()),
                        Double.valueOf(locationDataArrayList.get(i).getLongitude()));
                latLngs.add(latLng);


                currentLocation.setLatitude(Double.valueOf(locationDataArrayList.get(i).getLatitude()));
                currentLocation.setLongitude(Double.valueOf(locationDataArrayList.get(i).getLongitude()));
                Log.d("LIST_LOCATION", i + "   " + Utils.getLocalAddress(this, currentLocation.getLatitude(),
                        currentLocation.getLongitude()));
                if (i < locationDataArrayList.size() - 1) {
                    nextLocation.setLatitude(Double.valueOf(locationDataArrayList.get(i + 1).getLatitude()));
                    nextLocation.setLongitude(Double.valueOf(locationDataArrayList.get(i + 1).getLongitude()));
                }
                //getDirectionResponse(currentLocation,nextLocation);
                Log.d("MY_Distance", "" + distance);
                distance = distance + currentLocation.distanceTo(nextLocation);
            }

            if (locationDataArrayList.size() > 1) {
                distanceText.setText("Distance: " + Utils.getDistanceInKMS(distance));
            }

            if (mMap != null) {
                drawPolyline(latLngs);
            }


            stayedLocations.clear();
            stayedLocations.addAll(locationDatabase.daoLocation().loadStayedLocations());

            for (int j = 0; j < stayedLocations.size(); j++) {
                addStayedMarker(new LatLng(Double.valueOf(stayedLocations.get(j).getLatitude()),
                        Double.valueOf(stayedLocations.get(j).getLongitude())));
            }

        } catch (Exception e) {

        }

    }
*/
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        gMap = googleMap;
    }

    public void drawPolyline(ArrayList<LatLng> latLngs, double distance) {
        try {
            PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
            for (int z = 0; z < latLngs.size(); z++) {
                LatLng point = latLngs.get(z);
                addMarker(latLngs.get(z), z);
                options.add(point);
            }
            line = mMap.addPolyline(options);
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (LatLng latLng : latLngs) {
                builder.include(latLng);
            }

            final LatLngBounds bounds = builder.build();

            //BOUND_PADDING is an int to specify padding of bound.. try 100.
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 100);
            mMap.animateCamera(cu);
            if (latLngs.size() > 1) {
                distanceText.setText("Distance: " + Utils.getDistanceInKMS(distance));
            }

            progressBar.setVisibility(View.GONE);
        } catch (Exception e) {

        }
    }

    public void addMarker(LatLng latLng, int position) {
        try {
            markerOptions.position(latLng)
                    .title(latLng.latitude + " : " + latLng.longitude)
                    .icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));

            // Animating to the touched position
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

            // Placing a marker on the touched position
            mMap.addMarker(markerOptions);

        } catch (Exception e) {

        }
    }

    public void addStayedMarker(LatLng latLng) {
        try {
            markerOptions.position(latLng)
                    .title(latLng.latitude + " : " + latLng.longitude)
                    .icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_RED));

            // Animating to the touched position
            //   mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

            // Placing a marker on the touched position
            gMap.addMarker(markerOptions);
            //drawPolyline();
        } catch (Exception e) {

        }
    }

    public class MapsTask extends AsyncTask<Void, Void, ArrayList<LatLng>> {

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
        protected ArrayList<LatLng> doInBackground(Void... voids) {
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
            return latLngs;
        }

        @Override
        protected void onPostExecute(ArrayList<LatLng> latLngs) {
            drawPolyline(latLngs, distance);
            super.onPostExecute(latLngs);
        }
    }

}