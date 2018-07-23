package com.toolyt.location.database;

import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.RoomDatabase;

import com.toolyt.location.model.FilteredLocationData;
import com.toolyt.location.model.StayedLocation;
import com.toolyt.location.model.UserActivity;

@Database(entities = {LocationData.class, FilteredLocationData.class, UserActivity.class
        ,StayedLocation.class}, version = 1, exportSchema = false)
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
