package com.gonzaloaune.cordova.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.ydh.example.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Set;

public class GPPListenerService extends GcmListenerService {

    private final String TAG = "GPPListenerService";

    // [receive_message 시작]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        JSONObject jsonObject = new JSONObject();
        final Set<String> keys = data.keySet();
        for (String key : keys) {
            try {
                jsonObject.put(key, data.getString(key));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Log.d(TAG, "v,:"+jsonObject.toString());

        sendNotification(jsonObject);
    }
    // [receive_message 끝]


    private void sendNotification(JSONObject message) {
        Intent notificationIntent = new Intent(this, GPPActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        notificationIntent.putExtra("extra", message.optString("extra").toString());
        notificationIntent.putExtra("market", message.optString("title").toString());
        notificationIntent.putExtra("lat", message.optString("latitude").toString());
        notificationIntent.putExtra("lng", message.optString("longitude").toString());

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        BitmapDrawable drawable = (BitmapDrawable) getResources().getDrawable(R.drawable.icon);
        Bitmap bitmap = drawable.getBitmap();

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(getApplicationInfo().icon)
                .setContentTitle(message.optString("title"))
                .setContentText(message.optString("message"))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setVisibility(1)
                .setVibrate(new long[]{1000, 1000})
                .setContentIntent(contentIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());

        // 잠금화면 깨우기
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
        wl.acquire(15000);

        //서비스 -> 액티비티(NotificationAlertDialog) 실행
        Intent intent = new Intent(this, NotificationAlertDialog.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("title",message.optString("title"));
        intent.putExtra("message",message.optString("message"));
        startActivity(intent);
    }

}
