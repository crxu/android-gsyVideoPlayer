package com.gsgd.live.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gsgd.live.AppConfig;
import com.gsgd.live.data.api.ApiModule;
import com.gsgd.live.data.model.Order;
import com.gsgd.live.data.model.PlayOrder;
import com.gsgd.live.data.model.PlayOrderList;
import com.gsgd.live.data.response.RespPlayOrder;
import com.gsgd.live.ui.ClockReceiver;
import com.jiongbull.jlog.JLog;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.observers.DisposableObserver;

/**
 * Created by andy on 2017/12/25.
 */

public class PlayOrdersMananger {

    private static SparseIntArray sCurrentChannelIds = new SparseIntArray(16);
    private static SparseArray<PlayOrderList> sOrders = new SparseArray<>(16);
    private static WeakReference<OnDownloadListener> sListener;
    private static List<Order> sCacheOrders;
    public static final int DELAY_START_CLCOK = 10000;
    private static final int MINUTE = 60 * 1000;
    private static final int HOUR = MINUTE * 60;
    private static final String SP_NAME = "tv_order";
    private static final String ORDER_KEY = "order_%s";

    /**
     * 获取提示长时间看电视的时间间隔
     *
     * @return
     */
    public static long getPlayTimeDis() {
        if (AppConfig.isDebug) {//测试
            return 1000 * 60 * 5;
        } else {//正式
            return HOUR * 5;
        }
    }

    public static void getPlayOrder(final int channelId, OnDownloadListener listener) {
        sListener = new WeakReference<>(listener);
        if (listener != null) {
            listener.onPreStart(channelId);
        }
        PlayOrderList list = sOrders.get(channelId);
        if (list != null && listener != null) {//使用缓存
            listener.onSuccess(channelId, list.getOrders());
            return;
        }
        if (listener != null) {
            listener.onStart(channelId);
        }
        //也没有下载
        if (sCurrentChannelIds.get(channelId, -1) == -1) {
            sCurrentChannelIds.put(channelId, channelId);
            ApiModule.getApiManager().getPlayBillOrder(channelId)
                    .subscribeWith(new DisposableObserver<PlayOrderList>() {
                        @Override
                        public void onNext(PlayOrderList playOrderList) {
                            if (playOrderList.getOrders().size() > 0) {
                                sOrders.put(channelId, playOrderList);
                                if (sListener != null && sListener.get() != null) {
                                    sListener.get().onSuccess(channelId, playOrderList.getOrders());
                                }
                            } else {
                                if (sListener != null && sListener.get() != null) {
                                    sListener.get().onFailed(channelId);
                                }
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            sCurrentChannelIds.delete(channelId);
                            if (sListener != null && sListener.get() != null) {
                                sListener.get().onFailed(channelId);
                            }
                        }

                        @Override
                        public void onComplete() {
                            sCurrentChannelIds.delete(channelId);
                        }
                    });
        }
    }

    public static void clearOrder() {
        sOrders.clear();
        sCurrentChannelIds.clear();
        sListener = null;
    }

    public static void clear() {
        clearOrder();
        clearCacheOrder();
    }

    public static void clearCacheOrder() {
        if (sCacheOrders != null && sCacheOrders.size() > 0) {
            sCacheOrders.clear();
        }
        sCacheOrders = null;
    }

    public interface OnDownloadListener {
        void onSuccess(int channelId, List<PlayOrder> list);

        void onFailed(int channelId);

        void onStart(long channelId);

        void onPreStart(long channelId);
    }

    public static void deleteOrder(Context context, String key) {
        try {
            Order order = new Order();
            order.setKey(key);
            getOrders(context).remove(order);
            put(context, new Gson().toJson(getOrders(context)), ORDER_KEY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 取消预约
     *
     * @param context
     * @param order
     */
    public static void cancelOrder(Context context, PlayOrder order) {
        try {
            Order playOrder = new Order();
            playOrder.setKey(String.format("%s#%s#%s", order.getChannelId(), order.getDate(), order.getShowTime()));
            int indexOf = getOrders(context).indexOf(playOrder);
            if (indexOf != -1) {
                getOrders(context).remove(indexOf);
                put(context, new Gson().toJson(getOrders(context)), ORDER_KEY);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 预约
     *
     * @param context
     * @param order
     */
    public static void order(Context context, PlayOrder order) {
        try {//清除掉过期的预约
            Iterator<Order> iterator = getOrders(context).iterator();
            while (iterator.hasNext()) {
                Order next = iterator.next();
                if (next.getTime() < System.currentTimeMillis()) {
                    iterator.remove();
                }
            }
            Order playOrder = new Order();
            playOrder.setKey(String.format("%s#%s#%s", order.getChannelId(), order.getDate(), order.getShowTime()));
            playOrder.setShowTime(order.getShowTime());
            playOrder.setChannelId(order.getChannelId());
            playOrder.setChannelType(order.getChannelType());
            playOrder.setChannelName(order.getChannelName());
            playOrder.setType(order.getType());
            playOrder.setDate(order.getDate());
            playOrder.setTime(DateUtils.parse(order.getDate() + " " + order.getShowTime()));
            getOrders(context).add(playOrder);
            Collections.sort(getOrders(context));
            put(context, new Gson().toJson(getOrders(context)), ORDER_KEY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        PlayOrdersMananger.startClock(context);
    }

    public static boolean isOrder(Context context, PlayOrder order) {
        if (getOrders(context).isEmpty()) {
            return false;
        }
        try {
            Order playOrder = new Order();
            playOrder.setKey(order.getKey());
            int indexOf = getOrders(context).indexOf(playOrder);
            return indexOf != -1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Order firstOrder(Context context) {
        if (getOrders(context).isEmpty()) {
            return null;
        } else {
            Order order = getOrders(context).get(0);
            if (order.getTime() - System.currentTimeMillis() < MINUTE / 2) {
                return order;
            }
            return null;
        }
    }

    private static List<Order> getOrders(Context context) {
        if (sCacheOrders == null) {
            try {
                String orders = get(context, ORDER_KEY);
                if (TextUtils.isEmpty(orders)) {
                    sCacheOrders = new ArrayList<>();
                } else {
                    sCacheOrders = new Gson().fromJson(orders, new TypeToken<List<Order>>() {
                    }.getType());
                    //清除掉过期的提醒
                    Iterator<Order> iterator = sCacheOrders.iterator();
                    while (iterator.hasNext()) {
                        Order next = iterator.next();
                        if (next.getTime() < System.currentTimeMillis()) {
                            iterator.remove();
                        }
                    }
                    put(context, new Gson().toJson(getOrders(context)), ORDER_KEY);
                }
            } catch (Exception e) {
                e.printStackTrace();
                clearOrder(context);
                sCacheOrders = new ArrayList<>();
            }
        }
        return sCacheOrders;
    }

    private static void clearOrder(Context context) {
        put(context, "", ORDER_KEY);
    }

    public static void startClock(Context context) {
        if (context == null) return;
        List<Order> orders = getOrders(context);
        if (orders.isEmpty()) {
            JLog.e("Order", "没有预约节目");
            return;
        }
        Order order = orders.get(0);
        long startTime = order.getTime() - MINUTE / 2;
        if ((startTime - System.currentTimeMillis()) < MINUTE / 6) {
            startTime = System.currentTimeMillis() + MINUTE / 6;
        }
        Intent intent = new Intent(context, ClockReceiver.class);
        intent.setAction(ClockReceiver.ALARM_ACTION);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (am != null) {
            //再次发送
            am.set(AlarmManager.RTC_WAKEUP, startTime, sender);
        }
    }

    private static void put(Context context, String text, String format) {
        if (context == null) return;
        context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).edit().putString(getKey(format), text).apply();
    }

    private static String get(Context context, String format) {
        if (context == null) return "";
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(getKey(format), "");
    }

    private static String getKey(String format) {
        return String.format(format, LoginUser.getUserId());
    }

    /**
     * 设置当前开始播放时间
     * 该方法在进入该应用调用，有按钮事件处理时也调用
     *
     * @param context
     */
    public static void setPlayTime(Context context) {
        if (context != null) {
            put(context, System.currentTimeMillis() + "", "time_%s");
        }
    }

    /**
     * 获取播放的时长
     *
     * @param context
     * @return 播放时间的毫秒
     */
    public static long getPlayTime(Context context) {
        if (context != null) {
            String s = get(context, "time_%s");
            try {
                return System.currentTimeMillis() - Long.parseLong(s);
            } catch (Exception e) {
                e.printStackTrace();
                JLog.e(e.toString());
                return 0;
            }
        } else {
            return 0;
        }
    }

    /**
     * 是不是长时间播放,根据设置的时间段来判断
     *
     * @param context
     * @return
     */
    public static boolean isLongPlay(Context context) {
        long playTime = getPlayTime(context);
        if (playTime < getPlayTimeDis()) {
            return false;
        }
        return true;
    }
}
