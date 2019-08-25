package com.gonzaloaune.cordova.gcm;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ydh.example.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Map extends Activity {
    GoogleMap mGoogleMap;
    private String market;
    private String lat;
    private String lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Bundle extras = getIntent().getExtras();
        market = extras.getString("market");
        lat = extras.getString("lat");
        lng = extras.getString("lng");

        mGoogleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // LocationManager 객체 생성
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // GPSListener(LocationListener) 객체 생성
        GPSListener gpsListener = new GPSListener();

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, gpsListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, gpsListener);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new HttpAsyncTask().execute("https://www.lotteshop.tk/php_db/points.php");
                dialog.dismiss();     //닫기
            }
        });
        alert.setMessage("포인트 10점 추가");
        alert.show();
    }

    public class GPSListener implements LocationListener {
        public void onLocationChanged(Location location) {
            mGoogleMap.clear();
            LatLng myLoc = new LatLng(location.getLatitude(), location.getLongitude());
            LatLng shopLoc = new LatLng(Float.parseFloat(lat), Float.parseFloat(lng));

            // 내 위치로 카메라 이동
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLoc, 15));
            // 내 위치 마커
            MarkerOptions marker = new MarkerOptions();
            marker.title("내 위치");
            marker.position(myLoc);
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));
            marker.draggable(false);
            mGoogleMap.addMarker(marker).showInfoWindow();

            // 오프라인 매장 마커
            MarkerOptions shop = new MarkerOptions();
            shop.title(market);
            shop.position(shopLoc);
            shop.draggable(false);
            mGoogleMap.addMarker(shop).showInfoWindow();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    }
    public static String POST(String url){
        InputStream inputStream = null;
        String result = "";
        try {
            // 1. HttpClient 생성
            HttpClient httpclient = new DefaultHttpClient();
            // 2. POST request 생성
            HttpPost httpPost = new HttpPost(url);
            String json = "";
            // 3. 전송할 jsonObject 생성
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("flag", "55");
            // 4. JSonObject -> String
            json = jsonObject.toString();

            // 5. StringEntity에 json String 설정
            StringEntity se = new StringEntity(json);

            // 6. httpPost Entity 설정
            httpPost.setEntity(se);

            // 7. 서버 헤더 설정
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            // 8. URL로 전송
            HttpResponse httpResponse = httpclient.execute(httpPost);

            // 9. 수신
            inputStream = httpResponse.getEntity().getContent();

            // 10. inputstream -> string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }
        // 11. return result
        return result;
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return POST(urls[0]);
        }
        @Override
        protected void onPostExecute(String result) {
            //
        }
    }
    // inputStream을 String으로 convertings
    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;
        inputStream.close();
        return result;
    }
}
