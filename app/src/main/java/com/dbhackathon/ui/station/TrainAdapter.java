package com.dbhackathon.ui.station;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dbhackathon.R;
import com.dbhackathon.data.model.Train;
import com.dbhackathon.util.Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created on 12/17/16.
 *
 * @author Martin Fink
 */

public class TrainAdapter extends RecyclerView.Adapter<TrainAdapter.TrainViewHolder> {

    private List<Train> mItems;

    private Utils.ActionListener<Train> mActionListener;

    public TrainAdapter(List<Train> items) {
        mItems = items;
    }

    @Override
    public TrainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_train, parent, false);

        return new TrainViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrainViewHolder holder, int position) {
        Train train = mItems.get(position);

        holder.track.setText(String.format(holder.track.getText().toString(), position));
    }

    public void setActionListener(Utils.ActionListener<Train> actionListener) {
        mActionListener = actionListener;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class TrainViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.item_train_name) TextView name;
        @BindView(R.id.item_train_track) TextView track;
        @BindView(R.id.item_train_time) TextView time;

        TrainViewHolder(View itemView) {
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
