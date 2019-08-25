package com.gonzaloaune.cordova.gcm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.gms.iid.InstanceIDListenerService;

public class GPPInstanceIDListenerService extends InstanceIDListenerService {

    private static final String TAG = "GPPInstanceIDLS";

    // [refresh_token 시작]
    @Override
    public void onTokenRefresh() {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String senderId = sharedPreferences.getString(GCMPushPlugin.SENDER_ID_KEY, null);

        if (senderId != null) {
            sharedPreferences.edit().putBoolean(GCMPushPlugin.REFRESH_TOKEN_KEY, true).apply();

            // Fetch updated Instance ID token and notify our app's server of any changes (if applicable).
            Intent intent = new Intent(this, GPPRegistrationIntentService.class);
            intent.putExtra(GCMPushPlugin.SENDER_ID_KEY, senderId);
            startService(intent);
            return;
        }
    }
    // [refresh_token 끝]
}
