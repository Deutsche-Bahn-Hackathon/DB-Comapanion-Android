package com.dbhackathon.ui.station;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;

import com.dbhackathon.R;
import com.dbhackathon.data.model.Station;
import com.dbhackathon.ui.BaseActivity;
import com.dbhackathon.ui.widget.TabsAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StationActivity extends BaseActivity {

    public static final String BUNDLE_STATION = "station";
    public static final String BUNDLE_TRAINS = "trains";

    private DepartureArrivalFragment mDepartureFragment;
    private DepartureArrivalFragment mArrivalFragment;

    @BindView(R.id.viewpager) ViewPager mViewPager;
    @BindView(R.id.tabs) TabLayout mTabLayout;

    private Station mStation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();

        if (bundle == null) {
            throw new IllegalArgumentException("Some arguments are missing!");
        }

        mStation = bundle.getParcelable(BUNDLE_STATION);

        if (mStation == null) {
            throw new IllegalArgumentException("BUNDLE_STATION cannot be null!");
        }

        setContentView(R.layout.activity_station);
        ButterKnife.bind(this);

        TabsAdapter mAdapter = new TabsAdapter(getSupportFragmentManager());

        mViewPager.setOffscreenPageLimit(3);
        mTabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this, android.R.color.white));

        if (mDepartureFragment == null) {
            mDepartureFragment = new DepartureArrivalFragment();
        }

        if (mArrivalFragment == null) {
            mArrivalFragment = new DepartureArrivalFragment();
        }

        Bundle arrivals = new Bundle();
        Bundle departures = new Bundle();

//        arrivals.putParcelableArrayList(BUNDLE_TRAINS, mStation.getArrivals());
//        departures.putParcelableArrayList(BUNDLE_TRAINS, mStation.getDepartures());

        mAdapter.addFragment(mDepartureFragment, "Departures");
        mAdapter.addFragment(mArrivalFragment, "Arrival");

        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    protected int getNavItem() {
        return NAVDRAWER_ITEM_STATION;
    }
}
