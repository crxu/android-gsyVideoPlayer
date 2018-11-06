package com.gsgd.live.tv;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

/**
 * @author zhangqy
 * @Description
 * @date 2017/7/12
 */
public class TvContentLayout extends RelativeLayout {

    private int position;

    public TvContentLayout(Context context) {
        this(context, null);
    }

    public TvContentLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TvContentLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setClipChildren(false); //是否现限制其他控件在它周围绘制，这里我们要绘制边框，所以选择false
        setClipToPadding(false); //是否限制控件区域在padding里面，与上面的属性一起使用
        setChildrenDrawingOrderEnabled(true);//用于改变控件的绘制顺序，由于可能用到放大的空间，所以这里需要改变一下
        // 获取焦点，重绘item，防止控件放大被挡住的问题，但是问题是绘制频繁，会导致卡顿,不建议用
        // 最好的办法是看哪一个被挡住了，然后让控件重画，这样会好点。
        getViewTreeObserver()
                .addOnGlobalFocusChangeListener(new ViewTreeObserver.OnGlobalFocusChangeListener() {
                    @Override
                    public void onGlobalFocusChanged(View oldFocus, View newFocus) {
                        position = indexOfChild(newFocus);
                        if (position != -1) {
                            bringChildToFront(newFocus);
                            newFocus.postInvalidate();
                        }
                    }
                });
    }

    /**
     * 此函数 dispatchDraw 中调用.
     * 原理就是和最后一个要绘制的view，交换了位置.
     * 因为dispatchDraw最后一个绘制的view是在最上层的.
     * 这样就避免了使用 bringToFront 导致焦点错乱问题.
     */
    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        if (position != -1) {
            if (i == childCount - 1) {
                return position;
            }
            if (i == position)
                return childCount - 1;
        }
        return i;
    }

}
