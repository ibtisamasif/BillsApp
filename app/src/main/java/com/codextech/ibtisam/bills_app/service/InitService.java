package com.codextech.ibtisam.bills_app.service;

import android.app.Activity;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.codextech.ibtisam.bills_app.models.BPMerchant;
import com.codextech.ibtisam.bills_app.sync.MyURLs;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class InitService extends IntentService {
    public static final String TAG = "InitService";
    private int result = Activity.RESULT_CANCELED;
    public static final String RESULT = "result";
    public static final String NOTIFICATION = "com.example.ibtisam.myweatherapp";

    private Context mContext;
    private static RequestQueue queue;
    AtomicInteger requestsCounter;

//    boolean initError = false;

    public InitService() {
        super("InitService");
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        mContext = getApplicationContext();
        queue = Volley.newRequestQueue(mContext);
        requestsCounter = new AtomicInteger(0);
        return super.onStartCommand(intent, flags, startId);
    }

    // called asynchronously be Android
    @Override
    protected void onHandleIntent(Intent intent) {

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }

//        fetchMerchants();

        queue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                Log.d(TAG, "onRequestFinished: " + requestsCounter.get());
                requestsCounter.decrementAndGet();
                if (requestsCounter.get() == 0) {
                    Log.d(TAG, "onRequestFinished: +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                    publishResults(result);
                }
            }
        });

    }

//    private void fetchMerchants() {
//        Log.d(TAG, "fetchBPUniversityDataFunc: Fetching BPMerchant...");
//        final int MY_SOCKET_TIMEOUT_MS = 60000;
//        final String BASE_URL = MyURLs.GET_MERCHANTS;
//        Uri builtUri = Uri.parse(BASE_URL)
//                .buildUpon()
//                .appendQueryParameter("limit", "50000")
////                .appendQueryParameter("page", "1")
//                .build();
//        final String myUrl = builtUri.toString();
//        StringRequest sr = new StringRequest(Request.Method.GET, myUrl, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                Log.d(TAG, "onResponse() getBPUniversity: response = [" + response + "]");
//
//                try {
//                    JSONObject reader = new JSONObject(response);
//
//                    final String code = reader.optString("cod");
//                    if ("404".equals(code)) {
//                        Log.d(TAG, "onResponse: 404");
//                    }
//
//                    final String idString = reader.getJSONArray("weather").getJSONObject(0).getString("id");
//
////                    if (BPMerchant.getBPUniversityFromServerId(idString) == null) {
//
//                        BPMerchant newBPUniversity = city;
//
//                        newBPUniversity.setServerId(idString);
//                        String city = reader.getString("name");
//                        String lastUpdatedAt = reader.getString("dt");
//                        newBPUniversity.setLastUpdated(lastUpdatedAt);
//                        String country = "";
//                        JSONObject countryObj = reader.optJSONObject("sys");
//                        if (countryObj != null) {
//                            country = countryObj.getString("country");
//                            newBPUniversity.setSunrise(countryObj.getString("sunrise"));
//                            newBPUniversity.setSunset(countryObj.getString("sunset"));
//                        }
//                        newBPUniversity.setBPUniversity(city);
//                        newBPUniversity.setCountry(country);
//
//                        JSONObject main = reader.getJSONObject("main");
//
//                        newBPUniversity.setTemperature(main.getString("temp"));
//                        newBPUniversity.setDescription(reader.getJSONArray("weather").getJSONObject(0).getString("description"));
//                        JSONObject windObj = reader.getJSONObject("wind");
//                        newBPUniversity.setWind(windObj.getString("speed"));
//                        if (windObj.has("deg")) {
//                            newBPUniversity.setWindDirectionDegree(windObj.getDouble("deg"));
//                        } else {
//                            Log.e("parseTodayJson", "No wind direction available");
//                            newBPUniversity.setWindDirectionDegree(null);
//                        }
//                        newBPUniversity.setPressure(main.getString("pressure"));
//                        newBPUniversity.setHumidity(main.getString("humidity"));
//
//                        JSONObject rainObj = reader.optJSONObject("rain");
//                        String rain;
//                        if (rainObj != null) {
//                            rain = getRainString(rainObj);
//                        } else {
//                            JSONObject snowObj = reader.optJSONObject("snow");
//                            if (snowObj != null) {
//                                rain = getRainString(snowObj);
//                            } else {
//                                rain = "0";
//                            }
//                        }
//                        newBPUniversity.setRain(rain);
//                        newBPUniversity.setIcon(setWeatherIcon(Integer.parseInt(idString), Calendar.getInstance().get(Calendar.HOUR_OF_DAY)));
//                        newBPUniversity.save();
//                    TinyBus.from(getApplicationContext()).post(new BPUniversityEventModel());
////                    }
//                } catch (JSONException e) {
//                    Log.e("JSONException Data", response);
//                    e.printStackTrace();
//                }
//
//                result = Activity.RESULT_OK;
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e(TAG, "onErrorResponse: fetchBPUniversityDataFunc");
//            }
//        });
//        sr.setRetryPolicy(new DefaultRetryPolicy(
//                MY_SOCKET_TIMEOUT_MS,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        queue.add(sr);
//        requestsCounter.incrementAndGet();
//    }

    private void publishResults(int result) {
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(RESULT, result);
        sendBroadcast(intent);
    }
}