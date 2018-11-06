package com.gsgd.live.ui.widgets;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import hdpfans.com.R;

/**
 * @author zhangqy
 * @Description 电视节目信息
 * @date 2017/12/29
 */
public class TvShowView extends RelativeLayout {

    @BindView(R.id.tv_show_name)
    TextView mTvName;
    @BindView(R.id.tv_now_name)
    MarqueeTextView mTvNowName;
    @BindView(R.id.tv_now_time)
    TextView mTvNowTime;
    @BindView(R.id.tv_next_name)
    MarqueeTextView mTvNextName;
    @BindView(R.id.tv_next_time)
    TextView mTvNextTime;

    private Context mContext;

    public TvShowView(Context context) {
        this(context, null);
    }

    public TvShowView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.mContext = context;
        View root = LayoutInflater.from(context).inflate(R.layout.view_tv_show, this, true);
        ButterKnife.bind(this, root);
    }

    /**
     * 设置当前节目名
     */
    public void setTvShowName(String name) {
        mTvName.setText(name);
    }

    /**
     * 重置UI
     */
    public void resetUI() {
        mTvNowName.setText("暂无数据");
        mTvNowTime.setText("暂无数据");
        mTvNextName.setText("暂无数据");
        mTvNextTime.setText("暂无数据");

        mTvNowTime.setVisibility(View.GONE);
        mTvNextTime.setVisibility(View.GONE);
    }

    /**
     * 设置节目信息
     *
     * @param nowName  当前节目名
     * @param nowTime  节目开始时间
     * @param nextName 下个节目名称
     * @param nextTime 下个节目开始时间
     */
    public void setTvShowContent(String nowName, String nowTime, String nextName, String nextTime) {
        if (!TextUtils.isEmpty(nowName) && !TextUtils.isEmpty(nowTime)) {
            mTvNowName.setText(nowName);
            mTvNowTime.setText(nowTime);
            mTvNowTime.setVisibility(View.VISIBLE);

        } else {
            mTvNowName.setText("暂无数据");
            mTvNowTime.setText("暂无数据");
            mTvNowTime.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(nextName) && !TextUtils.isEmpty(nextTime)) {
            mTvNextName.setText(nextName);
            mTvNextTime.setText(nextTime);
            mTvNextTime.setVisibility(View.VISIBLE);

        } else {
            mTvNextName.setText("暂无数据");
            mTvNextTime.setText("暂无数据");
            mTvNextTime.setVisibility(View.GONE);
        }
    }

}
