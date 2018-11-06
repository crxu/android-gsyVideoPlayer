package com.gsgd.live.data.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * @author zhangqy
 * @Description 用户自建数据
 * @date 2017/12/27
 */
public class RespCustom implements Parcelable {

    @SerializedName(value = "userId")
    public int userId;//用户Id
    public String confUrl;//用户自定义源地址
    @SerializedName(value = "defineName")
    public String definedName;//用户自定义源名称

    public RespCustom() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.userId);
        dest.writeString(this.confUrl);
        dest.writeString(this.definedName);
    }

    protected RespCustom(Parcel in) {
        this.userId = in.readInt();
        this.confUrl = in.readString();
        this.definedName = in.readString();
    }

    public static final Creator<RespCustom> CREATOR = new Creator<RespCustom>() {
        @Override
        public RespCustom createFromParcel(Parcel source) {
            return new RespCustom(source);
        }

        @Override
        public RespCustom[] newArray(int size) {
            return new RespCustom[size];
        }
    };
}
