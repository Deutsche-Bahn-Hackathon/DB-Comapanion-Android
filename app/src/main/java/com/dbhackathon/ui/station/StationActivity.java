package com.dbhackathon.ui.station;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;

import com.dbhackathon.Config;
import com.dbhackathon.R;
import com.dbhackathon.data.model.Station;
import com.dbhackathon.ui.BaseActivity;
import com.dbhackathon.ui.widget.TabsAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class StationActivity extends BaseActivity {

    private DepartureArrivalFragment mDepartureFragment;
    private DepartureArrivalFragment mArrivalFragment;

    @BindView(R.id.viewpager) ViewPager mViewPager;
    @BindView(R.id.tabs) TabLayout mTabLayout;

    private Station mStation;

    private TabsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_station);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        if (!intent.hasExtra(Config.EXTRA_STATION)) {
            Timber.e("Missing intent extra %s", Config.EXTRA_STATION);
            finish();
            return;
        }

        mStation = intent.getParcelableExtra(Config.EXTRA_STATION);

        mAdapter = new TabsAdapter(getSupportFragmentManager());

        mViewPager.setOffscreenPageLimit(3);
        mTabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this, android.R.color.white));

        if (mDepartureFragment == null) {
            mDepartureFragment = new DepartureArrivalFragment();
        }

        if (mArrivalFragment == null) {
            mArrivalFragment = new DepartureArrivalFragment();
        }

        Bundle departures = new Bundle();
        Bundle arrivals = new Bundle();

        departures.putString(Config.EXTRA_DEPARTURES_ARRIVALS, Config.EXTRA_DEPARTURES);
        departures.putString(Config.EXTRA_STATION_ID, String.valueOf(mStation.id()));

        arrivals.putString(Config.EXTRA_DEPARTURES_ARRIVALS, Config.EXTRA_ARRIVALS);
        arrivals.putString(Config.EXTRA_STATION_ID, String.valueOf(mStation.id()));

        mDepartureFragment.setArguments(departures);
        mArrivalFragment.setArguments(arrivals);

        mAdapter.addFragment(mDepartureFragment, "Departures");
        mAdapter.addFragment(mArrivalFragment, "Arrivals");

        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    protected int getNavItem() {
        return NAVDRAWER_ITEM_STATION;
    }
}
