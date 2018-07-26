package com.toolyt.location.Utils;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.toolyt.location.database.LocationDatabase;
import com.toolyt.location.model.TrackedLocation;
import com.toolyt.location.service.ToolytLocationService;

import java.util.ArrayList;
import java.util.List;

import static com.toolyt.location.Utils.Constants.DATABASE_NAME;

public class ToolytLocation extends Application {
    public static final String TAG = "MyServiceTag";
    private Context appContext;
    private LocationDatabase locationDatabase;
    private boolean isUpdateStarted = false;
    private ToolytLocationService locationService;
    private String _color = "";
    private String _companyId;
    private String _userId;
    private String _userName;

    public ToolytLocation(Context context) {
        try {
            this.appContext = context;
            locationService = new ToolytLocationService();
        } catch (Exception e) {

        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            locationDatabase = Room.databaseBuilder(getApplicationContext(),
                    LocationDatabase.class, DATABASE_NAME)
                    .fallbackToDestructiveMigration().allowMainThreadQueries()
                    .build();

        } catch (Exception e) {

        }


    }

    private Activity activity;

    public void startLocationUpdates(final Activity activity) {
        try {
            this.activity = activity;
            Dexter.withActivity(activity)
                    .withPermissions(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport report) {
                            // check if all permissions are granted
                            if (report.areAllPermissionsGranted()) {
                                locationService.startLocationService(activity,
                                        _userId, _userName, _companyId, _color);
                            }


                            // check for permanent denial of any permission
                            if (report.isAnyPermissionPermanentlyDenied()) {
                                // permission is denied permenantly, navigate user to app settings
                                openSettings();
                            }
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                            token.continuePermissionRequest();
                        }
                    })
                    .onSameThread()
                    .check();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void openSettings() {
        Toast.makeText(appContext, "Pls enable permission in app settings", Toast.LENGTH_SHORT).show();
       /* Intent intent = new Intent();
        intent.setAction(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package",
                BuildConfig.APPLICATION_ID, null);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        appContext.startActivity(intent);*/
    }

    public void stopLocationUpdates(Context context) {
        try {
            locationService.stopLocationService(context);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public ArrayList<TrackedLocation> getLocationTracker(Context context) {
        final ArrayList<TrackedLocation> toolytLocations = new ArrayList<>();
        Utils.getDatabase().getReference().child(Constants.FB_TABLE_NAME)
                .child(Utils.getDeviceId(context)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> tLocations = dataSnapshot.getChildren();


                for (DataSnapshot tLocation : tLocations) {
                    TrackedLocation t = tLocation.getValue(TrackedLocation.class);
                    toolytLocations.add(t);
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        return toolytLocations;
    }

}
