package com.yidu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

/**
 * @author andy on 2018/1/23
 *         下载apk成功后接收消息的广播,用于给调用方实现，完成提示逻辑
 */
public abstract class CheckUpdateReceiver extends BroadcastReceiver {
    @Override
    public final void onReceive(Context context, Intent intent) {
        UpdateManager.log(CheckUpdateReceiver.class, intent.getAction());
        if (UpdateManager.UPDATE_ACTION.equals(intent.getAction())) {
            String filePath = intent.getStringExtra(UpdateManager.FILE_PATH);
            boolean isForceUpgrade = intent.getBooleanExtra(UpdateManager.IS_FORCE_UPGRADE, false);
            if (!TextUtils.isEmpty(filePath)) {
                UpdateManager.log(CheckUpdateReceiver.class, "onReceive");
                UpdateManager.log(CheckUpdateReceiver.class, "filePath = " + filePath);
                UpdateManager.log(CheckUpdateReceiver.class, "isForceUpgrade = " + isForceUpgrade);
                onReceive(filePath, isForceUpgrade);
            }
        }
    }

    public abstract void onReceive(String filePath, boolean isForceUpgrade);

}
