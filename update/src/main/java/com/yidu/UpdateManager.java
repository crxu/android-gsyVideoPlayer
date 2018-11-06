package com.yidu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;

/**
 * 升级管理类,升级操作都在这个类中处理
 *
 * @author andy on 2018/1/22
 *         使用规则
 *         1:在Application onCreate 函数中调用UpdateManager.init()方法,设置服务器地址
 *         2:调用UpdateManager.checkUpdate()函数
 *         3:调用调用UpdateManager.register()函数，监听CheckUpdateReceiver,下载成功后，回调onReceiver方法
 *         4:回调onReceiver时实现自己的提示逻辑代码(也可以使用默认的实现方式)
 *         5:点击安装，调用UpdateManager.install(filePath)函数
 *         6:使用默认弹框直接UpdateManager.show()但是需要在onPause方法中调用UpdateManager.hide()关闭，防止内存泄漏
 *         注意:记得要在对应Activity的onDestory函数中调用unRegister()函数
 */
public final class UpdateManager {

    protected static final String TYPE = "type";
    protected static final String PACKAGENAME = "packageName";
    protected static final String VERSIONCODE = "versionCode";
    protected static final String UPDATE = "update";
    private static boolean isOpenLog;
    protected static String URL;
    //下载完成后发送这个广播
    protected static final String UPDATE_ACTION = "com.yidu.action.update";
    protected static final String FILE_PATH = "file_path";
    protected static final String IS_FORCE_UPGRADE = "isForceUpgrade";

    /**
     * 发送广播通知
     * 使用LocalBroadcastManager 保证广播只能应用内有效
     *
     * @param context
     * @param file           apk 路径
     * @param isForceUpgrade true  强制更新
     */
    protected static void notifyUpdate(Context context, File file, boolean isForceUpgrade) {
        if (context == null || file == null || !file.exists()) return;
        Intent intent = new Intent(UPDATE_ACTION);
        intent.putExtra(FILE_PATH, file.getAbsolutePath());
        intent.putExtra(IS_FORCE_UPGRADE, isForceUpgrade);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public static void install(Context context, String filePath) {
        Utils.install(context, new File(filePath));
    }

    /**
     * 该方法适合Application 的onCreate()函数里面调用 必须在checkUpdate()函数前面调用
     *
     * @param openLog true 打开日志
     * @param isDebug true 测试环境 false 正式环境
     */
    public static void init(boolean openLog, boolean isDebug) {
        isOpenLog = openLog;
        if (isDebug) {
            URL = "http://192.168.4.99:9988/apkVersion/newestApkVersion.do";
        } else {
            URL = "http://aged-server.readyidu.com/apkVersion/newestApkVersion.do";
        }
    }

    public static void log(Class clazz, String message) {
        if (isOpenLog) {
            Log.d("UpdateManager_TAG", clazz.getSimpleName() + "==>>" + message);
        }
    }

    /**
     * 注册版本更新成功的广播,有更新apk下载成功后会通知
     * 使用LocalBroadcastManager 保证广播只能应用内有效
     *
     * @param receiver
     * @param context
     */
    public static void register(CheckUpdateReceiver receiver, Context context) {
        if (receiver != null && context != null) {
            IntentFilter filter = new IntentFilter(UPDATE_ACTION);
            LocalBroadcastManager.getInstance(context).registerReceiver(receiver, filter);
        }
    }

    /**
     * 取消广播
     *
     * @param receiver
     * @param context
     */
    public static void unRegister(CheckUpdateReceiver receiver, Context context) {
        if (receiver != null && context != null) {
            LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver);
        }
        hide();
    }

    /**
     * 开始检查版本更新 必须在init()方法调用后调用，不然会抛异常
     *
     * @param context
     */
    public static void checkUpdate(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context is null");
        }
        if (URL == null) {
            throw new IllegalArgumentException("UpdateManager must init");
        }
        log(UpdateManager.class, "checkUpdate");
        try {
            Intent intent = new Intent(context, UpdateIntentService.class);
            String pkName = context.getPackageName();
            int versionCode = context.getPackageManager().getPackageInfo(pkName, 0).versionCode;
            intent.putExtra(TYPE, UPDATE);
            intent.putExtra(PACKAGENAME, pkName);
            intent.putExtra(VERSIONCODE, versionCode);
            intent.putExtra(VERSIONCODE, versionCode);
            context.startService(intent);
        } catch (Exception e) {
            e.printStackTrace();
            log(UpdateManager.class, "e = " + e.toString());
        }
    }


    //默认弹框
    private static UpdateHintDialog sUpdateHintDialog;

    /**
     * 显示更新提示Dialog
     *
     * @param activity
     * @param filePath       apk路径
     * @param isForceUpgrade true强制更新
     */
    public static void show(Activity activity, String filePath, boolean isForceUpgrade, UpdateManager.OnUpdateListener listener) {
        if (activity == null || TextUtils.isEmpty(filePath)) return;
        log(UpdateManager.class, "show");
        hide();
        sUpdateHintDialog = new UpdateHintDialog(activity, filePath, isForceUpgrade);
        sUpdateHintDialog.setOnUpdateListener(listener);
        sUpdateHintDialog.show();
    }

    public static void hide() {
        try {
            log(UpdateManager.class, "hide");
            if (sUpdateHintDialog != null && sUpdateHintDialog.isShowing()) {
                sUpdateHintDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
            log(UpdateManager.class, e.toString());
        } finally {
            sUpdateHintDialog = null;
        }
    }

    public interface OnUpdateListener {
        void onUpdate(boolean isForceUpgrade);
    }

}
