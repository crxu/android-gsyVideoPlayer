package com.gsgd.live.data.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.gsgd.live.data.model.ProgramInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangqy
 * @Description 节目信息
 * @date 2017/10/11
 */
public class RespPlayBill implements Parcelable {

    @SerializedName(value = "tommorrowProgram")
    public List<ProgramInfo> tomorrowProgram = new ArrayList<>();
    public List<ProgramInfo> todayProgram = new ArrayList<>();

    public RespPlayBill() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.tomorrowProgram);
        dest.writeTypedList(this.todayProgram);
    }

    protected RespPlayBill(Parcel in) {
        this.tomorrowProgram = in.createTypedArrayList(ProgramInfo.CREATOR);
        this.todayProgram = in.createTypedArrayList(ProgramInfo.CREATOR);
    }

    public static final Creator<RespPlayBill> CREATOR = new Creator<RespPlayBill>() {
        @Override
        public RespPlayBill createFromParcel(Parcel source) {
            return new RespPlayBill(source);
        }

        @Override
        public RespPlayBill[] newArray(int size) {
            return new RespPlayBill[size];
        }
    };
}
