package com.gsgd.live.tv;

import android.view.View;
import android.view.ViewGroup;

public class ScaleTool {

    private View view;
    private int width;
    private int height;

    private ViewGroup.LayoutParams params;

    public ScaleTool(View view) {
        this.view = view;
        this.params = view.getLayoutParams();
    }

    public int getWidth() {
        return this.width;
    }

    public void setWidth(int width) {
        this.width = width;
        params.width = width;
        view.setLayoutParams(params);
    }

    public int getHeight() {
        return this.height;
    }

    public void setHeight(int height) {
        this.height = height;
        params.height = height;
        view.setLayoutParams(params);
    }

}
