package com.gsgd.live.ui.widgets;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import hdpfans.com.R;
import pl.droidsonroids.gif.GifImageView;

/**
 * 播放状态组件
 */
public class PlayStatusView extends LinearLayout {

    Context mContext;
    @BindView(R.id.tv_tip)
    TextView mTvTip;
    @BindView(R.id.iv_play_gif)
    GifImageView mIvPlayStatus;

    public PlayStatusView(Context context) {
        this(context, null);
    }

    public PlayStatusView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlayStatusView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        View root = LayoutInflater.from(context).inflate(R.layout.view_play_status, this, true);
        ButterKnife.bind(this, root);
    }

}
