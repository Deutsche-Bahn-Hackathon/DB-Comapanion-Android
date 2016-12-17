package com.dbhackathon.ui.main;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.dbhackathon.R;
import com.dbhackathon.ui.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {

    @BindView(R.id.recycler) RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);


    }

    @Override
    protected int getNavItem() {
        return NAVDRAWER_ITEM_MAIN;
    }
}
