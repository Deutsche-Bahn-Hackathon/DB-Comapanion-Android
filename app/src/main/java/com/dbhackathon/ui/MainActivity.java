package com.dbhackathon.ui;

import android.os.Bundle;

import com.dbhackathon.R;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO: 12/16/16 change this to activity_main
        setContentView(R.layout.activity_train);
    }

    @Override
    protected int getNavItem() {
        return NAVDRAWER_ITEM_MAIN;
    }
}
