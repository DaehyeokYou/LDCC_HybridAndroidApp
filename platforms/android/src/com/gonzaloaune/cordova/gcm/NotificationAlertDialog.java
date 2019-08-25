package com.gonzaloaune.cordova.gcm;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import com.ydh.example.R;

/**
 * Created by n on 2016-02-18.
 */
public class NotificationAlertDialog extends Activity {
    private String product;
    private String market;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            market = extras.getString("title");
            product = extras.getString("message");
            Log.d("롯데_푸쉬", market+"에서 고객님의 장바구니에 있는 " + product + "를 판매하고 있습니다.");
            if (market != null && product != null){
                showAlertDialg();
            }
        }
    }
    public void showAlertDialg() {
        AlertDialog.Builder alert = new AlertDialog.Builder(NotificationAlertDialog.this);
        alert.setTitle("Wish Push");
        alert.setIcon(R.drawable.wishpush);
        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();     //닫기
                finish();

            }
        });
        alert.setMessage(market+"에서 고객님의 장바구니에 있는 " + product + "를 판매하고 있습니다.");
        alert.show();
    }


}
