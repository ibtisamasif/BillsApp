package com.codextech.ibtisam.bills_app.migrations;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
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
import com.codextech.ibtisam.bills_app.app.MixpanelConfig;
import com.codextech.ibtisam.bills_app.service.InitService;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ibtisam on 4/1/2017.
 */

public class VersionManager {
    public static final String TAG = "VersionManager";
    private Context mContext;
    private SessionManager sessionManager;

    public VersionManager(Context context) {
        mContext = context;
        sessionManager = new SessionManager(mContext);
    }

    public boolean runMigrations() {
        PackageInfo pInfo = null;
        try {
            pInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        int version = pInfo.versionCode;
        Log.d(TAG, "func: version: " + version);
        try {
            Log.d(TAG, "runMigrations: STORE VERSION CODE");
            sessionManager.storeVersionCodeNow();
            Log.d(TAG, "runMigrations: MixPanel Instantiated");
            String projectToken = MixpanelConfig.projectToken;
            MixpanelAPI mixpanel = MixpanelAPI.getInstance(mContext, projectToken);
            mixpanel.identify(sessionManager.getKeyLoginId()); //user_id
            mixpanel.getPeople().identify(sessionManager.getKeyLoginId());

            JSONObject props = new JSONObject();

            props.put("$first_name", "" + sessionManager.getKeyLoginFirstName());
            props.put("$last_name", "" + sessionManager.getKeyLoginLastName());
            props.put("activated", "yes");
//            props.put("Last Activity", "" + PhoneNumberAndCallUtils.getDateTimeStringFromMiliseconds(Calendar.getInstance().getTimeInMillis()));
            mixpanel.getPeople().set(props);
        } catch (JSONException e) {
            Log.e("mixpanel", "Unable to add properties to JSONObject", e);
        }
        // no return should be out of IF ELSE condition ever :: application might consider migrations successful otherwise
        if (version == 139) {
            try {
                Log.d(TAG, "func: Running Script for Migration");
                if (sessionManager.getLoginMode().equals(SessionManager.MODE_NORMAL)) {
                    Log.d(TAG, "MODE_NORMAL");
                    return true;
                } else if (sessionManager.getLoginMode().equals(SessionManager.MODE_NEW_INSTALL)) {
                    Log.d(TAG, "MODE_NEW_INSTALL");
                    // Do first run stuff here then set 'firstrun' as false
                    // using the following line to edit/commit prefs
                    return true;
                } else if (sessionManager.getLoginMode().equals(SessionManager.MODE_UPGRADE)) {
                    Log.d(TAG, "MODE_UPGRADE");
                    // Do first run stuff here then set 'firstrun' as false
                    // using the following line to edit/commit prefs
                    Intent intentInitService = new Intent(mContext, InitService.class);
                    mContext.startService(intentInitService);
                    return true;
                } else {
                    return true;
                }
            } catch (Exception e) {
                return false;
            }
        } else {
            return true;
        }
    }
}