package com.dbhackathon.ui;

import android.os.Bundle;

import com.dbhackathon.R;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
    }

    @Override
    protected int getNavItem() {
        return NAVDRAWER_ITEM_MAIN;
    }
}
