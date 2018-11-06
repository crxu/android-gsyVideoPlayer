package com.gsgd.live.ui.widgets;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 *
 */
public class CustomLayoutManager extends LinearLayoutManager {

    private double speedRatio = 0.2;

    public CustomLayoutManager(Context context) {
        super(context);
    }

    public CustomLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public CustomLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        return super.scrollVerticallyBy((int) (speedRatio * dy), recycler, state);//屏蔽之后无滑动效果，证明滑动的效果就是由这个函数实现
    }

    public void setSpeedRatio(double speedRatio) {
        this.speedRatio = speedRatio;
    }

}
