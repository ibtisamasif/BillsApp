package com.codextech.ibtisam.bills_app.sync;

public class MyURLs {

//    private static final String server = "http://192.168.100.22:3000"; //local
    private static final String server = "http://165.227.62.23:3000"; //server

    public static final String LOGIN_URL = server + "/api/v1/users/signin";
    public static final String GET_MERCHANTS = server + "/api/v1/merchants";
    public static final String GET_SUBSCRIBERS = server + "/api/v1/subscriber";
    public static final String ADD_SUBSCRIBER = server + "/api/v1/subscriber";

    public static final String PRIVACY_POLICY = "https://bills.pk/privacy.html";
    public static final String FORGOT_PASSWORD = "https://app.bills.pk/#/access/forgotpwd";
}
