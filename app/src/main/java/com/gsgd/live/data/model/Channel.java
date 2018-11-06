package com.gsgd.live.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.gsgd.live.data.response.RespSource;

import java.util.ArrayList;
import java.util.List;

/**
 * 频道
 */
public class Channel implements Parcelable {

    public long id;
    public List<String> parentId = new ArrayList<>();//对应的栏目id列表
    public String channel;//节目名称
    public List<RespSource> sources = new ArrayList<>();//播放源地址
    public int subParentId;//对应父节目id
    public List<Channel> subChannels = new ArrayList<>();//子节目

    public int isFengmi;//是否是蜂蜜电影
    public long fengmiId;//蜂蜜视频id
    public int orderIndex;//集数
    public int playType; //3 表示为蜂蜜或天翼点播

    public int collectionStatus = 0;//收藏状态 1已收藏
    public int isCollectionType = 0;//是否是收藏类型 1是
    public int isMovie = 0;//是否是电影类型 1是
    public boolean isCustom;//是否是自定义源

    public boolean isFengMiType(){
        return playType == 3 && isFengMi();
    }

    public boolean isFengMi() {
        return isFengmi == 1;
    }

    public boolean isMovie() {
        return isMovie == 1;
    }

    @Override
    public boolean equals(Object obj) {
        return id == ((Channel)obj).id;
    }

    /**
     * 是否有子节目列表
     */
    public boolean hadSubChannel() {
        return null != subChannels && subChannels.size() > 0;
    }

    public Channel() {
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
        dest.writeLong(this.id);
        dest.writeStringList(this.parentId);
        dest.writeString(this.channel);
        dest.writeTypedList(this.sources);
        dest.writeInt(this.subParentId);
        dest.writeTypedList(this.subChannels);
        dest.writeInt(this.isFengmi);
        dest.writeLong(this.fengmiId);
        dest.writeInt(this.orderIndex);
        dest.writeInt(this.playType);
        dest.writeInt(this.collectionStatus);
        dest.writeInt(this.isCollectionType);
        dest.writeInt(this.isMovie);
        dest.writeByte(this.isCustom ? (byte) 1 : (byte) 0);
    }

    protected Channel(Parcel in) {
        this.id = in.readLong();
        this.parentId = in.createStringArrayList();
        this.channel = in.readString();
        this.sources = in.createTypedArrayList(RespSource.CREATOR);
        this.subParentId = in.readInt();
        this.subChannels = in.createTypedArrayList(Channel.CREATOR);
        this.isFengmi = in.readInt();
        this.fengmiId = in.readLong();
        this.orderIndex = in.readInt();
        this.playType = in.readInt();
        this.collectionStatus = in.readInt();
        this.isCollectionType = in.readInt();
        this.isMovie = in.readInt();
        this.isCustom = in.readByte() != 0;
    }

    public static final Creator<Channel> CREATOR = new Creator<Channel>() {
        @Override
        public Channel createFromParcel(Parcel source) {
            return new Channel(source);
        }

        @Override
        public Channel[] newArray(int size) {
            return new Channel[size];
        }
    };
}
