package com.dbhackathon.ui.station;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dbhackathon.R;
import com.dbhackathon.data.model.Train;
import com.trello.rxlifecycle.components.support.RxFragment;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DepartureArrivalFragment extends RxFragment implements TrainAdapter.ActionListener<Train> {

    private List<Train> mTrains;

    @BindView(R.id.recycler) RecyclerView mRecyclerView;
    private TrainAdapter mTrainAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_departures_arrivals, container, false);

        ButterKnife.bind(this, view);

        Bundle bundle = getArguments();

        if (bundle == null && mTrains == null) {
            throw new IllegalArgumentException("Trains cannot be null!");
        }

        mTrains = bundle != null ? bundle.getParcelableArrayList(StationActivity.BUNDLE_TRAINS) : mTrains;

        mTrainAdapter = new TrainAdapter(mTrains);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mTrainAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mTrainAdapter.setActionListener(this);

        return view;
    }

    @Override
    public void onClick(Train train) {
        Toast.makeText(getActivity(), "Ich mag Züge!", Toast.LENGTH_SHORT).show();
    }
}
