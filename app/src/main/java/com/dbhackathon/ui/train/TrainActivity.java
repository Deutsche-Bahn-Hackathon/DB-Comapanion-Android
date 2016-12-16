package com.dbhackathon.ui.train;

import android.content.Intent;
import android.os.Bundle;

import com.dbhackathon.R;
import com.dbhackathon.data.model.Train;
import com.dbhackathon.ui.BaseActivity;

/**
 * Created on 12/16/16.
 *
 * @author Martin Fink
 */

public class TrainActivity extends BaseActivity {

    public static final String BUNDLE_TRAIN = "train";

    private Train mTrain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_train);

        Intent intent = getIntent();

        Bundle bundle = intent.getExtras();

        if (bundle == null) {
            throw new IllegalArgumentException("Not all extras are satisifed!");
        }

        mTrain = bundle.getParcelable(BUNDLE_TRAIN);

        if (mTrain == null) {
            throw new IllegalArgumentException("BUNDLE_TRAIN cannot be null!");
        }
    }

    @Override
    protected int getNavItem() {
        return NAVDRAWER_ITEM_INVALID;
    }
}
