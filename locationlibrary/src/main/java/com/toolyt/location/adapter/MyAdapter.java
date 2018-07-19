package com.toolyt.location.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.toolyt.location.R;
import com.toolyt.location.database.LocationData;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private ArrayList<LocationData> locationsList;


    public void setLocationData(ArrayList<LocationData> locationsList) {
        this.locationsList = locationsList;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_item, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(locationsList.get(position));
    }

    @Override
    public int getItemCount() {
        return locationsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtTime;
        TextView txtLatitude;
        TextView txtLongitude;
        TextView txtAccuracy;
        TextView txtDifference;

        public ViewHolder(View itemView) {
            super(itemView);
            txtTime = itemView.findViewById(R.id.text_time);
            txtLatitude = itemView.findViewById(R.id.text_lat);
            txtLongitude = itemView.findViewById(R.id.text_longi);
            txtAccuracy = itemView.findViewById(R.id.text_accu);
            txtDifference = itemView.findViewById(R.id.tv_difference);

        }

        public void bind(LocationData locationData) {
            txtTime.setText("" + locationData.getTime());
            txtLatitude.setText("" + locationData.getLatitude());
            txtLongitude.setText("" + locationData.getLongitude());
            txtAccuracy.setText("" + locationData.getAccuracy());
            txtDifference.setText("" + locationData.getDiff());
        }
    }
}
