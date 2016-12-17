package com.dbhackathon.fcm.command;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.Map;

import timber.log.Timber;

/**
 * Test command because debugging is fun ;-)
 *
 * @author Alex Lardschneider
 */
public class TestCommand implements FcmCommand {

    @Override
    public void execute(Context context, @NonNull Map<String, String> data) {
        Timber.e("Received GCM test message: extraData=%s", data);
    }
}
