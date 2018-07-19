package com.toolyt.location.activity;

import android.arch.persistence.room.Room;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.toolyt.location.R;
import com.toolyt.location.Utils.Constants;
import com.toolyt.location.adapter.IdealAnalyticsAdapter;
import com.toolyt.location.database.LocationDatabase;
import com.toolyt.location.model.StayedLocation;

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
        /*rvIdealList = findViewById(R.id.rv_ideal);

        layoutManager = new LinearLayoutManager(this);
        rvIdealList.setLayoutManager(layoutManager);
        idealAnalyticsAdapter = new IdealAnalyticsAdapter();

        idealActivities = new ArrayList<>();

        locationDatabase = Room.databaseBuilder(getApplicationContext(),
                LocationDatabase.class, Constants.DATABASE_NAME)
                .fallbackToDestructiveMigration().allowMainThreadQueries()
                .build();
        getIdealDetails();*/
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
