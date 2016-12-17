package com.dbhackathon.ui.train;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.dbhackathon.R;
import com.dbhackathon.data.model.Train;
import com.dbhackathon.ui.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TrainActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.train_action_alarm) RelativeLayout mActionAlarm;
    @BindView(R.id.train_action_bar) RelativeLayout mActionBars;
    @BindView(R.id.train_action_toilets) RelativeLayout mActionToilets;
    @BindView(R.id.train_action_attractions) RelativeLayout mActionAttractions;
    @BindView(R.id.train_action_survey) RelativeLayout mActionSurveys;
    @BindView(R.id.train_action_statistics) RelativeLayout mActionStatistics;

    private Train mTrain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_train);
        ButterKnife.bind(this);

        Intent intent = getIntent();

        /*if (!intent.hasExtra(Config.EXTRA_TRAIN)) {
            Timber.e("Missing intent extra %s", Config.EXTRA_TRAIN);
            finish();
            return;
        }

        mTrain = intent.getParcelableExtra(Config.EXTRA_TRAIN);*/

        mActionAlarm.setOnClickListener(this);
        mActionBars.setOnClickListener(this);
        mActionToilets.setOnClickListener(this);
        mActionAttractions.setOnClickListener(this);
        mActionSurveys.setOnClickListener(this);
        mActionStatistics.setOnClickListener(this);
    }

    @Override
    protected int getNavItem() {
        return NAVDRAWER_ITEM_TRAIN;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }
}
