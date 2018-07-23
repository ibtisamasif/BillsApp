package com.codextech.ibtisam.bills_app;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.codextech.ibtisam.bills_app.models.BPSubscriber;
import com.codextech.ibtisam.bills_app.models.BPMerchant;
import com.codextech.ibtisam.bills_app.models.BPTransac;

import java.util.Calendar;


public class SessionManager {
    public static final String TAG = "SessionManager";
    private static final String KEY_LOGIN_ID = "user_login_id";
    private static final String KEY_LOGIN_TOKEN = "user_login_token";
    private static final String KEY_LOGIN_TIMESTAMP = "user_login_timestamp";
    private static final String KEY_LOGIN_USERNAME = "user_login_username";
    private static final String KEY_LOGIN_IMAGEPATH = "user_login_imagepath";
    private static final String KEY_LOGIN_FIREBASE_REG_ID = "user_login_firebase_reg_id";
    private static final String KEY_LOGIN_EMAIL = "user_login_email";
    private static final String KEY_LOGIN_ROLE_NAME = "user_login_role_name";
    private static final String KEY_LOGIN_WALLET_BALANCE = "user_login_wallet_balance";

    public static final String KEY_INIT_COMPLETED = "init_completed";
    public static final String KEY_INIT_TEAM_ADDED = "init_team_added";
    public static final String KEY_INIT_APP_DOWNLOADED = "init_app_downloaded";
    public static final String KEY_INIT_COMPANY_CREATED = "init_company_created";
    public static final String KEY_INIT_ACCOUNT_TYPE_SELECTED = "init_account_type_selected";

    private static final String KEY_LOGIN_MODE = "user_login_mode";
    public static final String MODE_NORMAL = "user_login_normal";
    public static final String MODE_NEW_INSTALL = "user_login_new_install";
    public static final String MODE_UPGRADE = "user_login_upgrade";

    public static final String LAST_APP_VISIT = "last_app_visit";

    public static final String FIRST_RUN_AFTER_LOGIN = "firstrun";

    public static final String KEY_IS_TRIAL_VALID = "is_trial_valid";

    public static final String KEY_IS_USER_ACTIVE = "is_user_active";

    public static final String KEY_IS_COMPANY_ACTIVE = "is_company_active";

    public static final String KEY_IS_COMPANY_PAYING = "is_company_paying";

    public static final String KEY_CAN_SYNC = "can_sync";

    public static final String KEY_UPDATE_AVAILABLE_VERSION = "update_available_version";

    public static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";

    // Sharedpref file name
    private static final String PREF_NAME = "ProjectBillsPkPrefs";
    // Shared Preferences
    SharedPreferences pref;
    // Editor for Shared preferences
    SharedPreferences.Editor editor;
    // Context
    Context _context;
    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Constructor
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public boolean getIsFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, false);
    }

    public void storeVersionCodeNow() {

        final String PREFS_NAME = PREF_NAME;
        final String PREF_VERSION_CODE_KEY = "version_code";
        final int DOESNT_EXIST = -1;

        // Get current version code
        int currentVersionCode = 0;
        try {
            currentVersionCode = _context.getPackageManager().getPackageInfo(_context.getPackageName(), 0).versionCode;
        } catch (android.content.pm.PackageManager.NameNotFoundException e) {
            // handle exception
            e.printStackTrace();
        }

        // Get saved version code
        int savedVersionCode = pref.getInt(PREF_VERSION_CODE_KEY, DOESNT_EXIST);

        // Check for first run or upgrade
        if (currentVersionCode == savedVersionCode) {
            // This is just a normal run
            Log.d(TAG, "storeVersionCodeNow: NormalRun = 1");
            setLoginMode(SessionManager.MODE_NORMAL);

        } else if (savedVersionCode == DOESNT_EXIST) {
            Log.d(TAG, "storeVersionCodeNow: NewInstall = 2");
            setLoginMode(SessionManager.MODE_NEW_INSTALL);

        } else if (currentVersionCode > savedVersionCode) {
            Log.d(TAG, "storeVersionCodeNow: Upgrade = 3");
            setLoginMode(SessionManager.MODE_UPGRADE);
        }
        // Update the shared preferences with the current version code
        editor.putInt(PREF_VERSION_CODE_KEY, currentVersionCode);
        editor.commit();
    }

    public Boolean isFirstRunAfterLogin() {
        if (getReadyForFirstRun()) {
            setReadyForFirstRun(false);
            return true;
        } else {
            return false;
        }
    }

    public Boolean isUserSignedIn() {

        if (getLoginToken().equals("")) {
            return false;
        }
        if (getLoginTimestamp() == 00L) {
            return false;
        }
        Long oldTimestamp = getLoginTimestamp();
        Long currentTimestamp = Calendar.getInstance().getTimeInMillis();
        Long oldAnd24Hours = oldTimestamp + 15552000000L; //Six months expiry
        if (currentTimestamp > oldAnd24Hours) {
            return false;
        }
        return true;
    }

    public void loginnUser(String userId, String token, Long timeStamp, String username, String imagePath, String email, String role_role, String user_wallet_balance) {
        Log.d(TAG, "loginnUser: ");
        deleteDataIfDifferentUser(username);
        setLoginUsername(username);
        setLoginToken(token);
        setLoginTimestamp(timeStamp);
        setKeyLoginId(userId);
        setKeyLoginImagePath(imagePath);
        setKeyLoginEmail(email);
        setKeyLoginRoleName(role_role);
        setKeyLoginWalletBalance(user_wallet_balance);
        editor.commit();
    }

//    public void fetchData() {
//        if( isUserSignedIn()){
//            AgentDataFetchAsync agentDataFetchAsync = new AgentDataFetchAsync(_context);
//            agentDataFetchAsync.execute();
//        }
//        else {
//            Log.d(TAG, "fetchData: USER IS NOT SIGNED IN");
//        }
//    }

    private void deleteDataIfDifferentUser(String username) {
        String oldUserNumber = getLoginUsername();
        if (!oldUserNumber.equals(username)) {
            deleteAllUserData();
        }
    }

    public void deleteAllUserData() {
        Log.d(TAG, "Different Agent: deleting data...");
        if (BPMerchant.count(BPMerchant.class) > 0) {
            BPMerchant.deleteAll(BPMerchant.class);
        }
        if (BPSubscriber.count(BPSubscriber.class) > 0) {
            BPSubscriber.deleteAll(BPSubscriber.class);
        }
        if (BPTransac.count(BPTransac.class) > 0) {
            BPTransac.deleteAll(BPTransac.class);
        }
    }

    /**
     * saves the data of user in the Shared preffrences which later can be accessed to perform user specific operations
     */
    public void logoutUser() {
        deleteAllUserData();
        setLoginTimestamp(00L);
        setKeyLoginId("");
        setLoginUsername("");
        setKeyLoginEmail("");
        setKeyLoginWalletBalance("");
        setLoginToken("");
        setKeyLoginImagePath("");
        setKeyLoginRoleName("");
        setReadyForFirstRun(true);
        editor.commit();
    }

    public String getLoginToken() {
        return pref.getString(KEY_LOGIN_TOKEN, "");
    }

    public void setLoginToken(String loginToken) {
        editor.putString(KEY_LOGIN_TOKEN, loginToken);
        editor.commit();
    }

    public String getLoginUsername() {
        return pref.getString(KEY_LOGIN_USERNAME, "");
    }

    public void setLoginUsername(String username) {
        editor.putString(KEY_LOGIN_USERNAME, username);
        editor.commit();
    }

    public Long getLoginTimestamp() {
        return pref.getLong(KEY_LOGIN_TIMESTAMP, 00l);
    }

    public void setLoginTimestamp(Long timestamp) {
        editor.putLong(KEY_LOGIN_TIMESTAMP, timestamp);
        editor.commit();
    }

    public String getKeyLoginImagePath() {
        return pref.getString(KEY_LOGIN_IMAGEPATH, "");
    }


    public void setKeyLoginImagePath(String path) {
        editor.putString(KEY_LOGIN_IMAGEPATH, path);
        editor.commit();
    }

    public String getKeyLoginId() {
        return pref.getString(KEY_LOGIN_ID, "");
    }

    public void setKeyLoginId(String path) {
        editor.putString(KEY_LOGIN_ID, path);
        editor.commit();
    }

    public String getKeyLoginFirebaseRegId() {
        return pref.getString(KEY_LOGIN_FIREBASE_REG_ID, "");
    }

    public void setKeyLoginFirebaseRegId(String token) {
        editor.putString(KEY_LOGIN_FIREBASE_REG_ID, token);
        editor.commit();
    }

    public String getLoginMode() {
        return pref.getString(KEY_LOGIN_MODE, "");
    }

    public void setLoginMode(String mode) {
        editor.putString(KEY_LOGIN_MODE, mode);
        editor.commit();
    }

    public String getKeyLoginEmail() {
        return pref.getString(KEY_LOGIN_EMAIL, "");
    }

    public void setKeyLoginEmail(String email) {
        editor.putString(KEY_LOGIN_EMAIL, email);
        editor.commit();
    }

    public String getKeyLoginRoleName() {
        return pref.getString(KEY_LOGIN_ROLE_NAME, "");
    }

    public void setKeyLoginRoleName(String name) {
        editor.putString(KEY_LOGIN_ROLE_NAME, name);
        editor.commit();
    }


    public String getKeyInitCompleted() {
        return pref.getString(KEY_INIT_COMPLETED, "");
    }

    public void setKeyInitCompleted(String boolVal) {
        editor.putString(KEY_INIT_COMPLETED, boolVal);
        editor.commit();
    }

    public String getKeyInitTeamAdded() {
        return pref.getString(KEY_INIT_TEAM_ADDED, "");
    }

    public void setKeyInitTeamAdded(String boolVal) {
        editor.putString(KEY_INIT_TEAM_ADDED, boolVal);
        editor.commit();
    }

    public String getKeyInitAppDownloaded() {
        return pref.getString(KEY_INIT_APP_DOWNLOADED, "");
    }

    public void setKeyInitAppDownloaded(String boolVal) {
        editor.putString(KEY_INIT_APP_DOWNLOADED, boolVal);
        editor.commit();
    }

    public String getKeyInitCompanyCreated() {
        return pref.getString(KEY_INIT_COMPANY_CREATED, "");
    }

    public void setKeyInitCompanyCreated(String boolVal) {
        editor.putString(KEY_INIT_COMPANY_CREATED, boolVal);
        editor.commit();
    }

    public String getKeyLoginWalletBalance() {
        return pref.getString(KEY_LOGIN_WALLET_BALANCE, "");
    }

    public void setKeyLoginWalletBalance(String balance) {
        editor.putString(KEY_LOGIN_WALLET_BALANCE, balance);
        editor.commit();
    }

    public String getLastAppVisit() {
        return pref.getString(LAST_APP_VISIT, "");
    }

    public void setLastAppVisit(String time) {
        editor.putString(LAST_APP_VISIT, time);
        editor.commit();
    }

    public boolean getReadyForFirstRun() {
        return pref.getBoolean(FIRST_RUN_AFTER_LOGIN, true);
    }

    public void setReadyForFirstRun(boolean val) {
        editor.putBoolean(FIRST_RUN_AFTER_LOGIN, val);
        editor.commit();
    }

    public boolean getKeyIsTrialValid() {
        return pref.getBoolean(KEY_IS_TRIAL_VALID, true);
    }

    public void setKeyIsTrialValid(boolean val) {
        editor.putBoolean(KEY_IS_TRIAL_VALID, val);
        editor.commit();
    }

    public boolean getKeyIsUserActive() {
        return pref.getBoolean(KEY_IS_USER_ACTIVE, true);
    }

    public void setKeyIsUserActive(boolean val) {
        editor.putBoolean(KEY_IS_USER_ACTIVE, val);
        editor.commit();
    }

    public boolean getKeyIsCompanyActive() {
        return pref.getBoolean(KEY_IS_COMPANY_ACTIVE, true);
    }

    public void setKeyIsCompanyActive(boolean val) {
        editor.putBoolean(KEY_IS_COMPANY_ACTIVE, val);
        editor.commit();
    }

    public boolean getKeyIsCompanyPaying() {
        return pref.getBoolean(KEY_IS_COMPANY_PAYING, true);
    }

    public void setKeyIsCompanyPaying(boolean val) {
        editor.putBoolean(KEY_IS_COMPANY_PAYING, val);
        editor.commit();
    }

    public boolean getCanSync() {
        return pref.getBoolean(KEY_CAN_SYNC, true);
    }

    public void setCanSync(boolean val) {
        editor.putBoolean(KEY_CAN_SYNC, val);
        editor.commit();
    }

    public int getUpdateAvailableVersion() {
        return pref.getInt(KEY_UPDATE_AVAILABLE_VERSION, 0);
    }

    public void setUpdateAvailableVersion(int val) {
        editor.putInt(KEY_UPDATE_AVAILABLE_VERSION, val);
        editor.commit();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, false);
    }


}