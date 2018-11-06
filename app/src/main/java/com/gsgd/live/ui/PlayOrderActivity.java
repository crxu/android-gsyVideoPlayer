package com.gsgd.live.ui;

import android.content.Intent;
import android.text.TextPaint;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.gsgd.live.AppConfig;
import com.gsgd.live.data.model.Order;
import com.gsgd.live.ui.base.BaseActivity;
import com.gsgd.live.utils.PlayOrdersMananger;
import com.jiongbull.jlog.JLog;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import hdpfans.com.R;

/**
 * Created by andy on 2017/12/26.
 * 显示立即观看和暂时不看的界面
 */
public class PlayOrderActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.fl)
    FrameLayout mFl;
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.btn_look)
    Button mBtnLook;
    @BindView(R.id.btn_not_look)
    Button mBtnNotLook;
    private Order mOrder;
    private static WeakReference<PlayOrderActivity> sPlayOrderActivity;

    /**
     * 关闭预约提示界面
     */
    public static void dismiss() {
        if (sPlayOrderActivity != null) {
            PlayOrderActivity playOrderActivity = sPlayOrderActivity.get();
            if (playOrderActivity != null) {
                try {
                    PlayOrdersMananger.deleteOrder(playOrderActivity, playOrderActivity.mOrder.getKey());
                    PlayOrdersMananger.startClock(playOrderActivity);
                    playOrderActivity.finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_play_order;
    }

    @Override
    protected void initParams() {
        super.initParams();
        sPlayOrderActivity = new WeakReference<PlayOrderActivity>(this);
        mOrder = PlayOrdersMananger.firstOrder(mContext);
        if (mOrder == null) {//如果
            PlayOrdersMananger.startClock(mContext);
            finish();
            return;
        }
        mTvTitle.setText(String.format("  %s  %s", mOrder.getType(), mOrder.getChannelName()));
        mBtnLook.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                setBtnStatue(mBtnLook, hasFocus);
            }
        });
        mBtnNotLook.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                setBtnStatue(mBtnNotLook, hasFocus);
            }
        });
        mBtnLook.setOnClickListener(this);
        mBtnNotLook.setOnClickListener(this);
    }

    private void setBtnStatue(Button btn, boolean hasFocus) {
        int color = mContext.getResources().getColor(hasFocus ? R.color.color_ffffff : R.color.color_f0f0f0);
        if (hasFocus) {
            TextPaint paint = btn.getPaint();
            paint.setFakeBoldText(true);
            btn.setTextColor(color);
            btn.setBackgroundResource(R.drawable.item_order_play_select_bg);
        } else {
            btn.setBackgroundResource(R.drawable.item_order_play_normal_bg);
            TextPaint paint = btn.getPaint();
            btn.setTextColor(color);
            paint.setFakeBoldText(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sPlayOrderActivity != null) {
            sPlayOrderActivity.clear();
        }
        sPlayOrderActivity = null;
    }

    @Override
    public void onClick(View v) {
        if (v == mBtnLook) {
            PlayOrdersMananger.deleteOrder(mContext, mOrder.getKey());
            Intent intent = new Intent(mContext, VideoPlayerActivity.class);
            intent.putExtra(AppConfig.TV_RESULT, mOrder.getChannelType());
            startActivity(intent);
            finish();
        } else if (v == mBtnNotLook) {
            PlayOrdersMananger.deleteOrder(mContext, mOrder.getKey());
            PlayOrdersMananger.startClock(mContext);
            finish();
        }
    }
}
