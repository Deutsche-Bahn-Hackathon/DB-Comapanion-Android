package com.dbhackathon.ui.main;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dbhackathon.R;
import com.dbhackathon.data.model.Station;
import com.dbhackathon.util.Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

class StationAdapter extends RecyclerView.Adapter<StationAdapter.StationViewHolder> {

    private List<Station> mItems;

    private Utils.ActionListener<Station> mActionListener;

    StationAdapter(List<Station> items) {
        mItems = items;
    }

    void setActionListener(Utils.ActionListener<Station> actionListener) {
        mActionListener = actionListener;
    }

    @Override
    public StationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.list_item_station, parent, false);
        return new StationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StationViewHolder holder, int position) {
        Station station = mItems.get(position);
        holder.name.setText(station.name());
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class StationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.list_item_station_name) TextView name;

        StationViewHolder(View itemView) {
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
