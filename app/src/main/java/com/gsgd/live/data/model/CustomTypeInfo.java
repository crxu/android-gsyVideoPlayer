package com.gsgd.live.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangqy
 * @Description 自建频道
 * @date 2017/12/26
 */
public class CustomTypeInfo implements Parcelable {

    public int userId;
    public int type;//-1 引导用户；0 正常设备
    public String phoneName;//设备名称
    public List<CustomInfo> sources = new ArrayList<>();

    public CustomTypeInfo() {
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
        dest.writeInt(this.userId);
        dest.writeInt(this.type);
        dest.writeString(this.phoneName);
        dest.writeTypedList(this.sources);
    }

    protected CustomTypeInfo(Parcel in) {
        this.userId = in.readInt();
        this.type = in.readInt();
        this.phoneName = in.readString();
        this.sources = in.createTypedArrayList(CustomInfo.CREATOR);
    }

    public static final Creator<CustomTypeInfo> CREATOR = new Creator<CustomTypeInfo>() {
        @Override
        public CustomTypeInfo createFromParcel(Parcel source) {
            return new CustomTypeInfo(source);
        }

        @Override
        public CustomTypeInfo[] newArray(int size) {
            return new CustomTypeInfo[size];
        }
    };
}
