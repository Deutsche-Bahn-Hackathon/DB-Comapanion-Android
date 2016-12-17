package com.dbhackathon.fcm;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;

import timber.log.Timber;

/**
 * Called if InstanceID token is updated. This may occur if the security of
 * the previous token had been compromised. Note that this is also called
 * when the InstanceID token is initially generated, so this is where
 * you retrieve the token.
 *
 * @author Alex Lardschneider
 */
public class InstanceIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        String token = FirebaseInstanceId.getInstance().getToken();

        Timber.e("Got token: %s", token);

        FcmSettings.setGcmToken(this, token);

        FirebaseMessaging.getInstance().subscribeToTopic("general");

        Timber.e("Registration completed");
    }
}