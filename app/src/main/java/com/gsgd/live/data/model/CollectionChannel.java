package com.gsgd.live.data.model;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

/**
 * 收藏的频道
 */
public class CollectionChannel extends DataSupport {

    @Column(unique = true)
    private long channelId;//频道id
    private String name;//名称
    private String desc;//频道信息(json数据)

    public long getChannelId() {
        return channelId;
    }

    public void setChannelId(long channelId) {
        this.channelId = channelId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

}
