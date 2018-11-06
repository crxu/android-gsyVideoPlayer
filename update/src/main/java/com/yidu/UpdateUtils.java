package com.yidu;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

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
 * @author andy on 2018/1/23
 */

class UpdateUtils {

    public static void checkUpdate(Context context, String packageName, int versionCode) {
        OkHttpClient client = client();
        //1:获取apk信息
        Call call = client.newCall(new Request.Builder().url(UpdateManager.URL)
                .post(new FormBody.Builder()
                        .add(UpdateManager.PACKAGENAME, packageName)
                        .add(UpdateManager.VERSIONCODE, String.valueOf(versionCode))
                        .build()).build());
        //重试次数
        int retryCount = 1;
        ResultModel.Data data = execute(call);
        while (data == null && retryCount <= 3) {
            retryCount++;
            data = execute(call.clone());
        }
        UpdateManager.log(UpdateIntentService.class, "retryCount = " + retryCount);
        //重试几次还是失败,结束
        if (data == null) {
            UpdateManager.log(UpdateIntentService.class, "resultModel == null");
            return;
        }
        UpdateManager.log(UpdateIntentService.class, "对比apk信息");
        //2:对比apk信息，判断是否需要更新
        if (data.getApkVersion() > versionCode) {//有新版本,下载文件
            ApkInfo apkInfo = getApkInfo(context);//获取保存的信息
            if (apkInfo != null) {//有缓存信息
                //缓存信息一样(三个字段一样)
                if (data.isForceUpgrade() == apkInfo.isForceUpgrade() && data.getApkVersion() == apkInfo.getVersionCode() && data.getApkUrl().equals(apkInfo.getApkUrl())) {
                    UpdateManager.log(UpdateIntentService.class, "缓存信息一样");
                    //文件下载成功
                    if (apkInfo.isSuccess() && !TextUtils.isEmpty(apkInfo.getFilePath()) && apkInfo.getFileCount() > 0) {
                        UpdateManager.log(UpdateIntentService.class, "apk下载成功");
                        File file = new File(apkInfo.getFilePath());
                        //文件存在，大小也一样,表示文件下载成功了
                        if (file.exists() && apkInfo.getFileCount() == file.length()) {
                            UpdateManager.log(UpdateIntentService.class, "有缓存的apk");
                            //安装apk
                            UpdateManager.notifyUpdate(context, file, data.isForceUpgrade());
                            return;
                        }
                    }
                }
            }
            //删除上次的apk
            Utils.delete(Utils.createDir(context.getExternalCacheDir(), "apks"));
            UpdateManager.log(UpdateIntentService.class, "没有缓存的apk");
            //没有已经下载apk的逻辑
            Call downloadCall = client.newCall(new Request.Builder().url(data.getApkUrl()).build());
            ApkInfo newInfo = new ApkInfo();
            newInfo.setApkUrl(data.getApkUrl());
            newInfo.setForceUpgrade(data.isForceUpgrade());
            newInfo.setVersionCode(data.getApkVersion());
            File download = download(createTempFile(context, data.getApkUrl()), downloadCall, newInfo);
            setApkInfo(context, newInfo);
            if (download != null) {//安装apk
                UpdateManager.notifyUpdate(context, download, data.isForceUpgrade());
            }
        } else {
            //删除上次的apk
            Utils.delete(Utils.createDir(context.getExternalCacheDir(), "apks"));
            //清空缓存信息
            setApkInfo(context, null);
            UpdateManager.log(UpdateIntentService.class, "没有新版本");
        }
    }

    private static OkHttpClient client() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(10, TimeUnit.SECONDS);
        builder.readTimeout(10, TimeUnit.SECONDS);
        builder.addNetworkInterceptor(new HttpLoggingInterceptor());
        OkHttpClient client = builder.build();
        return client;
    }

    private static ResultModel.Data execute(Call call) {
        try {
            UpdateManager.log(UpdateIntentService.class, "execute");
            Response execute = call.execute();
            if (execute.code() == 200 && execute.isSuccessful()) {
                String result = execute.body().string();
                ResultModel.Data parse = ResultModel.parse(result);
                return parse;
            }
        } catch (IOException e) {
            e.printStackTrace();
            UpdateManager.log(UpdateIntentService.class, "e = " + e.toString());
        }
        return null;
    }

    private static File download(File file, Call call, ApkInfo info) {
        BufferedSource buffer = null;
        BufferedSink sink = null;
        boolean isFailed = false;
        try {
            UpdateManager.log(UpdateIntentService.class, "download");
            Response execute = call.execute();
            if (execute.code() == 200 && execute.isSuccessful()) {
                UpdateManager.log(UpdateIntentService.class, "count = " + execute.body().contentLength());
                info.setFileCount(execute.body().contentLength());
                buffer = Okio.buffer(Okio.source(execute.body().byteStream()));
                sink = Okio.buffer(Okio.sink(file));
                sink.writeAll(buffer);
                sink.flush();
                sink.close();
                buffer.close();
                UpdateManager.log(UpdateIntentService.class, "文件下载完成");
                info.setSuccess(true);
                info.setFilePath(file.getAbsolutePath());
                return file;
            }
        } catch (IOException e) {
            e.printStackTrace();
            isFailed = true;
            info.setSuccess(false);
            UpdateManager.log(UpdateIntentService.class, "文件下载失败" + e.toString());
        } finally {
            Utils.close(buffer);
            Utils.close(sink);
            if (isFailed) {
                Utils.delete(file);
            }
        }
        return null;
    }

    private static File createTempFile(Context context, String url) {
        File apks = Utils.createDir(context.getExternalCacheDir(), "apks");
        String fileName = url.substring(url.lastIndexOf("/") + 1);
        UpdateManager.log(UpdateIntentService.class, "createTempFile fileName = " + fileName);
        File file = new File(apks, fileName);
        return file;
    }

    private static void setApkInfo(Context context, ApkInfo info) {
        String json = info == null ? "" : new Gson().toJson(info);
        UpdateManager.log(UpdateIntentService.class, "setApkInfo " + json);
        SharedPreferences sp = context.getSharedPreferences("apkversion", Context.MODE_PRIVATE);
        sp.edit().putString("version", json).commit();
    }

    private static ApkInfo getApkInfo(Context context) {
        SharedPreferences sp = context.getSharedPreferences("apkversion", Context.MODE_PRIVATE);
        String json = sp.getString("version", "");
        if (!TextUtils.isEmpty(json)) {
            UpdateManager.log(UpdateIntentService.class, "getApkInfo " + json);
            return new Gson().fromJson(json, ApkInfo.class);
        }
        return null;
    }
}
