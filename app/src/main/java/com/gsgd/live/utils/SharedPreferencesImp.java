package com.gsgd.live.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.gsgd.live.MainApplication;
import com.jiongbull.jlog.JLog;

import static android.content.Context.MODE_MULTI_PROCESS;
import static android.content.Context.MODE_WORLD_READABLE;
import static android.content.Context.MODE_WORLD_WRITEABLE;

/**
 * Created by Admin on 2017/3/9.
 */
public class SharedPreferencesImp {
    private Context context;
    private String settingString = "SharedPreferencesXml";
    private String loginHistory = "loginHistory";

    static class SharedPreferencesImpHolder {

        static SharedPreferencesImp sharedPreferencesImp = new SharedPreferencesImp(MainApplication.getContext());
    }

    public static SharedPreferencesImp getInstance() {
        return SharedPreferencesImpHolder.sharedPreferencesImp;
    }

    private SharedPreferencesImp(Context context) {
        try {
            this.context = context.createPackageContext("tv.yuyin.launcher", Context.CONTEXT_IGNORE_SECURITY);

        } catch (Exception e) {
            JLog.e(e);
            this.context = context;
        }
    }


    /**
     * 设置sharedPreferences
     *
     * @param keyName
     * @param keyValue
     */
    public void putSharedPreferences(String keyName, String keyValue) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(keyName, keyValue);
        editor.apply();
    }

    public SharedPreferences getSharedPreferences() {
        return context.getSharedPreferences("yidu.com", MODE_WORLD_WRITEABLE | MODE_WORLD_READABLE | MODE_MULTI_PROCESS);
    }

    /**
     * 获取sharedPreferences中key为keyName的值
     *
     * @param keyName
     * @return
     */
    public String getSharedPreferences(String keyName) {
        SharedPreferences sp = getSharedPreferences();
        return sp.getString(keyName, "");
    }

}