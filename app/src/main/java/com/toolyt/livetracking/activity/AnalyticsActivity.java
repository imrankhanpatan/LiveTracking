package com.toolyt.livetracking.activity;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.toolyt.livetracking.R;
import com.toolyt.livetracking.Utils.Constants;
import com.toolyt.livetracking.Utils.Utils;
import com.toolyt.livetracking.database.LocationDatabase;
import com.toolyt.livetracking.model.StayedLocation;
import com.toolyt.livetracking.model.UserActivity;

import java.util.ArrayList;
import java.util.Date;

public class AnalyticsActivity extends AppCompatActivity {

    private LocationDatabase locationDatabase;
    private ArrayList<UserActivity> userActivities;
    private ArrayList<UserActivity> walkingActivities;
    private ArrayList<UserActivity> idealActivities;
    private ArrayList<UserActivity> drivingActivities;

    private String data = "";

    TextView tvWalking;
    TextView tvDriving;
    TextView tvStill;
    Button btnIdeal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);
        tvWalking = findViewById(R.id.text_walking);
        tvStill = findViewById(R.id.text_still);
        tvDriving = findViewById(R.id.text_driving);
        btnIdeal = findViewById(R.id.btn_ideal);

        locationDatabase = Room.databaseBuilder(getApplicationContext(),
                LocationDatabase.class, Constants.DATABASE_NAME)
                .fallbackToDestructiveMigration().allowMainThreadQueries()
                .build();

        userActivities = new ArrayList<>();
        walkingActivities = new ArrayList<>();
        idealActivities = new ArrayList<>();
        drivingActivities = new ArrayList<>();
        userActivities.addAll(locationDatabase.daoLocation().getUserActivities());
        Log.d("ACTITIVY_COUNT", "" + userActivities.size());
        for (int i = 0; i < userActivities.size(); i++) {
            Log.d("USER_ACTITIVY", "" + userActivities.get(i).getActivity() + "  " + userActivities.get(i).getTime());
        }

        ArrayList<StayedLocation> locations = new ArrayList<>();
        locations.addAll(locationDatabase.daoLocation().loadStayedLocations());
        for(int i=0;i<locations.size();i++){
            Log.d("STAYED_TIME",""+locations.get(i).getDuration());
        }

        setDrivingActivities();
        setIdealActivities();
        setWalkingActivities();

        getIdealDetails();
        getWalkingDetails();

        btnIdeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AnalyticsActivity.this,IdealActivity.class));
            }
        });
    }


    private void setIdealActivities() {
        idealActivities.addAll(locationDatabase.daoLocation().getStillActivities());
        Log.d("IDEAL_COUNT", "" + idealActivities.size());
        for (int i = 0; i < idealActivities.size(); i++) {
            Log.d("IDEAL_ACTITIVY", "" + idealActivities.get(i).getActivity()
                    + "  " + idealActivities.get(i).getTime());
        }

        if (idealActivities.size() > 0) {
            Date startDate = Utils.getTime(idealActivities.get(0).getTime());
            Date endDate = Utils.getTime(idealActivities.get(idealActivities.size() - 1).getTime());
            Log.d("IDEAL_DATE", "" + startDate + "" + endDate);
            if (startDate != null && endDate != null) {
                String time = Utils.getSpentTime(startDate, endDate);
                tvStill.setText("Ideal: " + time);
            }

        }
    }

    private void setWalkingActivities() {
        walkingActivities.addAll(locationDatabase.daoLocation().getWalkingActivities());
        Log.d("WALKING_COUNT", "" + walkingActivities.size());
        for (int i = 0; i < walkingActivities.size(); i++) {
            Log.d("WALKING_ACTITIVY", "" + walkingActivities.get(i).getActivity() + "  " + walkingActivities.get(i).getTime());
        }

        if (walkingActivities.size() > 0) {
            Date startDate = Utils.getTime(walkingActivities.get(0).getTime());
            Date endDate = Utils.getTime(walkingActivities.get(walkingActivities.size() - 1).getTime());
            Log.d("WALKING_TIME", "" + startDate + "" + endDate);
            if (startDate != null && endDate != null) {
                String time = Utils.getSpentTime(startDate, endDate);
                tvWalking.setText("Walking: " + time);
            }

        }
    }

    private void setDrivingActivities() {
        drivingActivities.addAll(locationDatabase.daoLocation().getDrivingActivities());
        Log.d("DRIVING_COUNT", "" + drivingActivities.size());
        for (int i = 0; i < drivingActivities.size(); i++) {
            Log.d("DRIVING_ACTITIVY", "" + drivingActivities.get(i).getActivity() + "  " + drivingActivities.get(i).getTime());
        }

        if (drivingActivities.size() > 0) {
            Date startDate = Utils.getTime(drivingActivities.get(0).getTime());
            Date endDate = Utils.getTime(drivingActivities.get(drivingActivities.size() - 1).getTime());
            Log.d("DRIVING_TIME", "" + startDate + "" + endDate);
            if (startDate != null && endDate != null) {
                String time = Utils.getSpentTime(startDate, endDate);
                tvDriving.setText("Driving: " + time);
            }

        }
    }


    private void getWalkingDetails() {
        walkingActivities.clear();
        walkingActivities.addAll(locationDatabase.daoLocation().getWalkingActivities());
        if (walkingActivities.size() == 0) {
            return;
        }
        long id = walkingActivities.get(0).getStateId();
        ArrayList<UserActivity> walkings = new ArrayList<>();
        for (int i = 0; i < walkingActivities.size(); i++) {
            //     walkings.clear();
            Log.d("WALKING_ID", "" + walkingActivities.get(i).getStateId());
            if (id == walkingActivities.get(i).getStateId()) {
                walkings.add(walkingActivities.get(i));
            } else {
                id = walkingActivities.get(i).getStateId();
            }

            if (walkingActivities.size() > (i + 1)) {
                if (id != walkingActivities.get(i + 1).getStateId()) {
                    if (walkings.size() > 0) {
                        Date startDate = Utils.getTime(walkings.get(0).getTime());
                        Date endDate = Utils.getTime(walkings.get(walkings.size() - 1).getTime());
                        Log.d("WALKING_FROM", "" + startDate + "" + endDate);
                        if (startDate != null && endDate != null) {
                            String time = Utils.getSpentTime(startDate, endDate);
                            //tvDriving.setText("Driving: " + time);
                            Log.d("WALKING_TIME: ", "" + time);
                            tvDriving.setText(tvDriving.getText().toString() + "/n" + time);
                        }

                    }
                }
            }
        }
    }

    private void getIdealDetails() {
         idealActivities.clear();
        idealActivities.addAll(locationDatabase.daoLocation().getStillActivities());
        if (idealActivities.size() != 0) {

        }
         /*   long id = idealActivities.get(0).getStateId();
            ArrayList<UserActivity> ideals = new ArrayList<>();
            for (int i = 0; i < idealActivities.size(); i++) {
                //       ideals.clear();
                Log.d("IDEAL_ID", "" + idealActivities.get(i).getStateId());
                if (id == idealActivities.get(i).getStateId()) {
                    ideals.add(idealActivities.get(i));
                } else {
                    id = idealActivities.get(i).getStateId();
                }

                if (idealActivities.size() > (i + 1)) {
                    if (id != idealActivities.get(i + 1).getStateId()) {
                        if (ideals.size() > 0) {
                            Date startDate = Utils.getTime(ideals.get(0).getTime());
                            Date endDate = Utils.getTime(ideals.get(ideals.size() - 1).getTime());
                            Log.d("IDEAL_FROM", "" + startDate + "" + endDate);
                            if (startDate != null && endDate != null) {
                                String time = Utils.getSpentTime(startDate, endDate);
                                //tvDriving.setText("Driving: " + time);
                                Log.d("IDEAL_TIME: ", "" + time);
                                tvDriving.setText(tvDriving.getText().toString() + "/n" + time);
                            }

                        }
                    }
                }
        }*/
    }
}
