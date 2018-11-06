package com.gsgd.live.data.model;

import org.litepal.crud.DataSupport;

/**
 * 自定义频道
 */
public class CustomChannel extends DataSupport {

    private String name;//名称
    private String source;//源地址

    public long getId() {
        return getBaseObjId();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
