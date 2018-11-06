package com.gsgd.live.utils;

import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.gsgd.live.MainApplication;
import com.jiongbull.jlog.JLog;

public final class ToastUtil {

    private static Toast toast = null;
    private static Handler handlerResult = new Handler(Looper.getMainLooper());

    public static synchronized void showToast(final String str) {
        handlerResult.post(new Runnable() {
            @Override
            public void run() {
                try {
                    if (toast == null) {
                        toast = Toast.makeText(MainApplication.getContext(), str, Toast.LENGTH_SHORT);
                    } else {
                        toast.setText(str);
                    }
                    toast.show();

                } catch (Exception e) {
                    JLog.e(e);
                }
            }
        });
    }

    public static synchronized void showToast(final int resId) {
        handlerResult.post(new Runnable() {
            @Override
            public void run() {
                try {
                    if (toast == null) {
                        toast = Toast.makeText(MainApplication.getContext(), resId, Toast.LENGTH_SHORT);
                    } else {
                        toast.setText(resId);
                    }
                    toast.show();

                } catch (Exception e) {
                    JLog.e(e);
                }
            }
        });
    }

    public static synchronized void showToastLong(final String str) {
        handlerResult.post(new Runnable() {
            @Override
            public void run() {
                try {
                    if (toast == null) {
                        toast = Toast.makeText(MainApplication.getContext(), str, Toast.LENGTH_LONG);
                    } else {
                        toast.setText(str);
                    }
                    toast.show();

                } catch (Exception e) {
                    JLog.e(e);
                }
            }
        });
    }

    public static synchronized void showToastLong(final int resId) {
        handlerResult.post(new Runnable() {
            @Override
            public void run() {
                try {
                    if (toast == null) {
                        toast = Toast.makeText(MainApplication.getContext(), resId, Toast.LENGTH_LONG);
                    } else {
                        toast.setText(resId);
                    }
                    toast.show();

                } catch (Exception e) {
                    JLog.e(e);
                }
            }
        });
    }

    public static void showToastView(final View view) {
        handlerResult.post(new Runnable() {
            @Override
            public void run() {
                try {
                    if (toast == null) {
                        toast = new Toast(MainApplication.getContext());
                    }
                    toast.setView(view);
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 125, 0);
                    toast.show();

                } catch (Exception e) {
                    JLog.e(e);
                }
            }
        });
    }

}
