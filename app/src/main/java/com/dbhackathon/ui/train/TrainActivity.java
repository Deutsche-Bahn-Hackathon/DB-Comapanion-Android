package com.dbhackathon.ui.train;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dbhackathon.Config;
import com.dbhackathon.R;
import com.dbhackathon.data.model.Train;
import com.dbhackathon.ui.BaseActivity;
import com.dbhackathon.ui.alarm.AlarmActivity;
import com.dbhackathon.ui.toilet.ToiletActivity;
import com.dbhackathon.util.Settings;
import com.dbhackathon.util.Utils;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class TrainActivity extends BaseActivity implements View.OnClickListener,
        View.OnTouchListener {

    @BindView(R.id.train_title_text) TextView mTitleText;
    @BindView(R.id.train_departure_text) TextView mDepartureText;
    @BindView(R.id.train_arrival_text) TextView mArrivalText;

    @BindView(R.id.train_action_alarm) RelativeLayout mActionAlarm;
    @BindView(R.id.train_action_bar) RelativeLayout mActionBars;
    @BindView(R.id.train_action_toilets) RelativeLayout mActionToilets;
    @BindView(R.id.train_action_attractions) RelativeLayout mActionAttractions;
    @BindView(R.id.train_action_survey) RelativeLayout mActionSurveys;
    @BindView(R.id.train_action_statistics) RelativeLayout mActionStatistics;

    @BindView(R.id.train_action_alarm_delete) ImageView mDeleteAlarm;

    private Train mTrain;

    /**
     * Needed for the search activity circular reveal.
     */
    private float lastTouchX;
    private float lastTouchY;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_train);
        ButterKnife.bind(this);

        Intent intent = getIntent();

        if (!intent.hasExtra(Config.EXTRA_TRAIN)) {
            Timber.e("Missing intent extra %s", Config.EXTRA_TRAIN);
            finish();
            return;
        }

        if (!Settings.hasAlarms(this)) {
            mDeleteAlarm.setVisibility(View.GONE);
        }

        mTrain = intent.getParcelableExtra(Config.EXTRA_TRAIN);

        mActionAlarm.setOnClickListener(this);
        mActionBars.setOnClickListener(this);
        mActionToilets.setOnClickListener(this);
        mActionAttractions.setOnClickListener(this);
        mActionSurveys.setOnClickListener(this);
        mActionStatistics.setOnClickListener(this);

        mDeleteAlarm.setOnClickListener(this);

        mActionToilets.setOnTouchListener(this);
        mActionAlarm.setOnTouchListener(this);

        mTitleText.setText(getString(R.string.welcome_to_train, mTrain.name()));
        mDepartureText.setText(getString(R.string.train_from, "Munich Hbf"));
        mArrivalText.setText(getString(R.string.train_to, mTrain.stop()));
    }

    @Override
    protected int getNavItem() {
        return NAVDRAWER_ITEM_TRAIN;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.train_action_alarm:
                Intent intent = new Intent(this, AlarmActivity.class);
                intent.putExtra(ToiletActivity.EXTRA_X_POS, lastTouchX);
                intent.putExtra(ToiletActivity.EXTRA_Y_POS, lastTouchY);
                startActivity(intent);
                break;
            case R.id.train_action_bar:
                Toast.makeText(this, "Not implemented yet!", Toast.LENGTH_LONG).show();
                break;
            case R.id.train_action_toilets:
                intent = new Intent(this, ToiletActivity.class);
                intent.putExtra(Config.EXTRA_TRAIN, mTrain);
                intent.putExtra(ToiletActivity.EXTRA_X_POS, lastTouchX);
                intent.putExtra(ToiletActivity.EXTRA_Y_POS, lastTouchY);
                startActivity(intent);
                break;
            case R.id.train_action_attractions:
                Toast.makeText(this, "Not implemented yet!", Toast.LENGTH_LONG).show();
                break;
            case R.id.train_action_survey:
                scanQRCode();
                break;
            case R.id.train_action_statistics:
                Toast.makeText(this, "Not implemented yet!", Toast.LENGTH_LONG).show();
                break;
            case R.id.train_action_alarm_delete:
                new AlertDialog.Builder(this, R.style.DialogStyle)
                        .setTitle("Delete alert?")
                        .setMessage("Do you really want to delete this alert? This cannot be undone.")
                        .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                        .setPositiveButton("Delete", (dialog, which) -> {
                            mDeleteAlarm.setVisibility(View.GONE);
                            Settings.setHasAlarms(TrainActivity.this, false);
                            dialog.dismiss();
                        })
                        .create()
                        .show();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Config.CAMERA_PIC_REQUEST && data != null) {

            Bundle bundle = data.getExtras();

            if (bundle == null) {
                return;
            }

            Bitmap thumbnail = (Bitmap) bundle.get("data");

            BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(this)
                    .setBarcodeFormats(Barcode.DATA_MATRIX | Barcode.QR_CODE)
                    .build();

            if (barcodeDetector.isOperational() && thumbnail != null) {
                Frame frame = new Frame.Builder().setBitmap(thumbnail).build();

                SparseArray<Barcode> barcodes = barcodeDetector.detect(frame);

                if (barcodes.size() > 0) {
                    Utils.openCustomTab(barcodes.get(barcodes.keyAt(0)).displayValue, this);
                } else {
                    Snackbar.make(getMainContent(), getString(R.string.could_not_recognize_qr), Snackbar.LENGTH_INDEFINITE)
                            .setAction(R.string.retry, v -> scanQRCode())
                            .show();
                }
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            lastTouchX = event.getRawX();
            lastTouchY = event.getRawY();
        }

        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!Settings.hasAlarms(this)) {
            mDeleteAlarm.setVisibility(View.GONE);
        } else {
            mDeleteAlarm.setVisibility(View.VISIBLE);
        }
    }


    private void scanQRCode() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        startActivityForResult(cameraIntent, Config.CAMERA_PIC_REQUEST);
    }
}
