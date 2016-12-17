package com.dbhackathon.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.dbhackathon.Config;
import com.dbhackathon.R;
import com.dbhackathon.data.model.Station;
import com.dbhackathon.data.model.StationResponse;
import com.dbhackathon.data.network.RestClient;
import com.dbhackathon.data.network.TrainApi;
import com.dbhackathon.ui.BaseActivity;
import com.dbhackathon.ui.station.StationActivity;
import com.dbhackathon.util.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Retrofit;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class MainActivity extends BaseActivity implements Utils.ActionListener<Station> {

    @BindView(R.id.recycler) RecyclerView mRecyclerView;

    private StationAdapter mAdapter;
    private Subscription mSubscription;
    private Retrofit mRetrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mRetrofit = RestClient.ADAPTER;

        mAdapter = new StationAdapter();
        mAdapter.setActionListener(this);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);

        loadStations();
    }

    private void loadStations() {
        if (mSubscription != null) {
            mSubscription.unsubscribe();
        }

        TrainApi trainApi = mRetrofit.create(TrainApi.class);

        mSubscription = trainApi.getStations()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindToLifecycle())
                .subscribe(new Subscriber<StationResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
//                        DialogFactory.createErrorDialog(MainActivity.this, e.getMessage()).create();

                        Timber.e(e, "Could not load stations!");
                    }

                    @Override
                    public void onNext(StationResponse stationResponse) {
                        mAdapter.setItems(stationResponse.stations());
                    }
                });
    }

    @Override
    protected int getNavItem() {
        return NAVDRAWER_ITEM_MAIN;
    }

    @Override
    public void onClick(Station station) {
        Intent intent = new Intent(this, StationActivity.class);

        intent.putExtra(Config.EXTRA_STATION, station);

        startActivity(intent);
    }
}
