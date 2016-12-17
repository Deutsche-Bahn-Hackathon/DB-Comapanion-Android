package com.dbhackathon.ui.alarm;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dbhackathon.R;
import com.dbhackathon.data.model.Station;
import com.dbhackathon.util.Settings;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.ViewHolder> {

    private Context mContext;
    private List<Station> mItems;

    AlarmAdapter(Context context, List<Station> items) {
        this.mContext = context;
        this.mItems = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_station,
                parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Station station = mItems.get(position);
        holder.name.setText(station.name());
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.list_item_station_layout) FrameLayout layout;
        @BindView(R.id.list_item_station_name) TextView name;

        ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            layout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position == RecyclerView.NO_POSITION) return;

            Station station = mItems.get(position);

            new AlertDialog.Builder(mContext, R.style.DialogStyle)
                    .setTitle("Enable alarm?")
                    .setMessage("Do you want to be alerted before you enter " + station.name() + "?")
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .setPositiveButton("Enable", (dialog, which) -> {
                        Settings.setHasAlarms(mContext, true);
                        dialog.dismiss();

                        Toast.makeText(mContext, "Alarm enabled", Toast.LENGTH_SHORT).show();

                        if (mContext instanceof AlarmActivity) {
                            ((AlarmActivity) mContext).dismiss(null);
                        }
                    })
                    .create()
                    .show();
        }
    }
}
