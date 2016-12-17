package com.dbhackathon.ui.station;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dbhackathon.R;
import com.dbhackathon.data.model.Train;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

class StationDetailsAdapter extends RecyclerView.Adapter<StationDetailsAdapter.TrainViewHolder> {

    private final SimpleDateFormat INPUT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSSS");
    private final SimpleDateFormat OUTPUT = new SimpleDateFormat("HH:mm");

    private Context mContext;
    private List<Train> mItems;

    StationDetailsAdapter(Context context, List<Train> items) {
        mContext = context;
        mItems = items;
    }

    @Override
    public TrainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.list_item_train, parent, false);

        return new TrainViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrainViewHolder holder, int position) {
        Train train = mItems.get(position);

        int icon = R.drawable.ic_train_white_24dp;
        if (train.name().startsWith("ICE")) {
            icon = R.drawable.ic_directions_railway_white_24dp;
        }

        holder.icon.setImageDrawable(ContextCompat.getDrawable(mContext, icon));

        try {
            Date date = INPUT.parse(train.datetime().date());
            holder.time.setText(OUTPUT.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.name.setText(train.name());

        holder.destination.setText(train.stop());
        holder.track.setText("Track " + train.track());
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }


    class TrainViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.list_item_departures_icon) ImageView icon;

        @BindView(R.id.list_item_departures_line) TextView name;
        @BindView(R.id.list_item_departures_time) TextView time;

        @BindView(R.id.list_item_departures_destination) TextView destination;
        @BindView(R.id.list_item_departures_delay) TextView track;

        TrainViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
