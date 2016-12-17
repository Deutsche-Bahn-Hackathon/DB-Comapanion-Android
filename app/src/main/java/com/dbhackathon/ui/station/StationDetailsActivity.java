package com.dbhackathon.ui.station;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.dbhackathon.Config;
import com.dbhackathon.R;
import com.dbhackathon.data.model.Station;
import com.dbhackathon.ui.widget.TabsAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class StationDetailsActivity extends AppCompatActivity {

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
        if (!intent.hasExtra(Config.EXTRA_STATION)) {
            Timber.e("Missing intent extra %s", Config.EXTRA_STATION);
            finish();
            return;
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        Station station = intent.getParcelableExtra(Config.EXTRA_STATION);
        TabsAdapter adapter = new TabsAdapter(getSupportFragmentManager());

        mCollapsing.setTitle(station.name());

        mViewPager.setOffscreenPageLimit(3);
        mTabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this, android.R.color.white));

        if (mDepartureFragment == null) {
            mDepartureFragment = new StationDetailsFragment();
        }

        if (mArrivalFragment == null) {
            mArrivalFragment = new StationDetailsFragment();
        }

        Bundle departures = new Bundle();
        Bundle arrivals = new Bundle();

        departures.putString(Config.EXTRA_DEPARTURES_ARRIVALS, Config.EXTRA_DEPARTURES);
        departures.putString(Config.EXTRA_STATION_ID, String.valueOf(station.id()));

        arrivals.putString(Config.EXTRA_DEPARTURES_ARRIVALS, Config.EXTRA_ARRIVALS);
        arrivals.putString(Config.EXTRA_STATION_ID, String.valueOf(station.id()));

        mDepartureFragment.setArguments(departures);
        mArrivalFragment.setArguments(arrivals);

        adapter.addFragment(mDepartureFragment, "Departures");
        adapter.addFragment(mArrivalFragment, "Arrivals");

        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }
}
