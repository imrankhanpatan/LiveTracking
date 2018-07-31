package com.toolyt.location.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.toolyt.location.model.FilteredLocationData;
import com.toolyt.location.model.StayedLocation;

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
    void insertStayedLocation(StayedLocation stayedLocation);

    @Query("SELECT * FROM StayedLocation order by time asc")
    public List<StayedLocation> loadStayedLocations();

}
