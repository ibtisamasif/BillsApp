package com.codextech.ibtisam.bills_app.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.codextech.ibtisam.bills_app.R;
import com.codextech.ibtisam.bills_app.SessionManager;
import com.codextech.ibtisam.bills_app.app.MixpanelConfig;
import com.codextech.ibtisam.bills_app.migrations.VersionManager;
import com.codextech.ibtisam.bills_app.sync.MyURLs;
import com.codextech.ibtisam.bills_app.utils.NetworkAccess;

import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class LogInActivity extends AppCompatActivity {

    public static final String TAG = "LogInActivity";
    private ProgressDialog pdLoading;
    private EditText etEmail, etPassword;
    private Button bSignup;
    private Button bReset;
    private String email, password;
    private Button loginButtonLoginScreen;
    private SessionManager sessionManager;
    private static RequestQueue queue;

    public static Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        activity = this;
        queue = Volley.newRequestQueue(LogInActivity.this, new HurlStack());

//        Version Control
        VersionManager versionManager = new VersionManager(getApplicationContext());
        if (!versionManager.runMigrations()) {
            // if migration has failed
            Toast.makeText(getApplicationContext(), "Migration Failed", Toast.LENGTH_SHORT).show();
        }

        sessionManager = new SessionManager(getApplicationContext());
        if (sessionManager.isUserSignedIn()) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
        pdLoading = new ProgressDialog(this);
        pdLoading.setTitle("Loading data");
        //this method will be running on UI thread
        pdLoading.setMessage("Please Wait...");
        loginButtonLoginScreen = (Button) findViewById(R.id.loginButtonLoginScreen);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        bSignup = (Button) findViewById(R.id.bSignup);
        bReset = (Button) findViewById(R.id.bReset);
        etEmail.getBackground().clearColorFilter();
        etPassword.getBackground().clearColorFilter();

//        hardcoding number and password for development speedup purposes
        etEmail.setText("ibtisamasif@gmail.com");
        etPassword.setText("1234");

        loginButtonLoginScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etEmail.setError(null);
                etPassword.setError(null);
                Boolean emailVarified = true, passwordVarified = true;
                email = etEmail.getText().toString();
                password = etPassword.getText().toString();

                if (email.length() < 7) {
                    emailVarified = false;
                }
                if (password.length() < 4) {
                    passwordVarified = false;
                }
//                if (!PhoneNumberAndCallUtils.isValidPassword(password)) {
//                    passwordVarified = false;
//                }
                if (!emailVarified) {
                    etEmail.setError("Invalid Email!");
                }
                if (!passwordVarified) {
                    etPassword.setError("Invalid Password!");
                }
                if (emailVarified && passwordVarified) {
                    pdLoading.show();
                    makeLoginRequest(LogInActivity.this, email, password);
                }
            }
        });
        bSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(LogInActivity.this, OnBoardingActivity.class));
//                startActivity(new Intent(getApplicationContext(), SignupActivity.class));
//                Toast.makeText(LogInActivity.this, "Signup", Toast.LENGTH_SHORT).show();
            }
        });
        bReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(MyURLs.FORGOT_PASSWORD));
                startActivity(i);
                String projectToken = MixpanelConfig.projectToken;
                MixpanelAPI mixpanel = MixpanelAPI.getInstance(LogInActivity.this, projectToken);
                mixpanel.track("Password Reset");
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (pdLoading != null && pdLoading.isShowing()) {
            pdLoading.dismiss();
        }
        super.onDestroy();
    }

    public void makeLoginRequest(final Activity activity, final String email, final String password) {
        final int MY_SOCKET_TIMEOUT_MS = 60000;
        StringRequest sr = new StringRequest(Request.Method.POST, MyURLs.LOGIN_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse() makeLoginRequest called with: response = [" + response + "]");
//                pdLoading.dismiss();
                try {
                    if (pdLoading != null && pdLoading.isShowing()) {
                        pdLoading.dismiss();
                    }
                    JSONObject jObj = new JSONObject(response);
                    int responseCode = jObj.getInt("responseCode");
                    if (responseCode == 200) {
                        JSONObject jObjReponse = jObj.getJSONObject("response");
                        String access_token = jObjReponse.getString("access_token");
                        String type = jObjReponse.getString("type");
                        Log.d(TAG, "onResponse: access_token: " + access_token);
                        Log.d(TAG, "onResponse: type: " + type);

                        //parsing user object
                        JSONObject responseObject = jObjReponse.getJSONObject("user");
                        String user_id = responseObject.getString("user_id");
                        String user_name = responseObject.getString("user_name");
                        String user_email = responseObject.getString("user_email");
                        String user_scope = responseObject.getString("user_scope");
                        String user_fcm_id = responseObject.getString("user_fcm_id");
                        String user_image = responseObject.getString("user_image");
                        String user_wallet_balance = responseObject.getString("user_wallet_balance");
                        String iat = responseObject.getString("iat");

                        Log.d(TAG, "onResponse: user_id: " + user_id);
                        Log.d(TAG, "onResponse: user_name: " + user_name);
                        Log.d(TAG, "onResponse: user_email: " + user_email);
                        Log.d(TAG, "onResponse: user_scope: " + user_scope);
                        Log.d(TAG, "onResponse: user_fcm_id: " + user_fcm_id);
                        Log.d(TAG, "onResponse: user_image: " + user_image);
                        Log.d(TAG, "onResponse: user_wallet_balance: " + user_wallet_balance);
                        Log.d(TAG, "onResponse: iat: " + iat);

                        sessionManager.loginnUser(user_id, access_token, Calendar.getInstance().getTimeInMillis(), user_name, user_image, user_email, user_scope,user_wallet_balance);

                        startActivity(new Intent(LogInActivity.this, MainActivity.class));
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (pdLoading != null && pdLoading.isShowing()) {
                    pdLoading.dismiss();
                }
                Log.d(TAG, "onErrorResponse() called with: error = [" + error + "]");
                if (!NetworkAccess.isNetworkAvailable(getApplicationContext())) {
                    Toast.makeText(getApplicationContext(), "Turn on wifi or Mobile Data", Toast.LENGTH_LONG).show();
                } else {
                    try {
                        if (error.networkResponse != null) {
                            if (error.networkResponse.statusCode == 401) {
                                Toast.makeText(activity, "Invalid email or password entered", Toast.LENGTH_SHORT).show();
                            }
//                            else if (error.networkResponse.statusCode == 404) {
//                                Toast.makeText(activity, "User Does not Exist.", Toast.LENGTH_SHORT).show();
//                            }
                            else {
                                Toast.makeText(activity, "Server Error.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Internet Connectivity might be poor", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user[user_email]", email);
                params.put("user[user_password]", password);
                Log.d(TAG, "Login getParams: " + params);
                return params;
            }

//            @Override
//            public byte[] getBody() throws com.android.volley.AuthFailureError {
//                String str = "{\"user\" : {\"user_email\": \"ibtisamasif@gmail.com\",\"user_password\": \"123456\"}}";
//                Log.d(TAG, "getBody: str: " + str);
//                return str.getBytes();
//            }

//            public String getBodyContentType() {
//                return "application/json; charset=utf-8";
//            }

//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("Content-Type", "application/json");
//                return params;
//            }
        };
        sr.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(sr);
    }

//    private void updateFirebaseIdAndInitConfigMakeRequest(Activity activity, @Nullable JSONObject returnInitJson) {
//
//        final int MY_SOCKET_TIMEOUT_MS = 60000;
//        final String BASE_URL = MyURLs.UPDATE_AGENT;
//        Uri builtUri;
//        if (returnInitJson != null) {
//            builtUri = Uri.parse(BASE_URL)
//                    .buildUpon()
//                    .appendQueryParameter("config", "" + returnInitJson)
//                    .appendQueryParameter("device_id", "" + sessionManager.getKeyLoginFirebaseRegId())
//                    .appendQueryParameter("api_token", "" + sessionManager.getLoginToken())
//                    .build();
//
//        } else {
//            builtUri = Uri.parse(BASE_URL)
//                    .buildUpon()
//                    .appendQueryParameter("device_id", "" + sessionManager.getKeyLoginFirebaseRegId())
//                    .appendQueryParameter("api_token", "" + sessionManager.getLoginToken())
//                    .build();
//        }
//        final String myUrl = builtUri.toString();
//        StringRequest sr = new StringRequest(Request.Method.PUT, myUrl, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                Log.d(TAG, "onResponse() updateFirebaseIdAndInitConfigMakeRequest: response = [" + response + "]");
//                try {
//                    if (pdLoading != null && pdLoading.isShowing()) {
//                        pdLoading.dismiss();
//                    }
//                    JSONObject jObj = new JSONObject(response);
//                    int responseCode = jObj.getInt("responseCode");
//                    if (responseCode == 200) {
//
//                        JSONObject responseObject = jObj.getJSONObject("response");
//                        Log.d(TAG, "onResponse : FirebaseLocalRegID : " + sessionManager.getKeyLoginFirebaseRegId());
//                        Log.d(TAG, "onResponse : FirebaseServerRegID : " + responseObject.getString("device_id"));
//
////                        TheCallLogEngine theCallLogEngine = new TheCallLogEngine(getApplicationContext());
////                        theCallLogEngine.execute();
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                if (pdLoading != null && pdLoading.isShowing()) {
//                    pdLoading.dismiss();
//                }
//                Log.d(TAG, "onErrorResponse: CouldNotUpdateInitConfigMakeRequest OR CouldNotSyncAgentFirebaseRegId");
//
////                RecordingManager recordingManager = new RecordingManager();
////                recordingManager.execute();
////                TheCallLogEngine theCallLogEngine = new TheCallLogEngine(getApplicationContext());
////                theCallLogEngine.execute();
//            }
//        }) {
//        };
//        sr.setRetryPolicy(new DefaultRetryPolicy(
//                MY_SOCKET_TIMEOUT_MS,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        queue.add(sr);
//    }
}
