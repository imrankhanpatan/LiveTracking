package com.example;

import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.toolyt.livetracking.R;
import com.toolyt.location.callback.UserRegistrationCallback;
import com.toolyt.location.location.AccuracyMode;
import com.toolyt.location.callback.LocationUpdateCallback;
import com.toolyt.location.sdk.ToolytLocationTracker;
import com.toolyt.location.sdk.ToolytSDKManager;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private Button btnStart;
    private Button btnStop;
    private Button btnGetLocation;
    private Button btnRegister;
    private TextView tvCurrentLoc;
    ToolytLocationTracker locationTracker;

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
        ToolytSDKManager.initialize(MainActivity.this);
        locationTracker = ToolytLocationTracker.getInstance(getApplicationContext());
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, String> metadata = new HashMap<>();
                metadata.put("color", "#cdcdcd");
                metadata.put("company_id", "Toolyt123");
                metadata.put("details", "My_details");
                metadata.put("user_id", "1234");
                metadata.put("user_name", "Immu");

                ToolytSDKManager.registerUser(metadata, new UserRegistrationCallback() {
                    @Override
                    public void onSuccess(String success) {
                        Log.d("REG_USER", "" + success);
                    }

                    @Override
                    public void onFailed(String error) {
                        Log.d("REG_USER", "" + error);
                    }
                });
            }
        });

        btnGetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* ToolytSDKManager.getCurrentLocation(MainActivity.this, new LocationUpdateCallback() {
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
                    public void onError(String error) {
                        Log.d("CURR_LOCATION", "" + error);
                        tvCurrentLoc.setText(error);
                    }
                });
*/

                Boolean isRunning = ToolytLocationTracker.isServiceRunning(MainActivity.this);
                Log.d("IS_RUNNING", "" + isRunning);

            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //call startLocationUpdates method with ToolytLocation object and pass activity
                /*new ToolytLocationTracker(MainActivity.this)
                        .setAccuracyPriority(AccuracyMode.PRIORITY_HIGH_ACCURACY)
                        .startTracker();*/

                locationTracker.setAccuracyPriority(AccuracyMode.PRIORITY_HIGH_ACCURACY).startTracker();
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //call stopLocationUpdates method with ToolytLocation object and pass context
                // new ToolytLocationTracker(MainActivity.this).stopTracker();
                locationTracker.stopTracker();
            }
        });
    }

}