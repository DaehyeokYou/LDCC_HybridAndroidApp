package com.gonzaloaune.cordova.gcm;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class GPPActivity extends Activity {
    private static String TAG = "GPPActivity";
    private String market;
    private String lat;
    private String lng;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        market = extras.getString("market");
        lat = extras.getString("lat");
        lng = extras.getString("lng");
        if (extras != null) {
            String notificationExtras = extras.getString("extra");

            Intent intent = new Intent(GCMPushPlugin.MSG_RECEIVED_BROADCAST_KEY);
            intent.putExtra("data", notificationExtras);

            Log.d(TAG, "Booting GPPActivity with data: "+notificationExtras);

            SharedPreferences sharedPreferences =
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            sharedPreferences.edit().putString(GCMPushPlugin.LAST_PUSH_KEY, notificationExtras).commit();

            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }

        finish();

        forceMapActivityReload();
    }

    private void forceMapActivityReload() {
        Intent intent = new Intent(this, Map.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("market",market);
        intent.putExtra("lat",lat);
        intent.putExtra("lng",lng);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        final NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

}
