package com.toolyt.livetracking.activity;

import android.Manifest;
import android.arch.persistence.room.Room;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.DetectedActivity;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.toolyt.livetracking.database.LocationData;
import com.toolyt.livetracking.database.LocationDatabase;
import com.toolyt.livetracking.service.MyLocationService;
import com.toolyt.livetracking.R;
import com.toolyt.livetracking.Utils.Constants;
import com.toolyt.livetracking.adapter.MyAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private MyLocationReceiver locationReceiver;
    private static final String DATABASE_NAME = "location_db";
    private LocationDatabase locationDatabase;
    private RecyclerView rvLocationList;
    private LinearLayoutManager layoutManager;
    private MyAdapter locationAdapter;
    private ArrayList<LocationData> locationList;
    private int size = 0;
    private Button btnMap, btnAnalytics;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rvLocationList = findViewById(R.id.locationslist);

        btnMap = findViewById(R.id.btn_map);
        btnAnalytics = findViewById(R.id.btn_analytics);
        btnMap.setOnClickListener(this);
        btnAnalytics.setOnClickListener(this);
        layoutManager = new LinearLayoutManager(this);
        rvLocationList.setLayoutManager(layoutManager);
        locationList = new ArrayList<>();

        locationAdapter = new MyAdapter();
        getLocation();
        locationDatabase = Room.databaseBuilder(getApplicationContext(),
                LocationDatabase.class, DATABASE_NAME)
                .fallbackToDestructiveMigration().allowMainThreadQueries()
                .build();
    }

    public void updateList() {
        locationList.clear();
        locationList.addAll(locationDatabase.daoLocation().loadAllLocations());
        setAdapter();
    }

    public void setAdapter() {
        size = locationList.size();

        if (size != 0) {
            locationAdapter.setLocationData(locationList);
            rvLocationList.setAdapter(locationAdapter);
            locationAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(getApplicationContext(), "No data available", Toast.LENGTH_SHORT).show();
        }
    }

    public void startLocationService() {
        try {
            //Register BroadcastReceiver
            //to receive event from our service
            locationReceiver = new MyLocationReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(MyLocationService.LOCATION_ACTION);
            registerReceiver(locationReceiver, intentFilter);

            Intent intent = new Intent(MainActivity.this, MyLocationService.class);
            intent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
            startService(intent);

        } catch (Exception e) {

        }
    }

    public void getLocation() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {

                            startLocationService();
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // permission is denied permenantly, navigate user to app settings
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .onSameThread()
                .check();

    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btn_map) {
            startActivity(new Intent(MainActivity.this, LiveMapsActivity.class));
        }

        if (v.getId() == R.id.btn_analytics) {
            //startActivity(new Intent(MainActivity.this, AnalyticsActivity.class));
            startActivity(new Intent(MainActivity.this,IdealActivity.class));
        }

    }

    public class MyLocationReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            updateList();
        }
    }


}