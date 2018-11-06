package com.gsgd.live.data.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ProgramInfo implements Parcelable {

    public String channelName;
    public String showTime;

    public ProgramInfo() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.channelName);
        dest.writeString(this.showTime);
    }

    protected ProgramInfo(Parcel in) {
        this.channelName = in.readString();
        this.showTime = in.readString();
    }

    public static final Creator<ProgramInfo> CREATOR = new Creator<ProgramInfo>() {
        @Override
        public ProgramInfo createFromParcel(Parcel source) {
            return new ProgramInfo(source);
        }

        @Override
        public ProgramInfo[] newArray(int size) {
            return new ProgramInfo[size];
        }
    };
}
