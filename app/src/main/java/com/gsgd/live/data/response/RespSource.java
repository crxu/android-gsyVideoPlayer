package com.gsgd.live.data.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * 视频源信息
 */
public class RespSource implements Parcelable {

    //1280及以上就是高清。时间2秒以内就是极速，5秒以内就是流畅，10秒就是一般

    public int id;//源id，根据id去请求播放地址
    public int resolve;//分辨率
    public float responseTime;//反应时间
    public String source;
    @SerializedName(value = "isSelf")
    public int isInnerSelf;//是否是自有的内部源 2表示是


    /**
     * 是否是高清源
     *
     * @return true表示是
     */
    public boolean isHD() {
        return resolve >= 1280;
    }

    /**
     * 获取源描述
     *
     * @return 急速/流畅/一般
     */
    public String getSpeedDesc() {
        if (responseTime > 0 && responseTime <= 2) {
            return "极速";

        } else if (responseTime > 2 && responseTime < 5) {
            return "流畅";
        }

        return "一般";
    }

    /**
     * 是否是内部源
     */
    public boolean isInnerSource() {
        return 2 == isInnerSelf;
    }

    public RespSource() {
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
        dest.writeInt(this.id);
        dest.writeInt(this.resolve);
        dest.writeFloat(this.responseTime);
        dest.writeString(this.source);
        dest.writeInt(this.isInnerSelf);
    }

    protected RespSource(Parcel in) {
        this.id = in.readInt();
        this.resolve = in.readInt();
        this.responseTime = in.readFloat();
        this.source = in.readString();
        this.isInnerSelf = in.readInt();
    }

    public static final Creator<RespSource> CREATOR = new Creator<RespSource>() {
        @Override
        public RespSource createFromParcel(Parcel source) {
            return new RespSource(source);
        }

        @Override
        public RespSource[] newArray(int size) {
            return new RespSource[size];
        }
    };
}
