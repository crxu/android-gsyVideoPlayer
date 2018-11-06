package com.yidu;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

/**
 * 负责检测apk信息，下载apk的服务类
 * 1:检测apk信息
 * 2:有更新下载apk
 * 3:下载完成，发送广播
 * sp 保存本次下载文件信息以及文件是否下载成功
 *
 * @author andy on 2018/1/22
 */
public final class UpdateIntentService extends IntentService {

    private boolean isChecking;

    public UpdateIntentService() {
        super("UpdateIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        UpdateManager.log(UpdateIntentService.class, "onHandleIntent");
        String type = intent.getStringExtra(UpdateManager.TYPE);
        UpdateManager.log(UpdateIntentService.class, "type = " + type);
        if (UpdateManager.UPDATE.equals(type) && !isChecking) {//防止外部启动
            isChecking = true;
            String packageName = intent.getStringExtra(UpdateManager.PACKAGENAME);
            int versionCode = intent.getIntExtra(UpdateManager.VERSIONCODE, 1);
            UpdateManager.log(UpdateIntentService.class, "packageName = " + packageName);
            UpdateManager.log(UpdateIntentService.class, "versionCode = " + versionCode);
            UpdateUtils.checkUpdate(this, packageName, versionCode);
        }
    }


}
