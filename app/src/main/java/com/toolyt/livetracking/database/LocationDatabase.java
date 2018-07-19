package com.toolyt.livetracking.database;

import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.RoomDatabase;

import com.toolyt.livetracking.model.DrivingMode;
import com.toolyt.livetracking.model.FilteredLocationData;
import com.toolyt.livetracking.model.IdealMode;
import com.toolyt.livetracking.model.RunningMode;
import com.toolyt.livetracking.model.StayedLocation;
import com.toolyt.livetracking.model.UserActivity;
import com.toolyt.livetracking.model.WalkingMode;

@Database(entities = {LocationData.class, FilteredLocationData.class, UserActivity.class
        , DrivingMode.class, WalkingMode.class, RunningMode.class, IdealMode.class, StayedLocation.class}, version = 1, exportSchema = false)
public abstract class LocationDatabase extends RoomDatabase {

    public abstract LocationDao daoLocation();

    @Override
    protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration config) {
        return null;
    }

    @Override
    protected InvalidationTracker createInvalidationTracker() {
        return null;
    }
}
