package com.dbhackathon.ui.main;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.dbhackathon.data.model.Station;

import java.util.List;

import butterknife.ButterKnife;

/**
 * Created on 12/17/16.
 *
 * @author Martin Fink
 */

public class StationAdapter extends RecyclerView.Adapter<StationAdapter.StationViewHolder> {

    private List<Station> mItems;

    public StationAdapter(List<Station> items) {

    }


    @Override
    public StationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(StationViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class StationViewHolder extends RecyclerView.ViewHolder {



        public StationViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }

}
