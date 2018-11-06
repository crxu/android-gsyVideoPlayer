package com.gsgd.live.ui.leftMenu;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.github.florent37.viewanimator.ViewAnimator;
import com.gsgd.live.data.model.PlayOrder;
import com.gsgd.live.utils.DateUtils;
import com.gsgd.live.utils.PlayOrdersMananger;

import butterknife.BindView;
import butterknife.ButterKnife;
import hdpfans.com.R;

/**
 * Created by andy on 2017/12/25.
 * @author Andy
 * 预约点播的界面
 */
public class MenuOrderContentView extends FrameLayout {

    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.tv_time2)
    TextView mTvTime;
    @BindView(R.id.btn_play)
    Button mBtnPlay;
    @BindView(R.id.line)
    View mline;
    @BindView(R.id.btn_order)
    Button mBtnOrder;
    private int mPosition = -1;
    private boolean isOrder;
    private PlayOrder mOrder;
    private OnPlayClickListener mOnPlayClickListener;
    private MenuTLContentView.OnFocusGetListener mOnFocusGetListener;

    public MenuOrderContentView(@NonNull Context context) {
        this(context, null);
    }

    public MenuOrderContentView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.view_order_play_layout, this);
        ButterKnife.bind(this, this);
        initView();
    }

    public void setOnFocusGetListener(MenuTLContentView.OnFocusGetListener listener) {
        this.mOnFocusGetListener = listener;
    }

    private void initView() {
        mBtnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
                if (mOnPlayClickListener != null) {
                    mOnPlayClickListener.onPlay(mPosition);
                }
            }
        });
        mBtnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOrder) {
                    PlayOrdersMananger.cancelOrder(getContext(), mOrder);
                } else {
                    PlayOrdersMananger.order(getContext(), mOrder);
                }
                isOrder = !isOrder;
                if (mOnPlayClickListener != null) {
                    mOnPlayClickListener.onOrder(isOrder);
                }
                hide();
            }
        });
        mBtnPlay.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                setBtnStatue(hasFocus, mBtnPlay);
            }
        });
        mBtnOrder.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                setBtnStatue(hasFocus, mBtnOrder);
            }
        });
    }


    private void setBtnStatue(boolean hasFocus, Button view) {
        if (hasFocus) {
            view.setBackgroundResource(R.drawable.item_order_play_select_bg);
        } else {
            view.setBackgroundResource(R.drawable.item_order_play_normal_bg);
        }
        TextPaint paint = view.getPaint();
        paint.setFakeBoldText(hasFocus);
        int color = getContext().getResources().getColor(hasFocus ? R.color.color_ffffff : R.color.color_f0f0f0);
        paint.setColor(color);
    }

    public void show(PlayOrder order, int position, OnPlayClickListener listener) {
        this.isOrder = PlayOrdersMananger.isOrder(getContext(), order);
        mTvTitle.setText(String.format("  %s  %s", order.getType(), order.getChannelName()));
        mTvTime.setText(String.format("将于%s%s播放", DateUtils.getDay(order.getTime()), order.getShowTime()));
        mBtnPlay.setVisibility(order.isFengmi() ? View.VISIBLE : View.GONE);
        if (order.getTime() > System.currentTimeMillis()) {
            mBtnOrder.setVisibility(View.VISIBLE);
            mBtnOrder.setText(isOrder ? R.string.cancel_order : R.string.enter_order);
        } else {
            mBtnOrder.setVisibility(View.GONE);
        }
        if (mBtnPlay.getVisibility() == VISIBLE && mBtnOrder.getVisibility() == VISIBLE) {
            mline.setVisibility(VISIBLE);
        } else {
            mline.setVisibility(GONE);
        }

        if (mBtnPlay.getVisibility() == VISIBLE) {
            mBtnPlay.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBtnPlay.requestFocus();
                }
            }, 5);
        } else if (mBtnOrder.getVisibility() == VISIBLE) {
            mBtnOrder.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBtnOrder.requestFocus();
                }
            }, 5);
        }
        setVisibility(VISIBLE);
        this.mPosition = position;
        this.mOrder = order;
        this.mOnPlayClickListener = listener;
    }

    public void hide() {
        setVisibility(GONE);
        if (mOnFocusGetListener != null) {
            mOnFocusGetListener.onIndex(mPosition, isOrder);
        }
    }

    public boolean handleOnKeyDown(int keyCode) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            hide();
            return true;
        } else {
            if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN || keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                if (mBtnOrder.getVisibility() == VISIBLE && mBtnPlay.getVisibility() == VISIBLE) {
                    if (mBtnPlay.hasFocus() && keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                        mBtnOrder.requestFocus();
                    } else if (mBtnOrder.hasFocus() && keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                        mBtnPlay.requestFocus();
                    }
                }
            }
        }
        return false;
    }

    public interface OnPlayClickListener {

        /**
         * 点击点播按钮时候出发
         *
         * @param position 点播的index
         */
        void onPlay(int position);

        /**
         * 预约或者取消预约后调用
         *
         * @param isOrder true 预约 false 没有预约
         */
        void onOrder(boolean isOrder);
    }

}
