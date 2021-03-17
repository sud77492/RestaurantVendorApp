package com.nhsurveys.restaurantvendorapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class UserDetailsPref {
    public static String USER_ID = "user_id";
    public static String USER_NAME = "user_name";
    public static String USER_USERNAME = "user_username";
    public static String USER_EMAIL = "user_email";
    public static String USER_MOBILE = "user_mobile";
    public static String USER_LOGIN_KEY = "user_login_key";
    public static String USER_RESTAURANT_NAME = "user_restaurant_name";
    public static String QUESTION_LIST = "question_list";
    public static String OPTION_LIST = "option_list";
    public static String RESPONSE = "response";
    public static String LANGUAGE_TYPE = "language_type";
    public static String CUSTOMER_NAME = "customer_name";
    public static String CUSTOMER_MOBILE = "customer_mobile";
    public static String LOGIN_CHECK = "login_check";
    public static String FIREBASE_ID = "firebase_id";
    private static UserDetailsPref userDetailsPref;
    private String USER_DETAILS = "USER_DETAILS";

    public static UserDetailsPref getInstance () {
        if (userDetailsPref == null)
            userDetailsPref = new UserDetailsPref ();
        return userDetailsPref;
    }

    private SharedPreferences getPref (Context context) {
        return context.getSharedPreferences (USER_DETAILS, Context.MODE_PRIVATE);
    }

    public String getStringPref (Context context, String key) {
        return getPref (context).getString (key, "");
    }

    public int getIntPref (Context context, String key) {
        return getPref (context).getInt (key, 0);
    }

    public boolean getBooleanPref (Context context, String key) {
        return getPref (context).getBoolean (key, false);
    }

    public void putBooleanPref (Context context, String key, boolean value) {
        SharedPreferences.Editor editor = getPref (context).edit ();
        editor.putBoolean (key, value);
        editor.apply ();
    }

    public void putStringPref (Context context, String key, String value) {
        SharedPreferences.Editor editor = getPref (context).edit ();
        editor.putString (key, value);
        editor.apply ();
    }

    public void putIntPref (Context context, String key, int value) {
        SharedPreferences.Editor editor = getPref (context).edit ();
        editor.putInt (key, value);
        editor.apply ();
    }
}
