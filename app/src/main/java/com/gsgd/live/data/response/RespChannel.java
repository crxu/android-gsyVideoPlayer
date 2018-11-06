package com.gsgd.live.data.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * 节目信息
 */
public class RespChannel implements Parcelable {

    public long id;
    public String channel;//名称
    public String source;//播放源，以"|"分割
    public List<RespSource> sources = new ArrayList<>();
    @SerializedName(value = "typeid")
    public String typeId;//对应主栏目分类,有多个，以"|"分割
    @SerializedName(value = "parentid")
    public int parentId;//对应父节目id
    public List<RespChannel> subChannels = new ArrayList<>();//子节目
    public int isMovie;//1表示是电影
    public int isFengmi;//1 是蜂蜜电影
    public long fengmiId;//蜂蜜视频id
    public int orderIndex;//集数
    public int playType; //2 表示为蜂蜜

    public RespChannel() {
    }

    /**
     * 是否是分级节目
     */
    public boolean isSubChannel() {
        return parentId > 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.channel);
        dest.writeString(this.source);
        dest.writeTypedList(this.sources);
        dest.writeString(this.typeId);
        dest.writeInt(this.parentId);
        dest.writeTypedList(this.subChannels);
        dest.writeInt(this.isMovie);
        dest.writeInt(this.isFengmi);
        dest.writeLong(this.fengmiId);
        dest.writeInt(this.orderIndex);
        dest.writeInt(this.playType);
    }

    protected RespChannel(Parcel in) {
        this.id = in.readLong();
        this.channel = in.readString();
        this.source = in.readString();
        this.sources = in.createTypedArrayList(RespSource.CREATOR);
        this.typeId = in.readString();
        this.parentId = in.readInt();
        this.subChannels = in.createTypedArrayList(RespChannel.CREATOR);
        this.isMovie = in.readInt();
        this.isFengmi = in.readInt();
        this.fengmiId = in.readLong();
        this.orderIndex = in.readInt();
        this.playType = in.readInt();
    }

    public static final Creator<RespChannel> CREATOR = new Creator<RespChannel>() {
        @Override
        public RespChannel createFromParcel(Parcel source) {
            return new RespChannel(source);
        }

        @Override
        public RespChannel[] newArray(int size) {
            return new RespChannel[size];
        }
    };
}
