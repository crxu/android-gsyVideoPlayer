package com.gsgd.live.data.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;

/**
 * @author zhangqy
 * @Description 频道配置
 * @date 2017/12/19
 */
public class RespConfig implements Parcelable {

    public String confName;//": "channelInfo",
    public String confUrl;//": "http://p15mjwx7g.bkt.clouddn.com/FuLjA4E-z1KViaO9JLzxl4jvJKa5",
    public String hash;//": "FuLjA4E-z1KViaO9JLzxl4jvJKa5",
    public String version;//": "1.1.4"

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.confName);
        dest.writeString(this.confUrl);
        dest.writeString(this.hash);
        dest.writeString(this.version);
    }

    public RespConfig() {
    }

    protected RespConfig(Parcel in) {
        this.confName = in.readString();
        this.confUrl = in.readString();
        this.hash = in.readString();
        this.version = in.readString();
    }

    public static final Parcelable.Creator<RespConfig> CREATOR = new Parcelable.Creator<RespConfig>() {
        @Override
        public RespConfig createFromParcel(Parcel source) {
            return new RespConfig(source);
        }

        @Override
        public RespConfig[] newArray(int size) {
            return new RespConfig[size];
        }
    };
}
