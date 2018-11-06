package com.gsgd.live.ui.widgets;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.AttributeSet;

import com.jiongbull.jlog.JLog;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

import hdpfans.com.R;
import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * 视频播放组件(没有交互逻辑)
 */
public class CustomVideoView extends StandardGSYVideoPlayer {

    public CustomVideoView(Context context) {
        this(context, null);
    }

    public CustomVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public int getLayoutId() {
        return R.layout.empty_control_video;
    }

    @Override
    protected void touchSurfaceMoveFullLogic(float absDeltaX, float absDeltaY) {
        super.touchSurfaceMoveFullLogic(absDeltaX, absDeltaY);
        //不给触摸快进，如果需要，屏蔽下方代码即可
        mChangePosition = false;

        //不给触摸音量，如果需要，屏蔽下方代码即可
        mChangeVolume = false;

        //不给触摸亮度，如果需要，屏蔽下方代码即可
        mBrightness = false;
    }

    @Override
    protected void touchDoubleUp() {
        //super.touchDoubleUp();
        //不需要双击暂停
    }

    /**
     * 必须继承，根据你的状态实现不同的逻辑效果
     */
    @Override
    protected void setStateAndUi(int state) {
        mCurrentState = state;
        switch (mCurrentState) {
            case CURRENT_STATE_NORMAL://播放UI初始化
                JLog.d("*********CURRENT_STATE_NORMAL");
                break;

            case CURRENT_STATE_PREPAREING://播放loading
                JLog.d("*********CURRENT_STATE_PREPAREING");
                if (null != mGsyPlayListener) {
                    mGsyPlayListener.onStartBuffer();
                }
                break;

            case CURRENT_STATE_PLAYING://播放ing
                JLog.d("*********CURRENT_STATE_PLAYING");
                if (null != mGsyPlayListener) {
                    mGsyPlayListener.onEndBuffer();
                }
                break;

            case CURRENT_STATE_PAUSE://播放暂停
                JLog.d("*********CURRENT_STATE_PAUSE");
                if (null != mGsyPlayListener) {
                    mGsyPlayListener.onPause();
                }
                break;

            case CURRENT_STATE_ERROR://播放错误
                JLog.d("*********CURRENT_STATE_ERROR");
                if (null != mGsyPlayListener) {
                    mGsyPlayListener.onError();
                }
                break;

            case CURRENT_STATE_AUTO_COMPLETE://播放完成
                JLog.d("*********CURRENT_STATE_AUTO_COMPLETE");
                if (null != mGsyPlayListener) {
                    mGsyPlayListener.onComplete();
                }
                break;

            case CURRENT_STATE_PLAYING_BUFFERING_START://buffering
                JLog.d("*********CURRENT_STATE_PLAYING_BUFFERING_START");
                if (null != mGsyPlayListener) {
                    mGsyPlayListener.onStartBuffer();
                }
                break;
        }
    }

    @Override
    public void onPrepared() {
        super.onPrepared();
        if (null != mGsyPlayListener) {
            mGsyPlayListener.onPrepared();
        }
    }

    @Override
    public void onSeekComplete() {
        super.onSeekComplete();
        JLog.d("***********onSeekComplete");
    }

    private GsyPlayListener mGsyPlayListener;

    public void setGsyPlayListener(GsyPlayListener gsyPlayListener) {
        this.mGsyPlayListener = gsyPlayListener;
    }

    public interface GsyPlayListener {

        void onPrepared();

        void onStartBuffer();

        void onEndBuffer();

        void onError();

        void onPause();

        void onComplete();
    }

}
