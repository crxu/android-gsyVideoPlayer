package com.gsgd.live;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.gsgd.live.utils.AppException;
import com.jiongbull.jlog.JLog;
import com.umeng.analytics.MobclickAgent;
import com.yidu.UpdateManager;

import org.litepal.LitePal;

import cn.jpush.android.api.JPushInterface;

public class MainApplication extends Application {

    private static final String TAG = MainApplication.class.getSimpleName();
    private static Context mContext;

    public static Context getContext() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        LitePal.initialize(this);
        //日志
        JLog.init(getApplicationContext()).setDebug(AppConfig.isOpenLog);

        AppException.getInstance().init(this);

        JPushInterface.setDebugMode(AppConfig.isOpenLog);
        JPushInterface.init(this);

        MobclickAgent.startWithConfigure(new MobclickAgent.UMAnalyticsConfig(
                getApplicationContext(),
                "59dc3aac8f4a9d5440000267",
                "tv_channel",
                MobclickAgent.EScenarioType.E_UM_NORMAL,
                true));
        MobclickAgent.setDebugMode(AppConfig.isOpenLog);
        UpdateManager.init(AppConfig.isOpenLog, AppConfig.isDebug);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}
