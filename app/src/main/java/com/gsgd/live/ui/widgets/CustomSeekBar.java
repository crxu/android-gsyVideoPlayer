package com.gsgd.live.ui.widgets;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.jiongbull.jlog.JLog;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import hdpfans.com.R;

/**
 * @author zhangqy
 * @Description 自定义进度条
 * @date 2017/11/7
 */
public class CustomSeekBar extends LinearLayout {

    private static final String TAG = CustomSeekBar.class.getSimpleName();

    @BindView(R.id.sb_progress)
    SeekBar mSbProgress;
    @BindView(R.id.tv_play_time)
    TextView mTvPlayTime;
    @BindView(R.id.tv_current_time)
    TextView mTvCurTime;
    @BindView(R.id.tv_total_time)
    TextView mTvTotalTime;

    private int mWidth = 0;
    private FrameLayout.LayoutParams params;
    private boolean isCanDrag = false;//是否能拖拽
    private int totalTime = 0;//总时长 单位：ms
    private int currentTime = 0;//当前时长 单位：ms

    public CustomSeekBar(Context context) {
        this(context, null);
    }

    public CustomSeekBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        View root = LayoutInflater.from(context).inflate(R.layout.view_custom_seekbar, this, true);
        ButterKnife.bind(this, root);

        params = (FrameLayout.LayoutParams) mTvCurTime.getLayoutParams();

        mSbProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //设置mTvCurTime位置
                if (0 == mWidth) {
                    mWidth = mSbProgress.getWidth();
                }
                params.leftMargin = (int) ((mWidth - 50) * (progress * 1.0f / totalTime)) + 142;
                mTvCurTime.setLayoutParams(params);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        resetUI();
    }

    /**
     * 设置总时长
     *
     * @param time ms
     */
    public void setTotalTime(int time) {
        JLog.d(TAG, "**************setTotalTime:" + time);

        isCanDrag = true;
        totalTime = time;
        mTvTotalTime.setText(formatTime(time));
        mSbProgress.setMax(time);
    }

    public void setTime(int time) {
        this.currentTime = time;
    }

    /**
     * 设置当前时长位置
     */
    public void setCurrentTime(int time) {
        if (totalTime > 0) {
            setTime(time);
            setProgress(time);
        }
    }

    private void setProgress(int progress) {
        if (isCanDrag) {
            mSbProgress.setProgress(progress);
            mTvCurTime.setText(formatTime(progress));//设置时间
            mTvPlayTime.setText(formatTime(progress));//设置时间
        }
    }

    public int getTotalTime() {
        return totalTime;
    }

    public int getProgress() {
        return currentTime;
    }

    /**
     * 格式化时间
     */
    private String formatTime(int time) {
        int totalSeconds = time / 1000;
        int hours = totalSeconds / (60 * 60);
        int minutes = (totalSeconds - hours * 3600) / 60;
        int seconds = totalSeconds - hours * 3600 - minutes * 60;

        return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds);
    }

    /**
     * 重置UI
     */
    public void resetUI() {
        isCanDrag = false;
        totalTime = 0;
        currentTime = 0;
        mSbProgress.setMax(100);
        mSbProgress.setProgress(0);
        mTvTotalTime.setText("00:00:00");
        mTvCurTime.setText("00:00:00");
        mTvPlayTime.setText("00:00:00");
    }

}
