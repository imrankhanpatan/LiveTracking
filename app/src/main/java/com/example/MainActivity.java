package com.example;

import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.toolyt.livetracking.R;
import com.toolyt.location.location.AccuracyMode;
import com.toolyt.location.location.LocationUpdateCallback;
import com.toolyt.location.sdk.ToolytLocationTracker;
import com.toolyt.location.sdk.ToolytManager;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private Button btnStart;
    private Button btnStop;
    private Button btnGetLocation;
    private Button btnRegister;
    private TextView tvCurrentLoc;

    private ToolytManager toolytManager; //declare ToolytLocation class

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnStart = findViewById(R.id.btn_start);
        btnStop = findViewById(R.id.btn_stop);
        tvCurrentLoc = findViewById(R.id.curr_address);
        btnGetLocation = findViewById(R.id.btn_get_location);
        btnRegister = findViewById(R.id.btn_reg);

        // initialize ToolytLocation
        toolytManager = new ToolytManager(MainActivity.this);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, String> userDetailsMap = new HashMap<>();
                userDetailsMap.put("color", "#cdcdcd");
                userDetailsMap.put("company_id", "Toolyt123");
                userDetailsMap.put("details", "My_details");
                userDetailsMap.put("user_id", "1234");
                userDetailsMap.put("user_name", "Immu");
                toolytManager.registerUser(MainActivity.this, userDetailsMap);
            }
        });
        btnGetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toolytManager.getCurrentLocation(MainActivity.this, new LocationUpdateCallback() {
                    @Override
                    public void onLocation(Location location) {
                        Log.d("CURR_LOCATION", "" + location.getLatitude());
                    }

                    @Override
                    public void onAddress(String address) {
                        tvCurrentLoc.setText(address);
                        Log.d("CURR_LOCATION", "" + address);
                    }

                    @Override
                    public void onFailed(String error) {
                        Log.d("CURR_LOCATION", "" + error);
                    }
                });


            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //call startLocationUpdates method with ToolytLocation object and pass activity
                new ToolytLocationTracker(MainActivity.this)
                        .setAccuracy(AccuracyMode.PRIORITY_MEDIUM_ACCURACY)
                        .startTracker();
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //call stopLocationUpdates method with ToolytLocation object and pass context
                new ToolytLocationTracker(MainActivity.this).stopTracker();

            }
        });
    }

}