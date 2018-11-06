package com.yidu;

import android.text.TextUtils;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author andy on 2018/1/23
 */
class ResultModel {
    private int errorCode;
    private String errorMessage;
    private Data data;

    public static class Data {
        private String apkUrl;
        private String apkVersion;
        private int isForceUpgrade;//0 false 1 true

        public String getApkUrl() {
            return apkUrl;
        }

        public void setApkUrl(String apkUrl) {
            this.apkUrl = apkUrl;
        }

        public int getApkVersion() {
            return Integer.valueOf(apkVersion);
        }

        public void setApkVersion(String apkVersion) {
            this.apkVersion = apkVersion;
        }

        public boolean isForceUpgrade() {
            return isForceUpgrade == 1;
        }

        public void setIsForceUpgrade(int isForceUpgrade) {
            this.isForceUpgrade = isForceUpgrade;
        }

        @Override
        public String toString() {
            return "Data{" +
                    "apkUrl='" + apkUrl + '\'' +
                    ", apkVersion='" + apkVersion + '\'' +
                    ", isForceUpgrade=" + isForceUpgrade +
                    '}';
        }
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static ResultModel.Data parse(String json) {
        try {
            UpdateManager.log(ResultModel.class, "parse");
            if (TextUtils.isEmpty(json)) {
                UpdateManager.log(ResultModel.class, "json is null");
                return null;
            }else{
                UpdateManager.log(ResultModel.class, json);
            }
            JSONObject object = new JSONObject(json);
            if (object.has("data")) {
                JSONObject data = object.optJSONObject("data");
                if (data != null) {
                    ResultModel.Data model = new ResultModel.Data();
                    model.isForceUpgrade = data.optInt("isForceUpgrade");
                    model.apkUrl = data.optString("apkUrl");
                    model.apkVersion = data.optString("apkVersion");
                    UpdateManager.log(ResultModel.class, model.toString());
                    return model;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            UpdateManager.log(ResultModel.class, "解析数据失败");
        }
        return null;
    }
}
