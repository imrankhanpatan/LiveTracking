package com.example;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.toolyt.location.activity.HomeActivity;
import com.toolyt.livetracking.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        ToolytLocation.getInstance(getApplicationContext()).startLocationUpdates(MainActivity.this);
        startActivity(new Intent(MainActivity.this, HomeActivity.class));
    }
}
