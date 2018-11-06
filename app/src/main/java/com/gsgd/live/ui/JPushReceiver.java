package com.gsgd.live.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TextView;

import com.gsgd.live.AppConfig;
import com.gsgd.live.MainApplication;
import com.gsgd.live.data.api.ApiModule;
import com.gsgd.live.data.events.PlayEvent;
import com.gsgd.live.utils.ContentUtils;
import com.gsgd.live.utils.SPUtil;
import com.gsgd.live.utils.ToastUtil;
import com.jiongbull.jlog.JLog;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;
import hdpfans.com.R;

/**
 * 自定义接收器
 * <p>
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class JPushReceiver extends BroadcastReceiver {

    private static final String TAG = JPushReceiver.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Bundle bundle = intent.getExtras();

            if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
                String content = bundle.getString(JPushInterface.EXTRA_MESSAGE);
                JLog.d(TAG, "******-> 接收到推送下来的自定义消息: " + content);

//                {"code":12000,"data":"命令缓存失效","message":""}
//                {"code":13004,"data":{"date":"2017-12-28 19:25:09","message":"手机绑定成功"}
                JSONObject jsonObject = new JSONObject(content);
                if (AppConfig.isDebug) {
                    //测试环境
                    if (jsonObject.has("code") && 12001 == jsonObject.optInt("code")) {
                        //设置标志位
                        SPUtil.putBoolean(MainApplication.getContext(), AppConfig.SP_NAME, AppConfig.KEY_IS_GET_NEWEST, false);

                        //更新节目列表
                        ApiModule.getApiManager().clearCache();
                        EventBus.getDefault().post(new PlayEvent.RefreshCache());
                    }

                } else {
                    if (jsonObject.has("code") && 12000 == jsonObject.optInt("code")) {
                        //设置标志位
                        SPUtil.putBoolean(MainApplication.getContext(), AppConfig.SP_NAME, AppConfig.KEY_IS_GET_NEWEST, false);

                        //更新节目列表
                        ApiModule.getApiManager().clearCache();
                        EventBus.getDefault().post(new PlayEvent.RefreshCache());
                    }
                }

                if (jsonObject.has("code") && 13004 == jsonObject.optInt("code")) {
                    //手机绑定
                    EventBus.getDefault().post(new PlayEvent.BindDeviceEvent());

                    String msg = jsonObject.optJSONObject("data").optString("message");

                    TextView view = new TextView(context);
                    view.setBackgroundResource(R.drawable.bg_dialog_reset);
                    view.setText(msg);
                    view.setTextSize(18);
                    view.setGravity(Gravity.CENTER_VERTICAL);
                    view.setPadding(27, 9, 27, 9);
                    view.setTextColor(Color.parseColor("#F0F0F0"));
                    view.setCompoundDrawablePadding(6);
                    view.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.icon_chenggong), null, null, null);
                    ToastUtil.showToastView(view);
                }

                if (jsonObject.has("code") && 13006 == jsonObject.optInt("code")) {
                    //手机同步自建源
                    EventBus.getDefault().post(new PlayEvent.SynCustomEvent());
                }
            }

        } catch (Exception e) {
            JLog.e(e);
        }
    }

}
