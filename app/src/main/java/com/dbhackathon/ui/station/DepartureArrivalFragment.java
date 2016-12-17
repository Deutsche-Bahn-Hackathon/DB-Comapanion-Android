package com.dbhackathon.ui.station;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dbhackathon.Config;
import com.dbhackathon.R;
import com.dbhackathon.data.model.Train;
import com.dbhackathon.data.model.TrainResponse;
import com.dbhackathon.data.network.RestClient;
import com.dbhackathon.data.network.TrainApi;
import com.dbhackathon.util.Utils;
import com.trello.rxlifecycle.components.support.RxFragment;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class DepartureArrivalFragment extends RxFragment implements Utils.ActionListener<Train> {

    private String departures_arrivals;
    private String id;
    private List<Train> mTrains;

    @BindView(R.id.recycler) RecyclerView mRecyclerView;

    private TrainAdapter mTrainAdapter;

    private Subscription mTrainSubscription;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_departures_arrivals, container, false);

        ButterKnife.bind(this, view);

        Bundle bundle = getArguments();

        if (bundle == null && mTrains == null) {
            throw new IllegalArgumentException("Trains cannot be null!");
        }

        id = bundle != null ? bundle.getString(Config.EXTRA_STATION_ID) : id;
        departures_arrivals = bundle != null ? bundle.getString(Config.EXTRA_DEPARTURES_ARRIVALS) : departures_arrivals;

        mTrainAdapter = new TrainAdapter();

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mTrainAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mTrainAdapter.setActionListener(this);

        loadTrains();

        return view;
    }

    private void loadTrains() {
        if (mTrainSubscription != null) {
            mTrainSubscription.unsubscribe();
        }

        TrainApi trainApi = RestClient.ADAPTER.create(TrainApi.class);

        mTrainSubscription = trainApi.getArrivals(id, departures_arrivals)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindToLifecycle())
                .subscribe(new Subscriber<TrainResponse>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {

                        Timber.e(e, "Could not load trains!");
                    }

                    @Override
                    public void onNext(TrainResponse trainResponse) {
                        mTrains = departures_arrivals.equals(Config.EXTRA_DEPARTURES) ? trainResponse.departures() : trainResponse.arrivals();
                        mTrainAdapter.setItems(mTrains);
                    }
                });
    }

    @Override
    public void onClick(Train train) {
        Toast.makeText(getActivity(), "Ich mag Züge!", Toast.LENGTH_SHORT).show();
    }
}
