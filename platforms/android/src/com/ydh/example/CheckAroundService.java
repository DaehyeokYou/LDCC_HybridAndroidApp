package com.ydh.example;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.StrictMode;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CheckAroundService extends Service {
    private boolean isRunning = false;      //
    dbHelper helper;
    SQLiteDatabase db;
    public static Context mContext;

    public CheckAroundService() {
    }
    @Override
    public void onCreate() {
        mContext = this;
        helper = new dbHelper(this);

        try {
            db = helper.getWritableDatabase();
        } catch (SQLiteException e) {
            db = helper.getReadableDatabase();
        }
        // DB Table 데이터 전체 삭제
        removeAllTableData();
        helper.onUpgrade(db, 0, 1);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        new HttpAsyncTask().execute("https://www.lotteshop.tk/php_db/latlng_info.php","ydh");

        isRunning = true;
        Log.d("쳌어롸운드서비스", "크리에잍");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startID) {
        Log.d("쳌어롸운드서비스","스타트");
        // gps 계산
        // LocationManager 객체 생성
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // GPSListener(LocationListener) 객체 생성
        GPSListener gpsListener = new GPSListener();

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, gpsListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, gpsListener);
        return Service.START_STICKY;
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        Log.d("쳌어롸운드서비스","온바인드");
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public void onDestroy() {
        isRunning = false;
        Log.d("쳌어롸운드서비스","디스트로이");
    }
    public class GPSListener implements LocationListener {
        public void onLocationChanged(Location location) {
            Log.d("쥐피에스","온로케이션첸지드");
            // 0,1,2 index
            float[] results;
            results = new float[]{(float)0.0, (float)0.0, (float)0.0 };
            try {
                String datas = selectAll();
                JSONArray jArr = new JSONArray(datas);
                if (jArr != null) {
                    for (int i = 0; i < jArr.length(); i++) {
                        JSONObject jObj = jArr.getJSONObject(i);
                        String market_idx = jObj.getString("market_idx");
                        Float endLat = Float.parseFloat(jObj.getString("latitude"));
                        Float endLng = Float.parseFloat(jObj.getString("longitude"));
                        int flag = jObj.getInt("flag");
                        String product_idx = jObj.getString("product_idx");
                        if (flag == 0) {
                            // 내 위치와 오프라인 매장의 거리 비교
                            Location.distanceBetween(location.getLatitude(), location.getLongitude(), endLat, endLng, results);
                            if (results[0] < 300) { // 거리가 300 미터 이내로 나오면
                                String idxs = market_idx + "," + product_idx;
                                // push
                                new HttpAsyncTask().execute("https://www.lotteshop.tk/php_db/push.php", idxs);
                                updateFlag(Integer.parseInt(market_idx), Integer.parseInt(product_idx));
                                break;
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
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

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return POST(urls[0],urls[1]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            if(result != "ydh") {
                // Json Parsing
                JSONArray jArr = null;
                try {
                    jArr = new JSONArray(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (jArr != null) {
                    for (int i = 0; i < jArr.length(); i++) {
                        try {
                            JSONObject latlngObject = jArr.getJSONObject(i);
                            int market_idx = latlngObject.getInt("market_idx");
                            Float endLat = Float.parseFloat(latlngObject.getString("latitude"));
                            Float endLng = Float.parseFloat(latlngObject.getString("longitude"));
                            int product_idx = latlngObject.getInt("product_idx");
                            // DB Insert
                            insertData(market_idx, endLat, endLng, 0, product_idx);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    public static String POST(String url, String idxs){
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
            if(idxs != "ydh") {
                String[] push_idxs = idxs.split(",");
                jsonObject.accumulate("market_idx", push_idxs[0]);
                jsonObject.accumulate("product_idx", push_idxs[1]);
            }
            else {
                jsonObject.accumulate("flag", "1");
            }
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
            if(idxs == "ydh") {
                if (inputStream != null)
                    result = convertInputStreamToString(inputStream);
                else
                    result = "Did not work!";
            } else {
                result = "ydh";
            }
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }
        // 11. return result
        return result;
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
    // Data 추가
    public void insertData(int market_idx, Float lat, Float lng, int flag, int product_idx){
        //Log.d("디비","인설트데이타");
        String sql = "insert into myTable values(null, " + market_idx + ", " + lat + ", " + lng + ", " + flag + ", " + product_idx + ");";
        db.execSQL(sql);
    }
    // Data 삭제
    public void removeAllTableData(){
        //Log.d("디비","리무브테이블데이타");
        String sql = "delete from myTable;";
        db.execSQL(sql);
    }
    // Flag Update
    public void updateFlag(int market_idx, int product_idx) {
        //Log.d("디비","업데이트플래그");
        String sql = "update myTable set flag = 1 where market_idx = "+market_idx +" and product_idx = " + product_idx +";";
        db.execSQL(sql);
    }
    // 모든 Data 읽기
    public String selectAll() throws JSONException {
        JSONArray jsonArr = new JSONArray();


        String sql = "select * from myTable;";
        Cursor results = db.rawQuery(sql, null);

        results.moveToFirst();

        while(!results.isAfterLast()){
            int market_idx = results.getInt(1);
            float lat = results.getFloat(2);
            float lng = results.getFloat(3);
            int flag = results.getInt(4);
            int product_idx = results.getInt(5);

            results.moveToNext();
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("market_idx", market_idx);
            jsonObj.put("latitude",lat);
            jsonObj.put("longitude",lng);
            jsonObj.put("flag", flag);
            jsonObj.put("product_idx", product_idx);
            jsonArr.put(jsonObj);
        }
        results.close();
        String jString = jsonArr.toString();
        return jString;
    }
}
