package com.dbhackathon.fcm.command;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.Map;

import timber.log.Timber;

public class CoffeeCommand implements FcmCommand {

    @Override
    public void execute(Context context, @NonNull Map<String, String> data) {
        Timber.e("Received GCM coffee message: extraData=%s", data);
    }
}
