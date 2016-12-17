package com.dbhackathon.ui.train;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.SparseArray;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.dbhackathon.Config;
import com.dbhackathon.R;
import com.dbhackathon.data.model.Train;
import com.dbhackathon.ui.BaseActivity;
import com.dbhackathon.util.Utils;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

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
            case R.id.train_action_alarm:
                Toast.makeText(this, "Not implemented yet!", Toast.LENGTH_LONG).show();
                break;
            case R.id.train_action_bar:
                Toast.makeText(this, "Not implemented yet!", Toast.LENGTH_LONG).show();
                break;
            case R.id.train_action_toilets:
                new AlertDialog.Builder(this, R.style.DialogStyle)
                        .setTitle(getString(R.string.toilets))
                        .setMessage("Please walk in direction of travel for two wagons!")
                        .setNeutralButton(android.R.string.ok, null)
                        .create()
                        .show();
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
        }
    }

    private void scanQRCode() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        startActivityForResult(cameraIntent, Config.CAMERA_PIC_REQUEST);
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
}
