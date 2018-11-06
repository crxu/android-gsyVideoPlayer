package com.gsgd.live;

import com.gsgd.live.data.model.Channel;

import java.util.ArrayList;
import java.util.List;

/**
 * APP配置
 */
public class AppConfig {

    public static boolean isOpenLog = true;//是否打开日志
    public static boolean isDebug = false;//是否是测试
    public static boolean isPreRelease = false;//是否是预发布
    public static boolean isNeedReport = true;//是否上报数据

    public static final long COLLECTION_TYPE_ID = -3;//收藏源类型ID
    public static final long CUSTOM_TYPE_ID = -1;//自定义源类型ID
    public static final long CUSTOM_TYPE_ADD_ID = -2;
    public static final long CUSTOM_START_ID = 20000;//自定义ID起始ID

    public static final int CACHE_TIME = 2 * 60 * 60;//单位 s
    public static final int TIME_OUT = 10 * 1000;//超时处理时间 单位 ms
    public static final int TIME_WHAT = 6 * 1000;//错误处理时间 单位 ms

    public static final int TIME_CHECK_BLACK = 3 * 1000;//单位 ms

    //API地址
    public static String BASE_API_URL;

    static {
        if (isDebug) {
            if (isPreRelease) {
                BASE_API_URL = "http://47.97.21.98:9092/";

            } else {
                BASE_API_URL = "http://192.168.4.104:9092/";
            }

        } else {
            BASE_API_URL = "http://live.readyidu.com/";
        }
    }

    public static List<Channel> ALL_CHANNEL = new ArrayList<>();//全部频道
    public static boolean isClickFengmi = false;//标识是否点击了蜂蜜直播
    public static boolean isMenuDialogEnable = false;//标识TvDialog是否可用

    public static final String APP_ID = "10001";
    public static final String APP_PASSWORD = "123456";
    public static final String APP_API_VERSION = "1.1.5";

    public static final String SP_XY_APP = "sp_xy_app";
    public static final String KEY_DEVICE_ID = "key_device_id_mac";
    public static final String KEY_UUID = "key_uuid";
    public static final String KEY_API_URL = "key_api_url";

    public static long TIME_SPLASH = 2 * 1000;//闪屏时间
    public static long TIME_DISMISS = 10 * 1000;//隐藏时间
    public static long TIME_RIGHT_DISMISS = 6 * 1000;//隐藏时间
    public static int TIME_FAST = 10 * 1000;//快进、快退时间
    public static int TIME_FAST_LONG = 60 * 1000;//快进、快退时间

    public static final int PRESS_CODE_SELECT_TV = 1;//TV节目选择框
    public static final int PRESS_CODE_SELECT_SOURCE = 2;//源选择框
    public static final int PRESS_CODE_SELECT_SETTING = 3;//设置选择框
    public static final int PRESS_CODE_SELECT_NOTICE = 4;//提示框
    public static final int PRESS_CODE_SELECT_PROGRESS = 5;//底部进度选择框

    public static final String CHANNEL_LIST = "channel_list";
    public static final String TV_LIST = "TvList";
    public static final String TV_RESULT = "result";
    public static final String TV_TARGET_ID = "targetId";
    public static final String CHANNEL_MATCH_LIST = "channel_match_list";
    public static final int LAST_CACHE_VERSION = 22;//缓存版本

    public static String BASE_CONFIG_PATH = MainApplication.getContext().getExternalCacheDir().getAbsolutePath() + "/live_data";
    public static String BASE_CONFIG_TEMP_PATH = MainApplication.getContext().getExternalCacheDir().getAbsolutePath() + "/live_data_temp";
    public static String BASE_CUSTOM_PATH = MainApplication.getContext().getExternalCacheDir().getAbsolutePath() + "/custom_data";
    public static String BASE_CUSTOM_TEMP_PATH = MainApplication.getContext().getExternalCacheDir().getAbsolutePath() + "/custom_data_temp";
    public static final String INNER_CONFIG_NAME = "FpV9ErkG3K51N_JyFS20yLlWKUbV";//内置缓存版本

    //设置
    public static final String SP_SETTING_NAME = "sp_setting";
    public static final String KEY_SETTING_SCALE_MODE = "key_setting_scale_mode";
    public static final String KEY_SETTING_DECODE_MODE = "key_setting_decode_mode";

    //sp相关信息
    public static final String SP_NAME = "sp_live_multi";
    public static final String KEY_CHANNEL_LIST = "key_channel_list";
    public static final String KEY_SCREEN_MODE = "key_screen_mode";
    public static final String KEY_SWITCH_MODE = "key_switch_mode";
    public static final String KEY_LAST_CHANNEL_TYPE = "key_last_channel_type";
    public static final String KEY_LAST_CHANNEL = "key_last_channel";
    public static final String KEY_LAST_SOURCE = "key_last_source";
    public static final String KEY_LAST_CACHE_VERSION = "key_last_cache_version";//缓存版本
    public static final String KEY_IS_GET_NEWEST = "key_is_get_newest";//标识是否获取到最新数据
    public static final String KEY_CHANNEL_CONFIG = "key_channel_config";//配置

    //action
    public static final String ACTION_CLOSE_PLAY_GSGD = "action_close_play_gsgd";
    public static final String ACTION_HDMI_ON = "hdmi_on";
    public static final String ACTION_HDMI_OFF = "hdmi_off";

    //小益笼子，多app共享
    public static final String INNER_TYPE = "cage_tv_live";
    public static final String XY_CAGE_PKG = "com.readyidu.routerapp";
    public static final String SP_XY_CAGE = "sp_xy_cage";
    public static final String KEY_CURRENT_CAGE_TYPE = "key_current_cage_type";//标识当前笼子类型
}
