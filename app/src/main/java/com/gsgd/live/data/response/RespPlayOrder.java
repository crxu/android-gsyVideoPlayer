package com.gsgd.live.data.response;

/**
 * Created by andy on 2017/12/25.
 */

public class RespPlayOrder{

    private String date;
    private int isFengmi;// 0 不是 1是
    private String showTime;
    private int orderIndex;
    private String channelName;
    private long fengmiId;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getIsFengmi() {
        return isFengmi;
    }

    public void setIsFengmi(int isFengmi) {
        this.isFengmi = isFengmi;
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
}
