package com.codextech.ibtisam.bills_app.service;

import android.app.Activity;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.codextech.ibtisam.bills_app.SessionManager;
import com.codextech.ibtisam.bills_app.events.BPMerchantEventModel;
import com.codextech.ibtisam.bills_app.events.BPSubscriberEventModel;
import com.codextech.ibtisam.bills_app.models.BPBiller;
import com.codextech.ibtisam.bills_app.models.BPMerchant;
import com.codextech.ibtisam.bills_app.sync.MyURLs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import de.halfbit.tinybus.TinyBus;


public class InitService extends IntentService {
    public static final String TAG = "InitService";
    private int result = Activity.RESULT_CANCELED;
    public static final String RESULT = "result";
    public static final String NOTIFICATION = "com.example.ibtisam.myweatherapp";

    private Context mContext;
    private static RequestQueue queue;
    AtomicInteger requestsCounter;
    SessionManager sessionManager;
//    boolean initError = false;

    public InitService() {
        super("InitService");
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        mContext = getApplicationContext();
        queue = Volley.newRequestQueue(mContext);
        sessionManager = new SessionManager(getApplicationContext());
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

        fetchMerchants();

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

    private void fetchMerchants() {
        Log.d(TAG, "fetchBPMerchantDataFunc: Fetching BPMerchant...");
        final int MY_SOCKET_TIMEOUT_MS = 60000;
        final String BASE_URL = MyURLs.GET_MERCHANTS;
        Uri builtUri = Uri.parse(BASE_URL)
                .buildUpon()
                .appendQueryParameter("limit", "50000")
//                .appendQueryParameter("page", "1")
                .build();
        final String myUrl = builtUri.toString();
        StringRequest sr = new StringRequest(Request.Method.GET, myUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse() getBPMerchant: response = [" + response + "]");
                try {
                    JSONObject jObj = new JSONObject(response);
                    int responseCode = jObj.getInt("responseCode");
                    if (responseCode == 200) {
                        JSONObject jObjReponse = jObj.getJSONObject("response");
                        JSONObject jObjPage = jObjReponse.getJSONObject("page");
                        String totalElements = jObjPage.getString("totalElements");

                        JSONArray jsonArrayData = jObjReponse.getJSONArray("data");
                        for (int i = 0; i < jsonArrayData.length(); i++) {
                            JSONObject jsonObjectOneMerchant = jsonArrayData.getJSONObject(i);
                            String merchant_id = jsonObjectOneMerchant.getString("merchant_id");
                            String merchant_name = jsonObjectOneMerchant.getString("merchant_name");
                            String merchant_adderss = jsonObjectOneMerchant.getString("merchant_adderss");
                            String merchant_logo = jsonObjectOneMerchant.getString("merchant_logo");
                            String merchant_status = jsonObjectOneMerchant.getString("merchant_status");
                            String updated_at = jsonObjectOneMerchant.getString("updated_at");

                            Log.d(TAG, "onResponse: merchant_id: " + merchant_id);
                            Log.d(TAG, "onResponse: merchant_name: " + merchant_name);
                            Log.d(TAG, "onResponse: merchant_adderss: " + merchant_adderss);
                            Log.d(TAG, "onResponse: merchant_logo: " + merchant_logo);
                            Log.d(TAG, "onResponse: merchant_status: " + merchant_status);
                            Log.d(TAG, "onResponse: updated_at: " + updated_at);

                            if (BPMerchant.getMerchantFromServerId(merchant_id) == null) {
                                BPMerchant tempMerchant = new BPMerchant();
                                tempMerchant.setServerId(merchant_id);
                                tempMerchant.setName(merchant_name);
                                tempMerchant.setServerId(merchant_id);
                                tempMerchant.setAddress(merchant_adderss);
                                tempMerchant.setLogo(merchant_logo);
                                tempMerchant.setStatus(merchant_status);
                                tempMerchant.setUpdatedAt(Calendar.getInstance().getTime());
                                tempMerchant.save();
                                TinyBus.from(getApplicationContext()).post(new BPMerchantEventModel());
                            }
                        }
                        fetchSubscribers();
                    }
                } catch (JSONException e) {
                    Log.e("JSONException Data", response);
                    e.printStackTrace();
                }
//                result = Activity.RESULT_OK;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: fetchBPMerchantsDataFunc");
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("Authorization", "Bearer " + sessionManager.getLoginToken());
                return params;
            }
        };
        sr.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(sr);
        requestsCounter.incrementAndGet();
    }

    private void fetchSubscribers() {
        Log.d(TAG, "fetchSubscribers: Fetching BPSubscribers...");
        final int MY_SOCKET_TIMEOUT_MS = 60000;
        final String BASE_URL = MyURLs.GET_SUBSCRIBERS;
        Uri builtUri = Uri.parse(BASE_URL)
                .buildUpon()
                .appendQueryParameter("limit", "50000")
//                .appendQueryParameter("page", "1")
                .build();
        final String myUrl = builtUri.toString();
        StringRequest sr = new StringRequest(Request.Method.GET, myUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse() getBPMerchant: response = [" + response + "]");
                try {
                    JSONObject jObj = new JSONObject(response);
                    int responseCode = jObj.getInt("responseCode");
                    if (responseCode == 200) {
                        JSONObject jObjReponse = jObj.getJSONObject("response");
                        JSONObject jObjPage = jObjReponse.getJSONObject("page");
                        String totalElements = jObjPage.getString("totalElements");

                        JSONArray jsonArrayData = jObjReponse.getJSONArray("data");
                        for (int i = 0; i < jsonArrayData.length(); i++) {
                            JSONObject jsonObjectOneMerchant = jsonArrayData.getJSONObject(i);
                            String subscriber_id = jsonObjectOneMerchant.getString("subscriber_id");
                            String subscriber_nickname = jsonObjectOneMerchant.getString("subscriber_nickname");
                            String subscriber_reference_no = jsonObjectOneMerchant.getString("subscriber_reference_no");
                            String subscriber_balance = jsonObjectOneMerchant.getString("subscriber_balance");
                            int user_id = jsonObjectOneMerchant.getInt("user_id");
                            String merchant_id = jsonObjectOneMerchant.getString("merchant_id");
                            String updated_at = jsonObjectOneMerchant.getString("updated_at");

                            Log.d(TAG, "onResponse: subscriber_id: " + subscriber_id);
                            Log.d(TAG, "onResponse: subscriber_nickname: " + subscriber_nickname);
                            Log.d(TAG, "onResponse: subscriber_reference_no: " + subscriber_reference_no);
                            Log.d(TAG, "onResponse: subscriber_balance: " + subscriber_balance);
                            Log.d(TAG, "onResponse: user_id: " + user_id);
                            Log.d(TAG, "onResponse: merchant_id: " + merchant_id);
                            Log.d(TAG, "onResponse: updated_at: " + updated_at);

                            if (BPBiller.getSubscriberFromServerId(subscriber_id) == null) {
                                BPMerchant bpMerchant = BPMerchant.getMerchantFromServerId(merchant_id);
                                BPBiller tempSubscriber = new BPBiller();
                                tempSubscriber.setServerId(subscriber_id);
                                tempSubscriber.setNickname(subscriber_nickname);
                                tempSubscriber.setReferenceno(subscriber_reference_no);
                                tempSubscriber.setBalance(subscriber_balance);
                                if(bpMerchant!=null){
                                    tempSubscriber.setUniversity(bpMerchant);
                                }
                                tempSubscriber.setUpdatedAt(Calendar.getInstance().getTime());
                                tempSubscriber.save();
                                TinyBus.from(getApplicationContext()).post(new BPSubscriberEventModel());
                            }
                        }
                    }
                } catch (JSONException e) {
                    Log.e("JSONException Data", response);
                    e.printStackTrace();
                }
                result = Activity.RESULT_OK;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: fetchBPMerchantsDataFunc");
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("Authorization", "Bearer " + sessionManager.getLoginToken());
                return params;
            }
        };
        sr.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(sr);
        requestsCounter.incrementAndGet();
    }

    private void publishResults(int result) {
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(RESULT, result);
        sendBroadcast(intent);
    }
}