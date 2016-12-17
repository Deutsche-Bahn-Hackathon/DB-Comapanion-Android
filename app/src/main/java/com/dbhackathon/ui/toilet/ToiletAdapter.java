package com.dbhackathon.ui.toilet;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dbhackathon.R;
import com.dbhackathon.data.model.Facility;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ToiletAdapter extends RecyclerView.Adapter<ToiletAdapter.ViewHolder> {

    private Context mContext;
    private List<Facility> mItems;

    public ToiletAdapter(Context context, List<Facility> items) {
        this.mContext = context;
        this.mItems = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_toilet,
                parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Facility facility = mItems.get(position);

        holder.wagon.setText("Wagon " + facility.wagon());

        holder.status.setText("Toilet " + (facility.free() ? "unoccupied" : "occupied"));
        holder.status.setTextColor(ContextCompat.getColor(mContext, facility.free() ?
                R.color.material_green_500 : R.color.material_red_500));
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.list_item_toilet_wagon) TextView wagon;
        @BindView(R.id.list_item_toilet_status) TextView status;

        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }

}
