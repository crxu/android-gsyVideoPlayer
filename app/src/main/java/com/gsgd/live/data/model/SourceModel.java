package com.gsgd.live.data.model;

/**
 * @author andy on 2018/1/22
 */

public class SourceModel {

    private boolean isSuccess;
    private String source;

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public SourceModel(boolean isSuccess, String source) {
        this.isSuccess = isSuccess;
        this.source = source;
    }

    @Override
    public String toString() {
        return "SourceModel{" +
                "isSuccess=" + isSuccess +
                ", source='" + source + '\'' +
                '}';
    }
}
