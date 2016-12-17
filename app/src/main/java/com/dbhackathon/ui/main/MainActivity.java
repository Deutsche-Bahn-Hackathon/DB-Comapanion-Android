package com.dbhackathon.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.dbhackathon.Config;
import com.dbhackathon.R;
import com.dbhackathon.data.model.Station;
import com.dbhackathon.data.model.StationResponse;
import com.dbhackathon.data.network.RestClient;
import com.dbhackathon.data.network.TrainApi;
import com.dbhackathon.ui.BaseActivity;
import com.dbhackathon.ui.station.StationDetailsActivity;
import com.dbhackathon.ui.widget.RecyclerItemDivider;
import com.dbhackathon.util.NextObserver;
import com.dbhackathon.util.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends BaseActivity implements Utils.ActionListener<Station> {

    @BindView(R.id.recycler) RecyclerView mRecyclerView;
    @BindView(R.id.refresh) SwipeRefreshLayout mRefresh;

    private List<Station> mItems;
    private StationAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mRefresh.setColorSchemeResources(Config.REFRESH_COLORS);
        mRefresh.setOnRefreshListener(this::loadStations);

        mItems = new ArrayList<>();
        mAdapter = new StationAdapter(mItems);

        mAdapter.setActionListener(this);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new RecyclerItemDivider(this));

        loadStations();
    }

    private void loadStations() {
        mRefresh.setRefreshing(true);

        TrainApi trainApi = RestClient.ADAPTER.create(TrainApi.class);
        trainApi.getStations()
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NextObserver<StationResponse>() {
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        mRefresh.setRefreshing(false);
                    }

                    @Override
                    public void onNext(StationResponse response) {
                        mItems.clear();
                        mItems.addAll(response.stations());

                        mAdapter.notifyDataSetChanged();

                        mRefresh.setRefreshing(false);
                    }
                });
    }

    @Override
    protected int getNavItem() {
        return NAVDRAWER_ITEM_STATIONS;
    }

    @Override
    public void onClick(Station station) {
        Intent intent = new Intent(this, StationDetailsActivity.class);
        intent.putExtra(Config.EXTRA_STATION, station);
        startActivity(intent);
    }
}
