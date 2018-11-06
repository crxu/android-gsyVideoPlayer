package com.gsgd.live.data.model;

import android.content.Context;
import android.support.annotation.NonNull;

import com.gsgd.live.data.response.RespPlayOrder;
import com.gsgd.live.utils.DateUtils;
import com.gsgd.live.utils.PlayOrdersMananger;

/**
 * Created by andy on 2017/12/25.
 */

public class PlayOrder {

    private String date;
    private boolean isFengmi;
    private String showTime;
    private int orderIndex;
    private String channelName;
    private long fengmiId;
    private long time;
    //额外字段
    private String channelType;//频道名称
    private long channelId;//频道id
    private String type;//频道类型

    private String key;
    private int isOrder = -1;

    public boolean isOrder(Context context) {
        if (isOrder == -1) {
            isOrder = PlayOrdersMananger.isOrder(context, this) ? 1 : 0;
        }
        return isOrder == 1;
    }

    public void setOrder(int order) {
        isOrder = order;
    }

    public String getKey() {
        return key;
    }

    public void setKey() {
        this.key = String.format("%s#%s#%s", channelId, date, showTime);
    }

    public PlayOrder(RespPlayOrder respPlayOrder) {
        this.date = respPlayOrder.getDate();
        this.isFengmi = respPlayOrder.getIsFengmi() == 1;
        this.showTime = respPlayOrder.getShowTime();
        this.orderIndex = respPlayOrder.getOrderIndex();
        this.channelName = respPlayOrder.getChannelName();
        this.fengmiId = respPlayOrder.getFengmiId();
        this.time = DateUtils.parse(this.date + " " + this.showTime);
    }

    public PlayOrder() {
    }

    public String getType() {
        return type == null ? "" : type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getChannelType() {
        return channelType;
    }

    public void setChannelType(String channelType) {
        this.channelType = channelType;
    }

    public long getChannelId() {
        return channelId;
    }

    public void setChannelId(long channelId) {
        this.channelId = channelId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isFengmi() {
        return isFengmi;
    }

    public void setFengmi(boolean fengmi) {
        isFengmi = fengmi;
    }

    public String getShowTime() {
        return showTime;
    }

    public void setShowTime(String showTime) {
        this.showTime = showTime;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public long getFengmiId() {
        return fengmiId;
    }

    public void setFengmiId(long fengmiId) {
        this.fengmiId = fengmiId;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "PlayOrder{" +
                "date='" + date + '\'' +
                ", isFengmi=" + isFengmi +
                ", showTime='" + showTime + '\'' +
                ", orderIndex=" + orderIndex +
                ", channelName='" + channelName + '\'' +
                ", fengmiId=" + fengmiId +
                ", time=" + time +
                ", channelType='" + channelType + '\'' +
                ", channelId=" + channelId +
                ", type='" + type + '\'' +
                '}';
    }
}
