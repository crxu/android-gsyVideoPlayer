package com.gsgd.live.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import com.jiongbull.jlog.JLog;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 生命周期回调
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class LifecycleCallbacks implements Application.ActivityLifecycleCallbacks {

    public static final long CHECK_DELAY = 500;
    private static LifecycleCallbacks instance;
    private List<Listener> listeners = new CopyOnWriteArrayList<>();
    private Runnable check;
    private Handler handler = new Handler();
    private boolean foreground = false, paused = true;

    public static LifecycleCallbacks init(Application application) {
        if (instance == null) {
            instance = new LifecycleCallbacks();
            application.registerActivityLifecycleCallbacks(instance);
        }
        return instance;
    }

    public static LifecycleCallbacks get() {
        if (instance == null) {
            throw new IllegalStateException("LifecycleCallbacks没有初始化");
        }
        return instance;
    }

    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }

    @Override
    public void onActivityResumed(Activity activity) {
        paused = false;
        boolean wasBackground = !foreground;
        foreground = true;
        if (check != null) {
            handler.removeCallbacks(check);
        }
//        if (wasBackground) {
        JLog.d("went foreground");
        for (Listener l : listeners) {
            try {
                l.onBecameForeground();

            } catch (Exception exc) {
                JLog.d("Listener threw exception!:" + exc.toString());
            }
        }

//        } else {
//            JLog.d("still foreground");
//        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        if (check != null) {
            handler.removeCallbacks(check);
        }
        String curActivity = getCurrentActivityName(activity);
        JLog.d("************->curActivity:" + curActivity);

        if ("com.readyidu.routerapp.FinalSpeechActivity".equals(curActivity)) {
            return;
        }

        paused = true;
        handler.postDelayed(check = new Runnable() {
            @Override
            public void run() {
                if (foreground && paused) {
                    foreground = false;
                    JLog.d("went background");
                    for (Listener l : listeners) {
                        try {
                            l.onBecameBackground();
                        } catch (Exception exc) {
                            JLog.d("Listener threw exception!:" + exc.toString());
                        }
                    }
                } else {
                    JLog.d("still foreground");
                }
            }
        }, CHECK_DELAY);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    private String getCurrentActivityName(Context context) {
        try {
            ActivityManager am = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);

            // get the info from the currently running task
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            return componentInfo.getClassName();
        } catch (Exception e) {
            JLog.e(e);
        }

        return "";
    }


}
