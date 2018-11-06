package com.gsgd.live.data.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * Created by andy on 2017/12/25.
 */

public class Order implements Comparable<Order>, Parcelable {

    private String key;//channelId date showTime 组成
    private String showTime;
    private String date;
    private String channelName;
    private String channelType;
    private long time;
    private long channelId;
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getChannelType() {
        return channelType;
    }

    public void setChannelType(String channelType) {
        this.channelType = channelType;
    }

    @Override
    public boolean equals(Object obj) {
        return key.equals(((Order) obj).key);
    }

    @Override
    public int compareTo(@NonNull Order o) {
        return (int) (time - o.time);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getShowTime() {
        return showTime;
    }

    public void setShowTime(String showTime) {
        this.showTime = showTime;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getChannelId() {
        return channelId;
    }

    public void setChannelId(long channelId) {
        this.channelId = channelId;
    }

    @Override
    public String toString() {
        return "Order{" +
                "key='" + key + '\'' +
                ", showTime='" + showTime + '\'' +
                ", date='" + date + '\'' +
                ", channelName='" + channelName + '\'' +
                ", channelType='" + channelType + '\'' +
                ", time=" + time +
                ", channelId=" + channelId +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.key);
        dest.writeString(this.showTime);
        dest.writeString(this.date);
        dest.writeString(this.channelName);
        dest.writeString(this.channelType);
        dest.writeLong(this.time);
        dest.writeLong(this.channelId);
        dest.writeString(this.type);
    }

    public Order() {
    }

    protected Order(Parcel in) {
        this.key = in.readString();
        this.showTime = in.readString();
        this.date = in.readString();
        this.channelName = in.readString();
        this.channelType = in.readString();
        this.time = in.readLong();
        this.channelId = in.readLong();
        this.type = in.readString();
    }

    public static final Parcelable.Creator<Order> CREATOR = new Parcelable.Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel source) {
            return new Order(source);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };
}
