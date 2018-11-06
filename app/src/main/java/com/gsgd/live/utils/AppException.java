package com.gsgd.live.utils;

import android.app.Application;
import android.util.Log;

import com.umeng.analytics.MobclickAgent;

import java.lang.Thread.UncaughtExceptionHandler;

/**
 * 应用程序异常：用于捕获异常和提示错误信息
 */
public class AppException<App extends Application> implements UncaughtExceptionHandler {

    private static AppException mCrashHandler;
    private App application;
    private UncaughtExceptionHandler mDefaultHandler;

    private AppException() {
    }

    public static AppException getInstance() {
        if (mCrashHandler == null) {
            synchronized (AppException.class) {
                if (mCrashHandler == null) {
                    mCrashHandler = new AppException();
                }
            }
        }
        return mCrashHandler;
    }

    public void init(App mContext) {
        application = mContext;
        // 获取系统默认的 UncaughtException 处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        // 设置该 CrashHandler 为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {

        if (!handleException(ex) && mDefaultHandler != null) {
            // 如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);

        } else {
            //退出程序
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

    /**
     * 自定义异常处理:收集错误信息&发送错误报告
     *
     * @param ex
     * @return true:处理了该异常信息;否则返回false
     */
    private boolean handleException(final Throwable ex) {
        if (ex == null) {
            return false;
        }
        Log.e("", "", ex);
        MobclickAgent.reportError(application, ex);

        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);

        return true;
    }

}
