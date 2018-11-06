package com.gsgd.live.ui.widgets;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.text.format.DateFormat;
import android.util.AttributeSet;

/**
 * @author zhangqy
 * @Description 时间显示
 * @date 2017/12/29
 */
public class TvTimeView extends AppCompatTextView {

    private static final int WHAT_SYS_TIME = 999;//系统时间
    private static final int TIME_INTERVAL = 1000;//刷新间隔

    public TvTimeView(@NonNull Context context) {
        this(context, null);
    }

    public TvTimeView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void startUpdate() {
        stopUpdate();
        mHandler.sendEmptyMessage(WHAT_SYS_TIME);
    }

    public void stopUpdate() {
        mHandler.removeCallbacksAndMessages(null);
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            updateDateTime();
            return true;
        }
    });

    private void updateDateTime() {
        setText(DateFormat.format("HH:mm:ss", System.currentTimeMillis()));
        mHandler.sendEmptyMessageDelayed(WHAT_SYS_TIME, TIME_INTERVAL);
    }

}
