package com.gsgd.live.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.gsgd.live.data.model.Order;
import com.gsgd.live.utils.PlayOrdersMananger;
import com.jiongbull.jlog.JLog;

import java.util.logging.Handler;

/**
 * Created by andy on 2017/12/25.
 */

public class ClockReceiver extends BroadcastReceiver {

    public static final String ALARM_ACTION = "tv.readyidu.order.action";
    public static final String ACCOUNT_ACTION = "tv.readyidu.RYIM";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ACCOUNT_ACTION.equals(intent.getAction())) {
            //1为切换账号,0为退出账号
            int type = intent.getIntExtra("IM_type", 0);
            if (type == 1) {//账号切换
                PlayOrdersMananger.clearCacheOrder();
                PlayOrdersMananger.startClock(context);
            } else if (type == 0) {
                PlayOrdersMananger.clearCacheOrder();
            }
        } else if (ALARM_ACTION.equals(intent.getAction())) {
            //这里获取最新
            Order order = PlayOrdersMananger.firstOrder(context);
            if (order != null) {
                if (VideoPlayerActivity.isShowOrder()) {
                    Intent intent1 = new Intent(context, PlayOrderActivity.class);
                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent1);
                } else {
                    PlayOrdersMananger.deleteOrder(context, order.getKey());
                    PlayOrdersMananger.startClock(context);
                }
            } else {
                PlayOrdersMananger.startClock(context);
            }
        }
    }
}
