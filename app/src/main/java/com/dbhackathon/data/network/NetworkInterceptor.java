package com.dbhackathon.data.network;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;

import com.dbhackathon.BuildConfig;
import com.dbhackathon.util.Utils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import timber.log.Timber;

/**
 * Intercepts each call to the rest api and adds the user agent header to the request.
 * The user agent consists of the app title, followed by the app version title and code.
 * A token is needed to identify the user which made the request when trying to download trips.
 * The auth header is used to check if the request was made from a valid client and block
 * 3rd party users from using the api.
 *
 * @author Alex Lardschneider
 */
class NetworkInterceptor implements Interceptor {

    private final Context mContext;

    private String mAndroidId;

    NetworkInterceptor(Context context) {
        mContext = context;
    }

    @Override
    @SuppressLint("HardwareIds")
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();

        Request.Builder newRequest = originalRequest
                .newBuilder()
                .addHeader("User-Agent", "SasaBus Android")
                .addHeader("X-Device-Id", getAndroidId())
                .addHeader("X-Device", Build.MODEL)
                .addHeader("X-Language", Utils.locale(mContext))
                .addHeader("X-Serial", Build.SERIAL)
                .addHeader("X-Version-Code", String.valueOf(BuildConfig.VERSION_CODE))
                .addHeader("X-Version-Name", BuildConfig.VERSION_NAME);

        Request request = newRequest.build();

        Timber.w("%s: %s", request.method(), originalRequest.url());

        return chain.proceed(request);
    }

    @SuppressLint("HardwareIds")
    private String getAndroidId() {
        if (mAndroidId == null) {
            mAndroidId = Settings.Secure.getString(mContext.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
        }

        return mAndroidId;
    }
}