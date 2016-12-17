package com.dbhackathon.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.dbhackathon.Config;
import com.dbhackathon.beacon.BeaconStorage;
import com.dbhackathon.beacon.notification.CurrentTrip;
import com.dbhackathon.data.model.Train;
import com.dbhackathon.ui.main.MainActivity;
import com.dbhackathon.ui.train.TrainActivity;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CurrentTrip currentTrip = BeaconStorage.getInstance(this).getCurrentTrip();
        if (currentTrip != null) {
            Train train = currentTrip.toTrain();

            Intent intent = new Intent(this, TrainActivity.class);
            intent.putExtra(Config.EXTRA_TRAIN, train);

            startActivity(intent);
            finish();

            return;
        }

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
