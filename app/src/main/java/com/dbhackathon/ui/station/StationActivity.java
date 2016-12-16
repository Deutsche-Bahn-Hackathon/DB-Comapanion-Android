package com.dbhackathon.ui.station;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;

import com.dbhackathon.R;
import com.dbhackathon.ui.BaseActivity;
import com.dbhackathon.ui.widget.TabsAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StationActivity extends BaseActivity {

    private DepartureFragment mDepartureFragment;
    private DepartureFragment mArrivalFragment;

    @BindView(R.id.viewpager) ViewPager mViewPager;
    @BindView(R.id.tabs) TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_station);
        ButterKnife.bind(this);

        TabsAdapter mAdapter = new TabsAdapter(getSupportFragmentManager());

        mViewPager.setOffscreenPageLimit(3);
        mTabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this, android.R.color.white));

        if (mDepartureFragment == null) {
            mDepartureFragment = new DepartureFragment();
        }

        if (mArrivalFragment == null) {
            mArrivalFragment = new DepartureFragment();
        }

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
