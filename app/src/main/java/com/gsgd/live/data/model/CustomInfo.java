package com.gsgd.live.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * @author zhangqy
 * @Description 自建源
 * @date 2017/12/26
 */
public class CustomInfo implements Parcelable {

    @SerializedName(value = "c")
    public String sourceName;//源名称
    @SerializedName(value = "s")
    public String sourceUrl;//源地址

    public CustomInfo() {
    }

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
        dest.writeString(this.sourceName);
        dest.writeString(this.sourceUrl);
    }

    protected CustomInfo(Parcel in) {
        this.sourceName = in.readString();
        this.sourceUrl = in.readString();
    }

    public static final Creator<CustomInfo> CREATOR = new Creator<CustomInfo>() {
        @Override
        public CustomInfo createFromParcel(Parcel source) {
            return new CustomInfo(source);
        }

        @Override
        public CustomInfo[] newArray(int size) {
            return new CustomInfo[size];
        }
    };
}
