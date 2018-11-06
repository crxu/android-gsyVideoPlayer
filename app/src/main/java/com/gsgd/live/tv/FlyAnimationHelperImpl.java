package com.gsgd.live.tv;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import com.gsgd.live.MainApplication;
import com.gsgd.live.utils.MeasureUtil;
import com.jiongbull.jlog.JLog;

public class FlyAnimationHelperImpl implements FlyAnimationHelper {

    private static final String TAG = FlyAnimationHelperImpl.class.getSimpleName();

    private static int DEFAULT_TRAN_DUR_ANIM = 200;
    private static final float DEFAULT_SCALE = 1.0f;
    private boolean isDrawUpRect = true;
    private boolean isDrawing = false;

    private View mFocusView;
    private FlyFrameView mFlyView;
    private AnimatorSet mCombineAnimatorSet;

    private int minTop;
    private int maxTop;
    private boolean isNeedInit = true;

    @Override
    public void drawFlyView(Canvas canvas) {
        canvas.save();

        if (!isDrawUpRect) {
            onDrawShadow(canvas);
            onDrawUpRect(canvas);
        }

        //绘制焦点子控件
        if (null != mFocusView && (!isDrawUpRect && isDrawing)) {
            onDrawFocusView(canvas);
        }

        if (isDrawUpRect) {
            onDrawShadow(canvas);
            onDrawUpRect(canvas);
        }

        canvas.restore();
    }

    @Override
    public void setFocusView(View currentView, View oldView, float scale) {
        this.mFocusView = currentView;

        int getScale = (int) (scale * 10);
        if (getScale > 10) {
            //有放大
            if (null != currentView) {
                //放大当前焦点组件
                currentView.animate()
                        .scaleX(scale)
                        .scaleY(scale)
                        .setDuration(DEFAULT_TRAN_DUR_ANIM)
                        .start();
            }

            if (null != oldView) {
                //恢复上一个焦点组件的大小
                oldView.animate()
                        .scaleX(DEFAULT_SCALE)
                        .scaleY(DEFAULT_SCALE)
                        .setDuration(DEFAULT_TRAN_DUR_ANIM)
                        .start();
            }
        }

        //焦点变化时执行动画
        rectFlyAnimation(currentView, scale, scale);
    }

    @Override
    public void rectFlyAnimation(View currentView, float scaleX, float scaleY) {
        Rect fromRect = findLocationWithView(getFlyView());
        Rect toRect = findLocationWithView(currentView);
        JLog.d(TAG, "********->toRect:" + toRect + "||fromRect:" + fromRect);

        if (toRect.top > -10 && toRect.top < MeasureUtil.getScreenSize(MainApplication.getContext()).y) {
            int disX = toRect.left - fromRect.left;

            int top = toRect.top;
            if (top < minTop) {
                top = minTop;
            }

            JLog.d(TAG, "********->top:" + top + "||minTop:" + minTop + "||maxTop:" + maxTop);

            if (top > maxTop) {
                top = maxTop;
            }

            int disY = top - fromRect.top;

            JLog.d(TAG, "********->disX:" + disX + "||disY:" + disY);
            rectFlyMainLogic(currentView, disX, disY, scaleX, scaleY);
        }
    }

    /**
     * 获取相对于根组件的位置
     */
    private Rect findLocationWithView(View view) {
        ViewGroup root = (ViewGroup) getFlyView().getParent();
        Rect rect = new Rect();
        root.offsetDescendantRectToMyCoords(view, rect);
        return rect;
    }

    /**
     * 执行飞框动画
     *
     * @param focusView 当前view
     * @param x         x轴移动的距离
     * @param y         y轴移动的距离
     * @param scaleX    x轴缩放
     * @param scaleY    y轴缩放
     */
    private void rectFlyMainLogic(final View focusView, float x, float y, float scaleX, float scaleY) {
        int newWidth = 0;
        int newHeight = 0;

        if (null != focusView) {
            newWidth = (int) (focusView.getMeasuredWidth() * scaleX);
            newHeight = (int) (focusView.getMeasuredHeight() * scaleY);
            //修正缩放后的移动距离
            x += (focusView.getMeasuredWidth() - newWidth) / 2;
            y += (focusView.getMeasuredHeight() - newHeight) / 2;
        }

        int oldWidth = getFlyView().getMeasuredWidth();
        int oldHeight = getFlyView().getMeasuredHeight();

        JLog.d(TAG, "********->newWidth:" + newWidth
                + "||newHeight:" + newHeight
                + "||oldWidth:" + oldWidth
                + "||oldHeight:" + oldHeight);

        //取消之前的动画
        if (null != mCombineAnimatorSet) {
            if (mCombineAnimatorSet.isRunning()) {
                mCombineAnimatorSet.end();
            }
            mCombineAnimatorSet.cancel();
        }

        if (!isNeedInit && oldWidth == 0 && oldHeight == 0) {
            getFlyView().setTranslationX(x);
            getFlyView().setTranslationY(y);
            ViewGroup.LayoutParams params = getFlyView().getLayoutParams();
            params.width = newWidth;
            params.height = newHeight;
            getFlyView().setLayoutParams(params);
            getFlyView().setVisibility(View.VISIBLE);

        } else {
            getFlyView().setVisibility(View.VISIBLE);
            ObjectAnimator transAnimatorX = ObjectAnimator.ofFloat(getFlyView(), "translationX", x);
            ObjectAnimator transAnimatorY = ObjectAnimator.ofFloat(getFlyView(), "translationY", y);
            ObjectAnimator scaleXAnimator = ObjectAnimator.ofInt(new ScaleTool(getFlyView()), "width", oldWidth, newWidth);
            ObjectAnimator scaleYAnimator = ObjectAnimator.ofInt(new ScaleTool(getFlyView()), "height", oldHeight, newHeight);

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(transAnimatorX, transAnimatorY, scaleXAnimator, scaleYAnimator);
            animatorSet.setInterpolator(new DecelerateInterpolator(1));
            animatorSet.setDuration(DEFAULT_TRAN_DUR_ANIM);
            final int finalNewWidth = newWidth;
            final int finalNewHeight = newHeight;
            animatorSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    if (!isDrawUpRect) {
                        isDrawing = false;
                    }
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                    if (!isDrawUpRect) {
                        isDrawing = false;
                    }
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (!isDrawUpRect) {
                        isDrawing = true;
                    }

                    JLog.d(TAG, "**********->onAnimationEnd:" + getFlyView().getWidth() + "||" + getFlyView().getHeight());

                    if (getFlyView().getWidth() != finalNewWidth) {
                        JLog.d(TAG, "**********->onAnimationEnd fix");
                        ViewGroup.LayoutParams params = getFlyView().getLayoutParams();
                        params.width = finalNewWidth;
                        params.height = finalNewHeight;
                        getFlyView().setLayoutParams(params);
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    if (!isDrawUpRect) {
                        isDrawing = false;
                    }
                }
            });
            animatorSet.start();

            this.mCombineAnimatorSet = animatorSet;
        }
    }

    /**
     * 绘制外部阴影
     */
    private void onDrawShadow(Canvas canvas) {
        Drawable drawableShadow = getFlyView().getShadowDrawable();
        if (drawableShadow != null) {
            RectF shadowPaddingRect = getFlyView().getDrawShadowRect();
            int width = getFlyView().getWidth();
            int height = getFlyView().getHeight();
            Rect padding = new Rect();
            drawableShadow.getPadding(padding);
            drawableShadow.setBounds(
                    (int) (-padding.left + (shadowPaddingRect.left)),
                    (int) (-padding.top + (shadowPaddingRect.top)),
                    (int) (width + padding.right - (shadowPaddingRect.right)),
                    (int) (height + padding.bottom - (shadowPaddingRect.bottom))
            );
            drawableShadow.draw(canvas);
        }
    }

    /**
     * 绘制最上层的移动边框
     */
    private void onDrawUpRect(Canvas canvas) {
        Drawable drawableUp = getFlyView().getUpRectDrawable();
        if (drawableUp != null) {
            RectF paddingRect = getFlyView().getDrawUpRect();
            int width = getFlyView().getWidth();
            int height = getFlyView().getHeight();
            Rect padding = new Rect();
            drawableUp.getPadding(padding);
            drawableUp.setBounds(
                    (int) (-padding.left + (paddingRect.left)),
                    (int) (-padding.top + (paddingRect.top)),
                    (int) (width + padding.right - (paddingRect.right)),
                    (int) (height + padding.bottom - (paddingRect.bottom))
            );
            drawableUp.draw(canvas);
        }
    }

    private void onDrawFocusView(Canvas canvas) {
        View view = mFocusView;
        canvas.save();
        float scaleX = (float) (getFlyView().getWidth()) / (float) view.getWidth();
        float scaleY = (float) (getFlyView().getHeight()) / (float) view.getHeight();
        canvas.scale(scaleX, scaleY);
        view.draw(canvas);
        canvas.restore();
    }

    @Override
    public FlyFrameView getFlyView() {
        return this.mFlyView;
    }

    @Override
    public void setFlyView(FlyFrameView mFlyView) {
        this.mFlyView = mFlyView;
    }

    @Override
    public void setTranDurAnimTime(int time) {
        DEFAULT_TRAN_DUR_ANIM = time;
    }

    @Override
    public void setDrawUpRectEnabled(boolean isDrawUpRect) {
        this.isDrawUpRect = isDrawUpRect;
        getFlyView().postInvalidate();
    }

    @Override
    public void setMinTop(int minTop) {
        this.minTop = minTop;
    }

    @Override
    public void setMaxTop(int maxTop) {
        this.maxTop = maxTop;
    }

    @Override
    public void setIsNeedInit(boolean isNeedInit) {
        this.isNeedInit = isNeedInit;
    }

}
