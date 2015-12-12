package com.test.busanit.messenger_alpha;

import android.content.Context;
import android.content.SharedPreferences;

public class LoginPreference {

    private Context context;

    private final String PREFERENCE_NAME = "com.example.administrator.messenger";

    public LoginPreference(Context context) {
        this.context = context;
    }

    public void put(String key, String value) {
        SharedPreferences pref = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putString(key, value);
        editor.commit();

    }

    public String get(String key, String dftValue) {
        SharedPreferences pref = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        try {
            return pref.getString(key, dftValue);
        } catch (Exception e) {
            return dftValue;
        }
    }
}