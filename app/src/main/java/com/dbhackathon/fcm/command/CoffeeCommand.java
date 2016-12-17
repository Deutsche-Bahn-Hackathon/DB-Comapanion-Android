package com.dbhackathon.fcm.command;

import android.content.Context;
import android.support.annotation.NonNull;

import com.dbhackathon.data.model.Coffee;
import com.dbhackathon.util.Notifications;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;

import timber.log.Timber;

public class CoffeeCommand implements FcmCommand {

    @Override
    public void execute(Context context, @NonNull Map<String, String> data) throws Exception {
        Timber.e("Received GCM coffee message: extraData=%s", data);

        String content = data.get("content");
        JSONArray json = new JSONArray(content);

        Coffee[] coffees = new Coffee[json.length()];

        for (int i = 0; i < json.length(); i++) {
            JSONObject object = json.getJSONObject(i);

            Coffee coffee = new Coffee();
            coffee.id = object.getString("id");
            coffee.name = object.getString("name");
            coffee.price = object.getDouble("price");

            coffees[i] = coffee;
        }

        Notifications.coffee(context, coffees);
    }
}
