package com.dbhackathon.ui.main;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dbhackathon.R;
import com.dbhackathon.data.model.Station;
import com.dbhackathon.util.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created on 12/17/16.
 *
 * @author Martin Fink
 */

public class StationAdapter extends RecyclerView.Adapter<StationAdapter.StationViewHolder> {

    private List<Station> mItems;

    private Utils.ActionListener<Station> mActionListener;

    public StationAdapter() {
        mItems = new ArrayList<>();
    }

    public void setItems(List<Station> items) {
        mItems.clear();

        if (items != null) {
            mItems = items;
        }

        notifyDataSetChanged();
    }

    public void setActionListener(Utils.ActionListener<Station> actionListener) {
        mActionListener = actionListener;
    }

    @Override
    public StationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_station, parent, false);

        return new StationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StationViewHolder holder, int position) {
        Station station = mItems.get(position);

        holder.name.setText(station.name());
        holder.latLng.setText(String.format("%s,%s", station.lat(), station.lng()));
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class StationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.item_station_name) TextView name;
        @BindView(R.id.item_station_lat_lng) TextView latLng;

        public StationViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mActionListener != null) {
                mActionListener.onClick(mItems.get(getAdapterPosition()));
            }
        }
    }

}
