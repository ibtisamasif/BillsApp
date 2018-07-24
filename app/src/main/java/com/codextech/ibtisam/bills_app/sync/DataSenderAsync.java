package com.codextech.ibtisam.bills_app.sync;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.codextech.ibtisam.bills_app.SessionManager;
import com.codextech.ibtisam.bills_app.models.BPSubscriber;
import com.codextech.ibtisam.bills_app.utils.NetworkAccess;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataSenderAsync {
    public static final String TAG = "DataSenderAsync";
//    private static final String TAG = "AppInitializationTest";

    private static DataSenderAsync instance = null;
    private static int currentState = 1;
    private static final int IDLE = 1;
    private static final int PENDING = 2;
    private static boolean firstThreadIsRunning = false;
    private SessionManager sessionManager;
    private Context mContext;
    private long totalSize = 0;
    private static RequestQueue queue;
    private final int MY_TIMEOUT_MS = 30000;
    private final int MY_MAX_RETRIES = 0;


    protected DataSenderAsync(Context context) {
        Log.d(TAG, "DataSenderAsync: ==========================================================================================================================");
        mContext = context;
        queue = Volley.newRequestQueue(mContext);
        firstThreadIsRunning = false;
    }

    public static DataSenderAsync getInstance(Context context) {
        final Context appContext = context.getApplicationContext();
        if (instance == null) {
//            synchronized ((Object) firstThreadIsRunning){
            if (!firstThreadIsRunning) {
                firstThreadIsRunning = true;
                instance = new DataSenderAsync(appContext);
            }
//            }
        }
        return instance;
    }

    public void run() {
        Log.d(TAG, "run: ");
        if (currentState == IDLE) {
            currentState = PENDING;
            Log.d(TAG, "run: InsideRUNING" + this.toString());
            queue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<DataSenderAsync>() {
                @Override
                public void onRequestFinished(Request request) {
                    currentState = IDLE;
                    Log.d(TAG, "onRequestFinished: EVERYTHING COMPLETED");
                }
            });
            new AsyncTask<Object, Void, Void>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    sessionManager = new SessionManager(mContext);
                    Log.d(TAG, "DataSenderAsync onPreExecute: ");
                }

                @Override
                protected Void doInBackground(Object... params) {
                    try {
                        if (NetworkAccess.isNetworkAvailable(mContext)) {
                            if (sessionManager.isUserSignedIn() && sessionManager.getCanSync()) {
                                Log.d(TAG, "Syncing");
                                Log.d(TAG, "Token : " + sessionManager.getLoginToken());
                                Log.d(TAG, "user_id : " + sessionManager.getKeyLoginId());
                                Log.d(TAG, "Syncing");
                                addSubscriberToServer();
                            } else {
                                Toast.makeText(mContext, ".", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.d(TAG, "SyncNoInternet");
                        }
                    } catch (Exception e) {
                        Log.d(TAG, "SyncingException: " + e.getMessage());
                    }
                    return null;
                }

                @Override
                protected void onProgressUpdate(Void... values) {
                    super.onProgressUpdate(values);
                }

                //this method is executed when doInBackground function finishes
                @Override
                protected void onPostExecute(Void result) {
                }
            }.execute();
        } else {
            Log.d(TAG, "run: NotRunning");
        }
    }

    private void addSubscriberToServer() {
        List<BPSubscriber> subscribersList = null;
        if (BPSubscriber.count(BPSubscriber.class) > 0) {
            subscribersList = BPSubscriber.find(BPSubscriber.class, "sync_status = ? ", SyncStatus.SYNC_STATUS_SUBSCRIBER_ADD_NOT_SYNCED);
            Log.d(TAG, "addSubscribersToServer: count : " + subscribersList.size());
            for (BPSubscriber oneSubscriber : subscribersList) {
                addSubscriberToServerSync(oneSubscriber);
            }
        }
    }

    private void addSubscriberToServerSync(final BPSubscriber subscriber) {
        currentState = PENDING;
        StringRequest sr = new StringRequest(Request.Method.POST, MyURLs.ADD_SUBSCRIBER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse() addSubscriber: response = [" + response + "]");
                try {
                    JSONObject jObj = new JSONObject(response);
                    JSONObject responseObject = jObj.getJSONObject("response");
                    subscriber.setNickname(responseObject.getString("subscriber_nickname"));
                    subscriber.setReferenceno(responseObject.getString("subscriber_reference_no"));
                    subscriber.setStatus(responseObject.getString("subscriber_status"));
                    subscriber.setBalance(responseObject.getInt("subscriber_balance") + "");
                    subscriber.setServerId(responseObject.getString("subscriber_id"));
                    Log.d(TAG, "onResponse: subscriber_id : " + responseObject.getString("subscriber_id"));
                    Log.d(TAG, "onResponse: subscriber_nickname : " + responseObject.getString("subscriber_nickname"));
                    Log.d(TAG, "onResponse: subscriber_reference_no : " + responseObject.getString("subscriber_reference_no"));
                    Log.d(TAG, "onResponse: subscriber_status : " + responseObject.getString("subscriber_status"));
                    Log.d(TAG, "onResponse: subscriber_balance : " + responseObject.getString("subscriber_balance"));
                    subscriber.setSyncStatus(SyncStatus.SYNC_STATUS_SUBSCRIBER_ADD_SYNCED);
                    subscriber.save();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: CouldNotSyncAddSubscriber");
                try {
                    if (error != null) {
                        if (error.networkResponse != null) {
                            Log.d(TAG, "onErrorResponse: error.networkResponse: " + error.networkResponse);
                            if (error.networkResponse.statusCode == 409) {
                                JSONObject jObj = new JSONObject(new String(error.networkResponse.data));
                                int responseCode = jObj.getInt("responseCode");
                                if (responseCode == 409) { //already exists on server
                                    Log.d(TAG, "onErrorResponse: responseCode == 409");
//                                    JSONObject responseObject = jObj.getJSONObject("response"); //TODO uncomment later
//                                    subscriber.setServerId(responseObject.getString("id"));
                                    subscriber.setSyncStatus(SyncStatus.SYNC_STATUS_SUBSCRIBER_ADD_SYNCED);
                                    subscriber.save();
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
//                params.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                params.put("Authorization", "Bearer " + sessionManager.getLoginToken());
                Log.d(TAG, "getHeaders: getHeaders: " + params);
                return params;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("subscriber[subscriber_nickname]", "" + subscriber.getNickname());
                params.put("subscriber[subscriber_reference_no]", "" + subscriber.getReferenceno());
                if (subscriber.getMerchant() != null) {
                    params.put("subscriber[merchant_id]", "" + subscriber.getMerchant().getServerId());
                }
                Log.d(TAG, "getParams: addSubscriberToServerSync " + params);
                return params;
            }
        };
        sr.setRetryPolicy(new DefaultRetryPolicy(
                MY_TIMEOUT_MS,
                MY_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(sr);
    }
}