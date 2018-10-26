package com.gm.player.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPreferences工具
 * <p>
 * Created by zv on 2017-12-6.
 */
public class YcSpUtil {
    private Context context = null;

    private static YcSpUtil spInfo;

    private final String DEFAULT_SP = "YC_SP";

    private YcSpUtil() {
    }

    public static YcSpUtil getInstance() {
        if (spInfo == null) {
            spInfo = new YcSpUtil();
        }
        return spInfo;
    }

    public void init(Context context) {
        this.context = context;
    }



    public String getString(String key) {
        return getString(DEFAULT_SP, key, "");
    }

    public String getString(String key, String defValue) {
        return getString(DEFAULT_SP, key, defValue);
    }

    private String getString(String spName, String key, String defValue) {
        SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        return sp.getString(key, defValue);
    }

    public void putString(String key, String value) {
        putString(DEFAULT_SP, key, value);
    }

    private void putString(String spName, String key, String value) {
        SharedPreferences.Editor sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE).edit();
        sp.putString(key, value);
        sp.apply();
    }


    private int getInt(String spName, String key, int defValue) {
        SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        return sp.getInt(key, defValue);
    }
    private void putInt(String spName, String key, int value) {
        SharedPreferences.Editor sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE).edit();
        sp.putInt(key, value);
        sp.apply();
    }

    public void putInt(String key,int value){
        putInt(DEFAULT_SP, key, value);
    }
    public int getInt(String key,int defaultValue){
       return getInt(DEFAULT_SP, key, defaultValue);
    }

    public void clear(){
        SharedPreferences preferences = context.getSharedPreferences(DEFAULT_SP, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }
}
