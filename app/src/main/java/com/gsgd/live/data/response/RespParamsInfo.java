package com.gsgd.live.data.response;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

/**
 * 参数信息
 */
public class RespParamsInfo {

    public int errorCode;
    public ParamsInfo data;

    public class ParamsInfo {

        @SerializedName(value = "xiaoYiApiUrl")
        public String brainUrl;

    }

    public boolean isValid() {
        return 200 == errorCode
                && null != data
                && !TextUtils.isEmpty(data.brainUrl);
    }

}
