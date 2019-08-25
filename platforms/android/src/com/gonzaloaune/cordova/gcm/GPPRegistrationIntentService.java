package com.gonzaloaune.cordova.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

public class GPPRegistrationIntentService extends IntentService {

    private static final String TAG = "GPPRegIntentService";

    public GPPRegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            synchronized (TAG) {
                // [GCM register 시작]
                // [get_token 시작]
                final String projectId = intent.getExtras().getString(GCMPushPlugin.SENDER_ID_KEY);
                InstanceID instanceID = InstanceID.getInstance(this);
                String token = instanceID.getToken(projectId,
                        GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                // [get_token 끝]
                sharedPreferences.edit().putBoolean(GCMPushPlugin.SENT_TOKEN_KEY, true).apply();
                sharedPreferences.edit().putString(GCMPushPlugin.GCM_TOKEN_KEY, token).apply();
                // [register_for_gcm 끝]
            }
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            sharedPreferences.edit().putBoolean(GCMPushPlugin.SENT_TOKEN_KEY, false).apply();
        }
        Intent registrationComplete = new Intent(GCMPushPlugin.REG_COMPLETE_BROADCAST_KEY);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

}
