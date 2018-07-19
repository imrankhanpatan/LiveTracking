package com.toolyt.livetracking.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.toolyt.livetracking.model.FilteredLocationData;
import com.toolyt.livetracking.model.StayedLocation;
import com.toolyt.livetracking.model.UserActivity;

import java.util.Collection;
import java.util.List;

@Dao
public interface LocationDao {

    @Insert
    void insertLocation(LocationData locationData);

    @Query("SELECT * FROM locationdata order by time desc")
    public List<LocationData> loadAllLocations();

    @Insert
    void insertFilteredLocation(FilteredLocationData filteredLocationData);

    @Query("SELECT * FROM filteredLocationData order by currentTime desc")
    public List<FilteredLocationData> loadFilteredLocations();

    @Insert
    void insertActivityData(UserActivity userActivity);

    @Query("SELECT * FROM UserActivity order by time desc")
    public List<UserActivity> getUserActivities();

    @Query("SELECT * FROM UserActivity where activity like 'walking%'")
    public List<UserActivity> getWalkingActivities();

    @Query("SELECT * FROM UserActivity where activity like 'still%'")
    public List<UserActivity> getStillActivities();

    @Query("SELECT * FROM UserActivity where activity like 'invehicle%'")
    public List<UserActivity> getDrivingActivities();

    @Query("SELECT DISTINCT stateId FROM UserActivity where activity like 'still%'")
    public List<Long> getIdealsStateId();


    @Insert
    void insertStayedLocation(StayedLocation stayedLocation);

    @Query("SELECT * FROM StayedLocation order by time asc")
    public List<StayedLocation> loadStayedLocations();

}
