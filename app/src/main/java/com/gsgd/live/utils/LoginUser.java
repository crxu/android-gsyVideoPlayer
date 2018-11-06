package com.gsgd.live.utils;

import android.os.Environment;
import android.text.TextUtils;

import java.io.File;


/**
 * Created by bruce on 2017/2/10.
 */
public class LoginUser {

    private static final String SP_KEY_USER_TOKEN = "UserToken";
    private static final String SP_KEY_USER_ID = "UserId";
    private static final String SP_KEY_USER_RC_TOKEN = "UserRCToken";
    private static final String SP_KEY_USER_PHOTO_URL = "UserPhotoUrl";
    private static final String SP_KEY_USER_NAME = "UserName";
    private static final String SP_KEY_USER_AGE = "UserAge";
    private static final String SP_KEY_USER_SEX = "UserSex";
    private static final String SP_KEY_USER_BIRTHDAY = "UserBirthday";
    private static final String SP_KEY_USER_MOBILE = "UserMobile";
    private static final String SP_KEY_USER_CenterUserId = "UserCenterUserId"; //呼叫中心客服id
    private static final String SP_KEY_USER_CommunityMobile = "communityMobile";
    private static final String SP_KEY_DEVICE_KEY = "DEVICE_KEY";
    private static final String SP_KEY_USER_TYPE = "UserType";

    /**
     * 判断是否登录
     *
     * @return
     */
    public static boolean isLogin() {
        String token = SharedPreferencesImp.getInstance().getSharedPreferences(SP_KEY_USER_TOKEN);
        if (!TextUtils.isEmpty(token)) {
            return true;
        }
        return false;
    }

    public static String getUserName() {
        return SharedPreferencesImp.getInstance().getSharedPreferences(SP_KEY_USER_NAME);
    }

    public static String getPhotoUrl() {
        return SharedPreferencesImp.getInstance().getSharedPreferences(SP_KEY_USER_PHOTO_URL);
    }

    public static String getToken() {
        return SharedPreferencesImp.getInstance().getSharedPreferences(SP_KEY_USER_TOKEN);
    }

    public static String getDeviceKey() {
        return SharedPreferencesImp.getInstance().getSharedPreferences(SP_KEY_DEVICE_KEY);
    }

    public static String getUserId() {
        return SharedPreferencesImp.getInstance().getSharedPreferences(SP_KEY_USER_ID);
    }

    public static String getSex() {
        return SharedPreferencesImp.getInstance().getSharedPreferences(SP_KEY_USER_SEX);
    }

    public static String getBirthday() {
        return SharedPreferencesImp.getInstance().getSharedPreferences(SP_KEY_USER_BIRTHDAY);
    }

    public static String getMobile() {
        return SharedPreferencesImp.getInstance().getSharedPreferences(SP_KEY_USER_MOBILE);
    }

    public static String geCenterUserId() {
        return SharedPreferencesImp.getInstance().getSharedPreferences(SP_KEY_USER_CenterUserId);
    }

    public static String geCommunityMobile() {
        return SharedPreferencesImp.getInstance().getSharedPreferences(SP_KEY_USER_CommunityMobile);
    }

    public static String getUnReadCallCenter() {
        return SharedPreferencesImp.getInstance().getSharedPreferences("unread_call_center");
    }

    public static String getUnReadChat() {
        return SharedPreferencesImp.getInstance().getSharedPreferences("unread_chat");
    }

    public static String getTvType() {
        return SharedPreferencesImp.getInstance().getSharedPreferences("tvType");
    }

    public static String getLatitude() {
        return SharedPreferencesImp.getInstance().getSharedPreferences("Latitude");
    }

    public static String getLongitude() {
        return SharedPreferencesImp.getInstance().getSharedPreferences("Longitude");
    }

    public static String getPbPage() {
        return SharedPreferencesImp.getInstance().getSharedPreferences("pb_page");
    }

    public static String getPbTime() {
        return SharedPreferencesImp.getInstance().getSharedPreferences("pbtime");
    }

    public static String getAppDeviceKey() {
        return SharedPreferencesImp.getInstance().getSharedPreferences("device_key");
    }

    public static String getXYiApiUrl() {
        return SharedPreferencesImp.getInstance().getSharedPreferences("xiaoYiApiUrl");
    }

    public static String getMyDwCity() {
        return SharedPreferencesImp.getInstance().getSharedPreferences("my_dw_city");
    }

    public static String getMyDwCounty() {
        return SharedPreferencesImp.getInstance().getSharedPreferences("my_dw_county");
    }

    /**
     * 是否是正常的用户类型
     *
     * @return true表示是
     */
    public static boolean isNormalUserType() {
        String spType = SharedPreferencesImp.getInstance().getSharedPreferences(SP_KEY_USER_TYPE);

        try {
            File file = new File(Environment.getExternalStoragePublicDirectory("") + "/.agedUType");
            ACache mCache = ACache.get(file);
            if (!"1".equals(spType)) {
                mCache.put("uType", "");
            }

            String type = mCache.getAsString("uType");

            return "1".equals(type);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "1".equals(spType);
    }

}
