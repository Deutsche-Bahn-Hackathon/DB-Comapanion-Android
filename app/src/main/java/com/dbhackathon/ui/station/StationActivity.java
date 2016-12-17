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
    public static final String BUNDLE_STATION_ID = "station_id";

    public static final String BUNDLE_DEPARTURES_ARRIVALS = "departures_arrivals";
    public static final String BUNDLE_DEPARTURES = "departures";
    public static final String BUNDLE_ARRIVALS = "departures";

    private DepartureArrivalFragment mDepartureFragment;
    private DepartureArrivalFragment mArrivalFragment;

    @BindView(R.id.viewpager) ViewPager mViewPager;
    @BindView(R.id.tabs) TabLayout mTabLayout;

    private Station mStation;

    private TabsAdapter mAdapter;

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

        departures.putString(BUNDLE_DEPARTURES_ARRIVALS, BUNDLE_DEPARTURES);
        departures.putString(BUNDLE_STATION_ID, String.valueOf(mStation.id()));

        arrivals.putString(BUNDLE_DEPARTURES_ARRIVALS, BUNDLE_ARRIVALS);
        arrivals.putString(BUNDLE_STATION_ID, String.valueOf(mStation.id()));

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
