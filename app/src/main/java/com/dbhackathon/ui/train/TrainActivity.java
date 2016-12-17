package com.dbhackathon.ui.train;

import android.content.Intent;
import android.os.Bundle;

import com.dbhackathon.R;
import com.dbhackathon.data.model.Train;
import com.dbhackathon.ui.BaseActivity;

public class TrainActivity extends BaseActivity {

    private Train mTrain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_train);

        Intent intent = getIntent();

        /*if (!intent.hasExtra(Config.EXTRA_TRAIN)) {
            Timber.e("Missing intent extra %s", Config.EXTRA_TRAIN);
            finish();
            return;
        }

        mTrain = intent.getParcelableExtra(Config.EXTRA_TRAIN);*/
    }

    @Override
    protected int getNavItem() {
        return NAVDRAWER_ITEM_TRAIN;
    }
}
