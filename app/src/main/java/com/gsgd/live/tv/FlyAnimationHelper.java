package com.gsgd.live.tv;

import android.graphics.Canvas;
import android.view.View;

public interface FlyAnimationHelper {

    void drawFlyView(Canvas canvas);

    /**
     * 设置焦点组件
     *
     * @param currentView 当前获取焦点组件
     * @param oldView     上一个失去焦点的组件
     * @param scale       缩放比例
     */
    void setFocusView(View currentView, View oldView, float scale);

    void rectFlyAnimation(View currentView, float scaleX, float scaleY);

    FlyFrameView getFlyView();

    void setFlyView(FlyFrameView mFlyView);

    void setTranDurAnimTime(int time);

    void setDrawUpRectEnabled(boolean isDrawUpRect);

    void setMinTop(int minTop);

    void setMaxTop(int maxTop);

    void setIsNeedInit(boolean isNeedInit);
}
