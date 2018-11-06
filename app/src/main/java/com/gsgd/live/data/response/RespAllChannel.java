package com.gsgd.live.data.response;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangqy
 * @Description
 * @date 2017/10/30
 */
public class RespAllChannel implements Parcelable {

    public List<RespChannel> channels = new ArrayList<>();
    public List<RespChannel> hotChannels = new ArrayList<>();//热门列表
    public List<RespChannel> movieList = new ArrayList<>();//电影频道

    public boolean isValid() {
        return null != channels && channels.size() > 0;
    }

    public RespAllChannel() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.channels);
        dest.writeTypedList(this.hotChannels);
        dest.writeTypedList(this.movieList);
    }

    protected RespAllChannel(Parcel in) {
        this.channels = in.createTypedArrayList(RespChannel.CREATOR);
        this.hotChannels = in.createTypedArrayList(RespChannel.CREATOR);
        this.movieList = in.createTypedArrayList(RespChannel.CREATOR);
    }

    public static final Creator<RespAllChannel> CREATOR = new Creator<RespAllChannel>() {
        @Override
        public RespAllChannel createFromParcel(Parcel source) {
            return new RespAllChannel(source);
        }

        @Override
        public RespAllChannel[] newArray(int size) {
            return new RespAllChannel[size];
        }
    };
}
