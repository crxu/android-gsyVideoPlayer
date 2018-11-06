package com.gsgd.live.tv;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

/**
 * 飞框组件
 */
public class FlyFrameView extends FrameLayout {

    private static final String TAG = FlyFrameView.class.getSimpleName();

    private Context mContext;
    private Drawable mRectUpDrawable;
    private Drawable mRectUpShade;
    private FlyAnimationHelper mFlyAnimationHelper;

    private RectF mShadowPaddingRect = new RectF();
    private RectF mUpPaddingRect = new RectF();

    public FlyFrameView(Context context) {
        this(context, null);
    }

    public FlyFrameView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlyFrameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;

        setWillNotDraw(false);
        mFlyAnimationHelper = new FlyAnimationHelperImpl();
        mFlyAnimationHelper.setFlyView(this);
    }

    public void setFocusView(View currentView, View oldView, float scale) {
        mFlyAnimationHelper.setFocusView(currentView, oldView, scale);
    }

    public View getUpView() {
        return this;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mFlyAnimationHelper != null) {
            mFlyAnimationHelper.drawFlyView(canvas);
            return;
        }
        super.onDraw(canvas);
    }

    /**
     * 设置边框
     */
    public void setUpRectResource(int id) {
        this.mRectUpDrawable = mContext.getResources().getDrawable(id);
        postInvalidate();
    }

    /**
     * 设置阴影
     */
    public void setUpRectShadeResource(int id) {
        this.mRectUpShade = mContext.getResources().getDrawable(id);
        postInvalidate();
    }

    public Drawable getShadowDrawable() {
        return this.mRectUpShade;
    }

    public Drawable getUpRectDrawable() {
        return this.mRectUpDrawable;
    }

    public RectF getDrawShadowRect() {
        return this.mShadowPaddingRect;
    }

    public RectF getDrawUpRect() {
        return this.mUpPaddingRect;
    }

    public void setUpPaddingRect(RectF upPaddingRect) {
        this.mUpPaddingRect = upPaddingRect;
    }

    public void setShadowPaddingRect(RectF shadowPaddingRect) {
        this.mShadowPaddingRect = shadowPaddingRect;
    }

    public void setTranDurAnimTime(int defaultTranDurAnim) {
        this.mFlyAnimationHelper.setTranDurAnimTime(defaultTranDurAnim);
    }

    public void setDrawUpRectEnabled(boolean isDrawUpRect) {
        this.mFlyAnimationHelper.setDrawUpRectEnabled(isDrawUpRect);
    }

    public void setMinTop(int minTop) {
        this.mFlyAnimationHelper.setMinTop(minTop);
    }

    public void setMaxTop(int maxTop) {
        this.mFlyAnimationHelper.setMaxTop(maxTop);
    }

    public void setIsNeedInit(boolean isNeedInit) {
        this.mFlyAnimationHelper.setIsNeedInit(isNeedInit);
    }

}