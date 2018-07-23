package com.example;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.toolyt.location.Utils.ToolytLocation;
import com.toolyt.livetracking.R;

public class MainActivity extends AppCompatActivity {


    private Button btnStart;
    private Button btnStop;
    ToolytLocation toolytLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnStart = findViewById(R.id.btn_start);
        btnStop = findViewById(R.id.btn_stop);
        toolytLocation = new ToolytLocation(MainActivity.this);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // ToolytLocation.getInstance(getApplicationContext()).startLocationUpdates(MainActivity.this);
                toolytLocation.startLocationUpdates(MainActivity.this);
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // ToolytLocation.getInstance(getApplicationContext()).stopLocationUpdates(MainActivity.this);
                toolytLocation.stopLocationUpdates(getApplicationContext());
            }
        });
    }
}
