package com.gsgd.live.data.api;

public class ApiException extends Exception {

    //影视直播状态码
    public static int CODE_OK = 200;//成功
    public static int CODE_OK_RESULT_NULL = 204;// 成功但返回为空
    public static int CODE_REQ_ERROR = 10000;// 请求异常
    public static int CODE_TIMEOUT = 408;// 请求超时
    public static int CODE_PARAMS_ERROR = 400;// 参数错误
    public static int CODE_NO_ADDRESS = 11000;// 无可用地址
    public static int CODE_CACHE_OUT = 12001;// 缓存过期
    public static int CODE_CHANGE_PLAY = 12100;// 播放器切换
    public static int CODE_RE_BIND = 13000;// 重复绑定
    public static int CODE_OVER_LIMIT = 13001;// 机顶盒绑定数达到上限
    public static int CODE_ACCOUNT_NOT_FIND = 13003;// 账号不存在
    public static int CODE_BIND_OK = 13004;// 绑定成功
    public static int CODE_BIND_ERROR = 13005;// 绑定失败
    public static int CODE_SYN = 13006;// 同步

    private final int code;
    private final String message;

    public ApiException(int errCode, String errMsg) {
        this.code = errCode;
        this.message = errMsg;
    }

    public int code() {
        return code;
    }

    public String message() {
        return message;
    }
}
