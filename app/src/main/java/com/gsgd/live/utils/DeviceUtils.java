package com.gsgd.live.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.gsgd.live.AppConfig;
import com.gsgd.live.MainApplication;
import com.jiongbull.jlog.JLog;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class DeviceUtils {

    /**
     * 获取注册设备信息
     */
    public static String getDeviceRegisterInfo() {
        return "deviceId=" + getDeviceId() +
                "&packageName=com.readyidu.routerapp" +
                "&deviceType=android" +
                "&deviceName=" + getDeviceName() +
                "&sysVersion=" + getOSVersion() +
                "&resolution=720*1080" +
                "&country=" + getCountry() +
                "language=" + getLanguage();
    }

    /**
     * 获取设备ID
     */
    public static String getDeviceId() {
        SharedPreferences sp = MainApplication.getContext().getSharedPreferences(AppConfig.SP_XY_APP, Context.MODE_PRIVATE);
        String device_id = sp.getString(AppConfig.KEY_DEVICE_ID, "");
        if (!TextUtils.isEmpty(device_id)) {
            return device_id;
        }

        StringBuilder deviceId = new StringBuilder();
        // 渠道标志
        deviceId.append("a");

        try {
            //wifi mac地址
            String wifiMac = getMacAddress();

            if (!"02:00:00:00:00:00".equals(wifiMac)) {
                deviceId.append("wifi");
                deviceId.append(wifiMac);
                JLog.e("getDeviceId wifiMac: ", wifiMac);
            }

            String androidId = getAndroidID();
            if (!TextUtils.isEmpty(androidId)) {
                deviceId.append("androidId");
                deviceId.append(androidId);
                JLog.e("getDeviceId androidId: ", androidId);
            }

            try {
                if ("02:00:00:00:00:00".equals(wifiMac)) {
                    //如果上面都没有， 则生成一个id：随机码
                    String uuid = getUUID();
                    if (!TextUtils.isEmpty(uuid)) {
                        deviceId.append("id");
                        deviceId.append(uuid);
                        JLog.e("getDeviceId uuid: ", uuid);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        JLog.e("getDeviceId : ", deviceId.toString());

        String result = MD5.toMD5(deviceId.toString());
        sp.edit().putString(AppConfig.KEY_DEVICE_ID, result).apply();

        return result;
    }

    /**
     * 获取设备MAC地址
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>}</p>
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.INTERNET"/>}</p>
     *
     * @return MAC地址
     */
    public static String getMacAddress() {
        String macAddress = getMacAddressByWifiInfo();
        if (!"02:00:00:00:00:00".equals(macAddress)) {
            return macAddress;
        }
        macAddress = getMacAddressByNetworkInterface();
        if (!"02:00:00:00:00:00".equals(macAddress)) {
            return macAddress;
        }
        macAddress = getMacAddressByFile();
        if (!"02:00:00:00:00:00".equals(macAddress)) {
            return macAddress;
        }
        return "02:00:00:00:00:00";
    }

    /**
     * 获取设备MAC地址
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>}</p>
     *
     * @return MAC地址
     */
    @SuppressLint("HardwareIds")
    private static String getMacAddressByWifiInfo() {
        try {
            @SuppressLint("WifiManagerLeak")
            WifiManager wifi = (WifiManager) MainApplication.getContext().getSystemService(Context.WIFI_SERVICE);
            if (wifi != null) {
                WifiInfo info = wifi.getConnectionInfo();
                if (info != null) return info.getMacAddress();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "02:00:00:00:00:00";

    }

    /**
     * 获取设备MAC地址
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.INTERNET"/>}</p>
     *
     * @return MAC地址
     */
    private static String getMacAddressByNetworkInterface() {
        try {
            List<NetworkInterface> nis = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface ni : nis) {
                if (!ni.getName().equalsIgnoreCase("wlan0")) continue;
                byte[] macBytes = ni.getHardwareAddress();
                if (macBytes != null && macBytes.length > 0) {
                    StringBuilder res1 = new StringBuilder();
                    for (byte b : macBytes) {
                        res1.append(String.format("%02x:", b));
                    }
                    return res1.deleteCharAt(res1.length() - 1).toString();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "02:00:00:00:00:00";
    }

    /**
     * 获取设备MAC地址
     *
     * @return MAC地址
     */
    private static String getMacAddressByFile() {
        ShellUtils.CommandResult result = ShellUtils.execCmd("getprop wifi.interface", false);
        if (result.result == 0) {
            String name = result.successMsg;
            if (name != null) {
                result = ShellUtils.execCmd("cat /sys/class/net/" + name + "/address", false);
                if (result.result == 0) {
                    if (result.successMsg != null) {
                        return result.successMsg;
                    }
                }
            }
        }
        return "02:00:00:00:00:00";
    }

    /**
     * 获取设备AndroidID
     *
     * @return AndroidID
     */
    @SuppressLint("HardwareIds")
    public static String getAndroidID() {
        return Settings.Secure.getString(MainApplication.getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * 得到全局唯一UUID
     */
    private static String getUUID() {
        SharedPreferences sp = MainApplication.getContext().getSharedPreferences(AppConfig.SP_XY_APP, Context.MODE_PRIVATE);
        String uuid = sp.getString(AppConfig.KEY_UUID, "");
        if (TextUtils.isEmpty(uuid)) {
            uuid = UUID.randomUUID().toString();
            sp.edit().putString(AppConfig.KEY_UUID, uuid).apply();
        }
        Log.e("getUUID : ", uuid);
        return uuid;
    }

    /**
     * 获取设备所属国家
     */
    private static String getCountry() {
        return MainApplication.getContext().getResources().getConfiguration().locale.getCountry();
    }

    /**
     * 获取设备当前语言
     */
    private static String getLanguage() {
        return MainApplication.getContext().getResources().getConfiguration().locale.getLanguage();
    }

    /**
     * 设备名称
     */
    private static String getDeviceName() {
        return Build.DEVICE;
    }

    /**
     * 系统版本号
     */
    private static String getOSVersion() {
        return Build.VERSION.RELEASE;
    }


}