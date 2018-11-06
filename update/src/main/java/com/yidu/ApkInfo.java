package com.yidu;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author andy on 2018/1/23
 *         保存在sp里面的信息
 */
public class ApkInfo {
    private String apkUrl;
    private int versionCode;
    private boolean isForceUpgrade;
    private boolean isSuccess;//是否下载成功
    private String filePath;//下载成功的文件路径
    private long fileCount;//文件长度

    public boolean isForceUpgrade() {
        return isForceUpgrade;
    }

    public void setForceUpgrade(boolean forceUpgrade) {
        isForceUpgrade = forceUpgrade;
    }

    public long getFileCount() {
        return fileCount;
    }

    public void setFileCount(long fileCount) {
        this.fileCount = fileCount;
    }

    public String getApkUrl() {
        return apkUrl;
    }

    public void setApkUrl(String apkUrl) {
        this.apkUrl = apkUrl;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public String toString() {
        return "ApkInfo{" +
                "apkUrl='" + apkUrl + '\'' +
                ", versionCode=" + versionCode +
                ", isForceUpgrade=" + isForceUpgrade +
                ", isSuccess=" + isSuccess +
                ", filePath='" + filePath + '\'' +
                ", fileCount=" + fileCount +
                '}';
    }
}
