package com.gsgd.live.data.response;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author zhangqy
 * @Description 设备信息
 * @date 2017/12/27
 */
public class RespDevice implements Parcelable {

    public String deviceId;//机顶盒设备id
    public int id;//手机id
    public String phoneAlias;//手机别名
    public String phoneId;//手机id
    public String phoneName;//手机名
    public int userId;//小益账户
    public String nickname;

    public RespDevice() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.deviceId);
        dest.writeInt(this.id);
        dest.writeString(this.phoneAlias);
        dest.writeString(this.phoneId);
        dest.writeString(this.phoneName);
        dest.writeInt(this.userId);
        dest.writeString(this.nickname);
    }

    protected RespDevice(Parcel in) {
        this.deviceId = in.readString();
        this.id = in.readInt();
        this.phoneAlias = in.readString();
        this.phoneId = in.readString();
        this.phoneName = in.readString();
        this.userId = in.readInt();
        this.nickname = in.readString();
    }

    public static final Creator<RespDevice> CREATOR = new Creator<RespDevice>() {
        @Override
        public RespDevice createFromParcel(Parcel source) {
            return new RespDevice(source);
        }

        @Override
        public RespDevice[] newArray(int size) {
            return new RespDevice[size];
        }
    };
}
