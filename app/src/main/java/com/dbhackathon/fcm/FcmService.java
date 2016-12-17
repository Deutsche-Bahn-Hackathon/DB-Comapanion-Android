package com.dbhackathon.fcm;

import com.dbhackathon.fcm.command.CoffeeCommand;
import com.dbhackathon.fcm.command.FcmCommand;
import com.dbhackathon.fcm.command.NotificationCommand;
import com.dbhackathon.fcm.command.TestCommand;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

/**
 * This {@link android.app.Service} is launched when a new GCM message arrives. It will then select
 * the appropriate command from a defined list of commands. The command will then handle the
 * GCM message and process it further.
 *
 * @author Alex Lardschneider
 */
public class FcmService extends FirebaseMessagingService {

    private static final Map<String, FcmCommand> MESSAGE_RECEIVERS;

    static {
        Map<String, FcmCommand> receivers = new HashMap<>();
        receivers.put("test", new TestCommand());
        receivers.put("notification", new NotificationCommand());
        receivers.put("coffee", new CoffeeCommand());

        MESSAGE_RECEIVERS = Collections.unmodifiableMap(receivers);
    }

    @Override
    public void onMessageReceived(RemoteMessage message) {
        Timber.e("onMessageReceived()");

        String receiver = message.getData().get("receiver");
        String topic = message.getFrom();

        FcmCommand command = MESSAGE_RECEIVERS.get(receiver);
        if (command == null) {
            Timber.e("Unknown command received: %s", receiver);
        } else {
            command.execute(this, message.getData());
        }
    }
}