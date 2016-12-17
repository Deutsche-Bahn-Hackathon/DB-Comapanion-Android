package com.dbhackathon.ui.station;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;

import com.dbhackathon.Config;
import com.dbhackathon.R;
import com.dbhackathon.beacon.station.StationBeacon;
import com.dbhackathon.beacon.station.StationBeaconHandler;
import com.dbhackathon.data.model.Station;
import com.dbhackathon.ui.BaseActivity;
import com.dbhackathon.ui.widget.TabsAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class StationDetailsActivity extends BaseActivity {

    private StationDetailsFragment mDepartureFragment;
    private StationDetailsFragment mArrivalFragment;

    @BindView(R.id.collapsing_toolbar) CollapsingToolbarLayout mCollapsing;
    @BindView(R.id.viewpager) ViewPager mViewPager;
    @BindView(R.id.tabs) TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_station);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        Station station = intent.getParcelableExtra(Config.EXTRA_STATION);

        if (station == null) {
            StationBeacon beacon = StationBeaconHandler.getInstance(this).getCurrentStation();
            if (beacon != null) {
                Timber.e("Got station from beacon");
                station = beacon.station();
            }
        }

        TabsAdapter adapter = new TabsAdapter(getSupportFragmentManager());

        mViewPager.setOffscreenPageLimit(3);
        mTabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this, android.R.color.white));

        if (mDepartureFragment == null) {
            mDepartureFragment = new StationDetailsFragment();
        }

        if (mArrivalFragment == null) {
            mArrivalFragment = new StationDetailsFragment();
        }

        if (station != null) {
            parseStationData(station);
        } else {
            new AlertDialog.Builder(this, R.style.DialogStyle)
                    .setTitle("No station nearby")
                    .setMessage("Make sure you are within a station and bluetooth is enabled.")
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss())
                    .create()
                    .show();
        }

        adapter.addFragment(mDepartureFragment, "Departures");
        adapter.addFragment(mArrivalFragment, "Arrivals");

        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    protected int getNavItem() {
        return NAVDRAWER_ITEM_STATION_CURRENT;
    }

    private void parseStationData(Station station) {
        mCollapsing.setTitle(station.name());

        Bundle departures = new Bundle();
        Bundle arrivals = new Bundle();

        departures.putString(Config.EXTRA_DEPARTURES_ARRIVALS, Config.EXTRA_DEPARTURES);
        departures.putString(Config.EXTRA_STATION_ID, String.valueOf(station.id()));

        arrivals.putString(Config.EXTRA_DEPARTURES_ARRIVALS, Config.EXTRA_ARRIVALS);
        arrivals.putString(Config.EXTRA_STATION_ID, String.valueOf(station.id()));

        mDepartureFragment.setArguments(departures);
        mArrivalFragment.setArguments(arrivals);
    }
}
