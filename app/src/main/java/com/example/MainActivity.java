package com.example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.toolyt.livetracking.R;
import com.toolyt.location.Utils.ToolytLocation;
import com.toolyt.location.Utils.ToolytUser;

public class MainActivity extends AppCompatActivity {

    private Button btnStart;
    private Button btnStop;

    private ToolytLocation toolytLocation; //declare ToolytLocation class

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnStart = findViewById(R.id.btn_start);
        btnStop = findViewById(R.id.btn_stop);

        // initialize ToolytLocation
        toolytLocation = new ToolytLocation(MainActivity.this);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //call startLocationUpdates method with ToolytLocation object and pass activity
                toolytLocation.startLocationUpdates(MainActivity.this);
                new ToolytUser.Builder().setUserId("123").setUserName("imran")
                        .setCompanyId("xxx").setColor("#cdcdcd").build();

            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //call stopLocationUpdates method with ToolytLocation object and pass context
                toolytLocation.stopLocationUpdates(MainActivity.this);
            }
        });
    }
}