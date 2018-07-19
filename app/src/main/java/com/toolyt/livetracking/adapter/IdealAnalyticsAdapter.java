package com.toolyt.livetracking.adapter;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.toolyt.livetracking.R;
import com.toolyt.livetracking.database.LocationData;
import com.toolyt.livetracking.model.IdealMode;
import com.toolyt.livetracking.model.StayedLocation;
import com.toolyt.livetracking.model.UserActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class IdealAnalyticsAdapter extends RecyclerView.Adapter<IdealAnalyticsAdapter.ViewHolder> {
    private ArrayList<StayedLocation> stayedLocations;
    private Context context;

    public void setIdealActivityData(Context context,ArrayList<StayedLocation> stayedLocations) {
        this.stayedLocations = stayedLocations;
        this.context = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.analytics_row_item, parent, false);

        return new ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(stayedLocations.get(position));
    }

    @Override
    public int getItemCount() {
        return stayedLocations.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtLatitude;
        TextView txtLongitude;
        TextView txtDuration;
        TextView txtId;

        public ViewHolder(View itemView) {
            super(itemView);
            txtLatitude = itemView.findViewById(R.id.text_lat);
            txtLongitude = itemView.findViewById(R.id.text_longi);
            txtDuration = itemView.findViewById(R.id.text_duration);
            txtId = itemView.findViewById(R.id.text_id);
        }

        public void bind(StayedLocation stayedLocation) {

            txtLatitude.setText("" + stayedLocation.getLatitude());
            txtLongitude.setText("" + stayedLocation.getLongitude());
            txtId.setText("" + getAddress(stayedLocation.getLatitude(),stayedLocation.getLongitude()));
            txtDuration.setText("" + stayedLocation.getDuration());
        }

        private String getAddress(String latitude, String longitude) {
            Geocoder geocoder;
            List<Address> addresses = null;
            double lat = Double.parseDouble(latitude);
            double lang = Double.parseDouble(longitude);
            geocoder = new Geocoder(context, Locale.getDefault());

            try {
                addresses = geocoder.getFromLocation(lat, lang, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            } catch (IOException e) {
                e.printStackTrace();
            }

            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String address2 = addresses.get(0).getLocality();
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();

            if(knownName!=null){
                return knownName+", "+address2+", "+city;
            }else {
                return address+", "+address2+", "+city;
            }
        }
    }


}
