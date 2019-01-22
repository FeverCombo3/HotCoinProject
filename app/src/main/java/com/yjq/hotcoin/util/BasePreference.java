package com.yjq.hotcoin.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by yjq
 * 2018/6/19.
 */

public class BasePreference {
    private Context context;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private String FILE_NAME = "userinfo";

    protected BasePreference(Context context) {
        this.context = context;
        sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
    }

    protected void setString(String key, String value) {
        sp.edit().putString(key, value).apply();
    }

    protected String getString(String key) {
        return sp.getString(key, null);
    }

    protected void setBoolean(String key, boolean value) {
        sp.edit().putBoolean(key, value).apply();
    }

    protected boolean getBoolean(String key) {
        return sp.getBoolean(key, false);
    }

    protected void setInt(String key, int value) {
        sp.edit().putInt(key, value).apply();
    }

    protected int getInt(String key) {
        return sp.getInt(key, 4);
    }

    protected int getInt(String key,int dvalue){
        return sp.getInt(key, dvalue);
    }

}
