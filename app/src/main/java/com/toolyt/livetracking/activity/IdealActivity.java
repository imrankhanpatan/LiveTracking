package com.toolyt.livetracking.activity;

import android.arch.persistence.room.Room;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.toolyt.livetracking.R;
import com.toolyt.livetracking.Utils.Constants;
import com.toolyt.livetracking.adapter.IdealAnalyticsAdapter;
import com.toolyt.livetracking.database.LocationDatabase;
import com.toolyt.livetracking.model.StayedLocation;
import com.toolyt.livetracking.model.UserActivity;

import java.util.ArrayList;

public class IdealActivity extends AppCompatActivity {

    RecyclerView rvIdealList;
    LinearLayoutManager layoutManager;
    IdealAnalyticsAdapter idealAnalyticsAdapter;
    private LocationDatabase locationDatabase;
    private ArrayList<StayedLocation> idealActivities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ideal);
        rvIdealList = findViewById(R.id.rv_ideal);

        layoutManager = new LinearLayoutManager(this);
        rvIdealList.setLayoutManager(layoutManager);
        idealAnalyticsAdapter = new IdealAnalyticsAdapter();

        idealActivities = new ArrayList<>();

        locationDatabase = Room.databaseBuilder(getApplicationContext(),
                LocationDatabase.class, Constants.DATABASE_NAME)
                .fallbackToDestructiveMigration().allowMainThreadQueries()
                .build();
        getIdealDetails();
    }

    private void getIdealDetails() {
        ArrayList<StayedLocation> stateIds = new ArrayList<>();
        stateIds.addAll(locationDatabase.daoLocation().loadStayedLocations());
        for(int i=0;i<stateIds.size();i++){
            Log.d("STATE_ID",""+stateIds.get(i));
        }

        idealActivities.clear();
        idealActivities.addAll(locationDatabase.daoLocation().loadStayedLocations());
        if (idealActivities.size() != 0) {
            idealAnalyticsAdapter.setIdealActivityData(this,idealActivities);
            rvIdealList.setAdapter(idealAnalyticsAdapter);
            idealAnalyticsAdapter.notifyDataSetChanged();
        }
    }
}
