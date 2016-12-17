package com.dbhackathon.ui.station;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dbhackathon.Config;
import com.dbhackathon.R;
import com.dbhackathon.data.model.Train;
import com.dbhackathon.data.model.TrainResponse;
import com.dbhackathon.data.network.RestClient;
import com.dbhackathon.data.network.TrainApi;
import com.dbhackathon.ui.widget.RecyclerItemDivider;
import com.dbhackathon.util.NextObserver;
import com.dbhackathon.util.Utils;
import com.trello.rxlifecycle.components.support.RxFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class StationDetailsFragment extends RxFragment {

    private String departureArrivals;
    private String id;

    @BindView(R.id.recycler) RecyclerView mRecyclerView;
    @BindView(R.id.refresh) SwipeRefreshLayout mRefresh;

    private List<Train> mTrains;
    private StationDetailsAdapter mTrainAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_departures_arrivals, container, false);

        ButterKnife.bind(this, view);

        Bundle bundle = getArguments();

        if (bundle == null) {
            return view;
        }

        id = bundle.getString(Config.EXTRA_STATION_ID);
        departureArrivals = bundle.getString(Config.EXTRA_DEPARTURES_ARRIVALS);

        mRefresh.setColorSchemeResources(Config.REFRESH_COLORS);
        mRefresh.setOnRefreshListener(this::loadTrains);

        mTrains = new ArrayList<>();
        mTrainAdapter = new StationDetailsAdapter(getActivity(), mTrains);

        mRecyclerView.setAdapter(mTrainAdapter);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new RecyclerItemDivider(getActivity()));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        loadTrains();

        return view;
    }

    private void loadTrains() {
        mRefresh.setRefreshing(true);

        TrainApi trainApi = RestClient.ADAPTER.create(TrainApi.class);
        trainApi.getDepArrs(id, departureArrivals)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindToLifecycle())
                .subscribe(new NextObserver<TrainResponse>() {
                    @Override
                    public void onError(Throwable e) {
                        Utils.logException(e);
                        mRefresh.setRefreshing(false);
                    }

                    @Override
                    public void onNext(TrainResponse response) {
                        if (departureArrivals.equals(Config.EXTRA_DEPARTURES)) {
                            mTrains.addAll(response.departures());
                        } else {
                            mTrains.addAll(response.arrivals());
                        }

                        mTrainAdapter.notifyDataSetChanged();

                        mRefresh.setRefreshing(false);
                    }
                });
    }
}
