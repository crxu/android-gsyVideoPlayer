package com.gsgd.live.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.gsgd.live.AppConfig;
import com.gsgd.live.MainApplication;
import com.gsgd.live.data.api.ApiModule;
import com.gsgd.live.data.events.PlayEvent;
import com.gsgd.live.data.listener.ChannelListener;
import com.gsgd.live.data.listener.OnGetCustomTypeListener;
import com.gsgd.live.data.model.Channel;
import com.gsgd.live.data.model.ChannelType;
import com.gsgd.live.data.model.CustomInfo;
import com.gsgd.live.data.model.CustomTypeInfo;
import com.gsgd.live.data.model.ProgramInfo;
import com.gsgd.live.data.model.SourceModel;
import com.gsgd.live.data.response.RespPlayBill;
import com.gsgd.live.data.response.RespSource;
import com.gsgd.live.ui.base.BaseActivity;
import com.gsgd.live.ui.base.BaseDialog;
import com.gsgd.live.ui.dialog.AddSourceDialog;
import com.gsgd.live.ui.dialog.DeleteSourceDialog;
import com.gsgd.live.ui.dialog.HintDialog;
import com.gsgd.live.ui.dialog.SelectMatchTvDialog;
import com.gsgd.live.ui.dialog.SelectSourceDialog;
import com.gsgd.live.ui.dialog.SelectTvDialog;
import com.gsgd.live.ui.leftMenu.MenuType;
import com.gsgd.live.ui.widgets.CustomVideoView;
import com.gsgd.live.ui.widgets.PlayStatusView;
import com.gsgd.live.ui.widgets.TvShowView;
import com.gsgd.live.ui.widgets.TvTimeView;
import com.gsgd.live.utils.ACache;
import com.gsgd.live.utils.ContentUtils;
import com.gsgd.live.utils.DeviceUtils;
import com.gsgd.live.utils.PlayControlUtil;
import com.gsgd.live.utils.PlayOrdersMananger;
import com.gsgd.live.utils.ProgramListUtils;
import com.gsgd.live.utils.RouterUtils;
import com.gsgd.live.utils.SP2Util;
import com.gsgd.live.utils.SPUtil;
import com.gsgd.live.utils.ToastUtil;
import com.gsgd.live.utils.Utils;
import com.jiongbull.jlog.JLog;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.utils.GSYVideoType;
import com.umeng.analytics.MobclickAgent;
import com.yidu.CheckUpdateReceiver;
import com.yidu.UpdateManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import cn.jpush.android.api.JPushInterface;
import hdpfans.com.R;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.plugins.RxJavaPlugins;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * 视频播放界面
 */
public class VideoPlayerActivity extends BaseActivity {

    private static final String TAG = VideoPlayerActivity.class.getSimpleName();

    private static final int WHAT_TIME_OUT = 111;//超时
    private static final int WHAT_CHANGE_CHANNEL = 106;//自动换台
    private static final int WHAT_HANDLE_DATA_SUCCESS = 107;//处理正确数据
    private static final int WHAT_HANDLE_DATA_ERROR = 108;//处理错误数据
    private static final int HANDLE_LONG_TIME_LOOK = 200;//播放超过五小时
    private static final int HANDLE_CHECK_UPDATE = 300;//检测更新
    private static final int WHAT_TIME_CHECK_BLACK = 666;//检测是否黑屏

    @BindView(R.id.iv_bg_cover)
    View mIvBgCover;
    @BindView(R.id.gsy_video_view)
    CustomVideoView mGsyVideoView;
    @BindView(R.id.status_view)
    PlayStatusView mStatusView;
    @BindView(R.id.tv_address)
    TextView mTvAddress;
    @BindView(R.id.view_tv_show)
    TvShowView mViewNotice;
    @BindView(R.id.tv_time)
    TvTimeView mTvTime;
    @BindView(R.id.iv_logo)
    ImageView mIvLogo;
    @BindView(R.id.fl_bg_error)
    FrameLayout mViewBgError;

    private String mTvMatchStr;//获取匹配的频道,外部传来的数据，用于匹配台
    private String mTvMatchId;//获取匹配的频道,外部传来的数据，用于匹配台
    private ArrayList<ChannelType> mChannelTypes;//频道列表，所有界面的数据源
    private boolean isGetChannelList = false;//是否获取到了频道列表
    private boolean mIsActivityPaused = false;

    //记录当前播放的信息
    private ChannelType mCurrentType;//当前栏目
    private Channel mCurrentChannel;//当前节目
    private Channel mCurrentSubChannel;//当前子节目
    private Channel mLastChannel;//上一个节目
    private RespSource mCurrentSource;//当前播放源

    private boolean isPlayCustom = false;//是否在播放用户自建源
    //记录用户当前播放的自建频道信息
    private CustomTypeInfo mCustomTypeInfo;
    private CustomInfo mCustomInfo;
    private List<CustomTypeInfo> mCustomTypeList = new ArrayList<>();

    private MyReceiver networkReceiver;
    private static final int MAX_ERROR_COUNT = 5;//最大允许重试次数

    private boolean isPreparePlay = false;//是否准备播放
    private int errCount = 0;//当前源播放错误次数
    private long lastTime = -1;//上一次播放时间
    private boolean isClickFengmi = false;//是否点击蜜蜂视频

    private static WeakReference<VideoPlayerActivity> sVideoPlayerActivity;

    public static boolean isShowOrder() {
        if (sVideoPlayerActivity != null) {
            VideoPlayerActivity activity = sVideoPlayerActivity.get();
            if (activity != null && activity.mHintDialog != null && activity.mHintDialog.isShowing()) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_video_player;
    }

    @Override
    protected void initParams() {
        super.initParams();
        sVideoPlayerActivity = new WeakReference<>(this);
        RxJavaPlugins.setErrorHandler(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                JLog.e(TAG, "发生错误......");
                JLog.e(TAG, throwable);
            }
        });

        Intent intent = getIntent();
        mTvMatchStr = intent.getStringExtra(AppConfig.TV_RESULT);
        mTvMatchId = intent.getStringExtra(AppConfig.TV_TARGET_ID);
        if (null == networkReceiver) {
            registerNetReceiver();
        }

        isGetChannelList = false;
        getChannelList();

        handleOrderChannel();

        //设置别名
        JPushInterface.setAlias(MainApplication.getContext(), 1, DeviceUtils.getDeviceId());
        //监听版本升级
        UpdateManager.register(mCheckUpdateReceiver, this);
    }

    private CheckUpdateReceiver mCheckUpdateReceiver = new CheckUpdateReceiver() {
        @Override
        public void onReceive(String filePath, boolean isForceUpgrade) {
            //关闭其它弹窗
            PlayOrderActivity.dismiss();//预约弹窗
            hideDialog(selectMatchTvDialog);
            hideDialog(sourceDialog);
            hideDialog(tvDialog);
            hideDialog(mHintDialog);

            UpdateManager.show(mContext, filePath, isForceUpgrade, new UpdateManager.OnUpdateListener() {
                @Override
                public void onUpdate(boolean isForceUpgrade) {
                    if (mContext != null) {
                        if (isForceUpgrade) {//强制更新时关闭更新对话框
                            finish();
                        } else {
                            Intent intent = new Intent("action_close_play_gsgd");
                            mContext.sendBroadcast(intent);
                        }
                    }
                }
            });
        }
    };

    private void checkPlayTime() {
        PlayOrdersMananger.setPlayTime(mContext);
        mHandler.removeMessages(HANDLE_LONG_TIME_LOOK);
        mHandler.sendEmptyMessageDelayed(HANDLE_LONG_TIME_LOOK, PlayOrdersMananger.getPlayTimeDis());
    }

    private HintDialog mHintDialog;
    private boolean isPause;//表示是否需要处于pause状态

    /**
     * 显示疲劳提示
     */
    private void showHintDialog() {
        //关闭其它弹窗
        PlayOrderActivity.dismiss();//预约弹窗
        UpdateManager.hide();//升级提示弹窗
        hideDialog(tvDialog);//选台弹窗
        hideDialog(selectMatchTvDialog);//内容匹配弹窗
        hideDialog(sourceDialog);//选源弹窗

        if (mHintDialog != null && mHintDialog.isShowing()) return;

        hideDialog(mHintDialog);
        mHintDialog = new HintDialog(mContext, R.style.TransparentDialog);
        mHintDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                isPause = false;
                checkPlayTime();
                toggleOptTip(false);
                //开始播放
                mGsyVideoView.onVideoStart();
            }
        });

        mHintDialog.show();
        isPause = true;
        toggleOptTip(true);

        //停止播放
        mGsyVideoView.onVideoPause();
    }

    /**
     * 获取频道列表
     */
    private void getChannelList() {
        final long lastSplashTime = System.currentTimeMillis();
        toggleBgCover(true);
        addDisposable(ApiModule.getApiManager()
                .getAllChannelDirect()
                .subscribeWith(new DisposableObserver<ArrayList<ChannelType>>() {
                    @Override
                    public void onNext(ArrayList<ChannelType> value) {
                        long dexTime = System.currentTimeMillis() - lastSplashTime;
                        mHandler.removeMessages(WHAT_HANDLE_DATA_SUCCESS);

                        Message message = new Message();
                        message.what = WHAT_HANDLE_DATA_SUCCESS;
                        message.obj = value;
                        if (dexTime < AppConfig.TIME_SPLASH && dexTime > 0) {
                            mHandler.sendMessageDelayed(message, dexTime);

                        } else {
                            mHandler.sendMessage(message);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        JLog.e(e);
                        long dexTime = System.currentTimeMillis() - lastSplashTime;
                        mHandler.removeMessages(WHAT_HANDLE_DATA_ERROR);
                        if (dexTime < AppConfig.TIME_SPLASH && dexTime > 0) {
                            mHandler.sendEmptyMessageDelayed(WHAT_HANDLE_DATA_ERROR, dexTime);

                        } else {
                            mHandler.sendEmptyMessage(WHAT_HANDLE_DATA_ERROR);
                        }
                    }

                    @Override
                    public void onComplete() {
                    }
                })
        );
    }

    /**
     * 切换闪屏
     */
    private void toggleBgCover(boolean isNeedShow) {
        mIvBgCover.setVisibility(isNeedShow ? View.VISIBLE : View.GONE);
    }

    /**
     * 处理请求频道列表后逻辑
     */
    private void handleGetChannelResult() {
        if (null == mChannelTypes || mChannelTypes.size() == 0) {
            ToastUtil.showToast("无法获取视频资源，请检查网络后再试！");
            finish();
            return;
        }

        //初始化播放器
        initGsyVideo();

        ArrayList<Channel> tvMatchList = Utils.getMatchChannel(mTvMatchStr, mTvMatchId);

        if (null != tvMatchList && tvMatchList.size() > 0) {
            //通过命令进入
            JLog.d(TAG, "******通过命令进入->size:" + tvMatchList.size());

            mCurrentChannel = tvMatchList.get(0);//默认播放匹配列表的第一个频道
            mLastChannel = mCurrentChannel;
            mCurrentType = Utils.getSelectType(mCurrentChannel, mChannelTypes);
            mCurrentSource = mCurrentChannel.sources.get(0);

            if (tvMatchList.size() > 1) {
                //弹出框让用户选择
                showSelectMatchTvDialog(tvMatchList);
            }

        } else {
            //TODO
            if (!TextUtils.isEmpty(mTvMatchId)) {
                //匹配的频道ID没有数据
                //先显示错误提示，在处理上次播放的数据

            } else {
                //匹配搜索结果

            }

            try {
                //读取上一次播放的台
                int version = SP2Util.getInt(MainApplication.getContext(), AppConfig.SP_NAME, AppConfig.KEY_LAST_CACHE_VERSION, 0);
                if (version >= AppConfig.LAST_CACHE_VERSION) {
                    String json_type = SP2Util.getString(MainApplication.getContext(), AppConfig.SP_NAME, AppConfig.KEY_LAST_CHANNEL_TYPE, "");
                    String json_channel = SP2Util.getString(MainApplication.getContext(), AppConfig.SP_NAME, AppConfig.KEY_LAST_CHANNEL, "");
                    String json_source = SP2Util.getString(MainApplication.getContext(), AppConfig.SP_NAME, AppConfig.KEY_LAST_SOURCE, "");

                    if (!TextUtils.isEmpty(json_type)
                            && !TextUtils.isEmpty(json_channel)
                            && !TextUtils.isEmpty(json_source)) {
                        Gson gson = new Gson();

                        ChannelType currentType = gson.fromJson(json_type, ChannelType.class);
                        Channel currentChannel = gson.fromJson(json_channel, Channel.class);
                        RespSource currentSource = gson.fromJson(json_source, RespSource.class);

                        int type = Utils.isValidCurrentData(mChannelTypes, currentType, currentChannel, currentSource);
                        if (type == 0) {
                            //有效
                            mCurrentType = currentType;
                            mCurrentChannel = currentChannel;
                            mCurrentSource = currentSource;

                        } else if (type == 1) {
                            //有效，以mCurrentChannel第一个源为准
                            mCurrentType = currentType;
                            mCurrentChannel = currentChannel;
                            mCurrentSource = mCurrentChannel.sources.get(0);

                        } else if (type == 2) {
                            //有效，以mCurrentChannel为准
                            mCurrentChannel = currentChannel;
                            mCurrentSource = mCurrentChannel.sources.get(0);
                            mCurrentType = Utils.getSelectType(mCurrentChannel, mChannelTypes);

                        } else {
                            //无效
                            mCurrentType = mChannelTypes.get(0);//跳过我的收藏
                            mCurrentChannel = mCurrentType.channels.get(0);
                            mCurrentSource = mCurrentChannel.sources.get(0);
                        }
                        mLastChannel = mCurrentChannel;
                    }
                }

            } catch (Exception e) {
                JLog.e(TAG, e);
            }
        }

        //默认频道
        if (null == mCurrentType || null == mCurrentChannel) {
            mCurrentType = mChannelTypes.get(0);//跳过我的收藏
            mCurrentChannel = mCurrentType.channels.get(0);
            mLastChannel = mCurrentChannel;
        }

        if (null == mCurrentSource) {
            //默认播放第一个源
            mCurrentSource = mCurrentChannel.sources.get(0);
        }

        preparePlay(mCurrentSource, false);

        //重新请求数据
        onRefreshCacheEvent(null);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        Intent intent_new = getIntent();
        String tvMatchStr = intent_new.getStringExtra(AppConfig.TV_RESULT);
        String tvMatchId = intent.getStringExtra(AppConfig.TV_TARGET_ID);

        ArrayList<Channel> tvList = Utils.getMatchChannel(tvMatchStr, tvMatchId);

        if (null != tvList && tvList.size() > 0) {
            //通过命令进入
            JLog.d(TAG, "******通过命令进入->size:" + tvList.size());

            //关闭所有弹框
            PlayOrderActivity.dismiss();//预约弹窗
            UpdateManager.hide();//升级提示弹窗
            hideDialog(sourceDialog);//选源弹窗
            hideDialog(tvDialog);//选台弹窗
            hideDialog(selectMatchTvDialog);//内容匹配弹窗
            hideDialog(mHintDialog);//疲劳提示弹窗

            if (tvList.size() == 1) {
                try {
                    //直接播放
                    Channel channel = tvList.get(0);
                    ChannelType channelType = Utils.getSelectType(channel, mChannelTypes);
                    EventBus.getDefault().post(new PlayEvent.SelectChannelEvent(channelType, channel));

                } catch (Exception e) {
                    JLog.e(e);
                }

            } else {
                //弹出框让用户选择
                showSelectMatchTvDialog(tvList);
            }
        }

        handleOrderChannel();
    }

    private void handleOrderChannel() {
        //处理预约的项目
        if (PlayOrdersMananger.firstOrder(mContext) != null) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(mContext, ClockReceiver.class);
                    intent.setAction(ClockReceiver.ALARM_ACTION);
                    mContext.sendBroadcast(intent);
                }
            }, PlayOrdersMananger.DELAY_START_CLCOK);
        } else {
            PlayOrdersMananger.startClock(mContext);
        }
    }

    private void initGsyVideo() {
        mGsyVideoView.setGsyPlayListener(new CustomVideoView.GsyPlayListener() {
            @Override
            public void onPrepared() {
                JLog.d(TAG, "*************************onPrepared");
                toggleErrorUI(false, null);

                //监测黑屏
                mHandler.removeMessages(WHAT_TIME_CHECK_BLACK);
                mHandler.sendEmptyMessageDelayed(WHAT_TIME_CHECK_BLACK, AppConfig.TIME_CHECK_BLACK);
            }

            @Override
            public void onStartBuffer() {
                //监测黑屏
                mHandler.removeMessages(WHAT_TIME_CHECK_BLACK);

                mHandler.removeMessages(WHAT_TIME_OUT);
                mHandler.sendEmptyMessageDelayed(WHAT_TIME_OUT, AppConfig.TIME_OUT);
                toggleTip(true);
            }

            @Override
            public void onEndBuffer() {
                mHandler.removeMessages(WHAT_TIME_OUT);
                toggleTip(false);

                //监测黑屏
                mHandler.removeMessages(WHAT_TIME_CHECK_BLACK);

                if (isPause) {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (isPause) {
                                toggleOptTip(true);
                                try {//停止播放
                                    mGsyVideoView.onVideoPause();

                                } catch (Exception e) {
                                    JLog.e(TAG, e);
                                }
                            }
                        }
                    }, 1000);

                } else {
                    mHandler.sendEmptyMessageDelayed(WHAT_TIME_CHECK_BLACK, AppConfig.TIME_CHECK_BLACK);
                }
            }

            @Override
            public void onError() {
                mHandler.removeMessages(WHAT_TIME_CHECK_BLACK);
                mHandler.removeMessages(WHAT_TIME_OUT);
                toggleTip(false);
                handelError(2);
            }

            @Override
            public void onPause() {
                mHandler.removeMessages(WHAT_TIME_OUT);
                mHandler.removeMessages(WHAT_TIME_CHECK_BLACK);
            }

            @Override
            public void onComplete() {
                JLog.d(TAG, "****************************->onComplete");
                mHandler.removeMessages(WHAT_TIME_OUT);
                mHandler.removeMessages(WHAT_TIME_CHECK_BLACK);

                preparePlay(mCurrentSource, false);
            }
        });
    }

    /**
     * 准备播放
     *
     * @param source  播放源
     * @param isRetry 是否重试
     */
    private void preparePlay(final RespSource source, boolean isRetry) {
        //清空之前请求
        clearDisposable();

        //清空部分消息
        mHandler.removeMessages(WHAT_TIME_CHECK_BLACK);

        if (isPlayCustom) {
            //播放用户自建频道
            if (!isRetry) {
                //不需要重试时直接销毁当前播放器，避免出现画面与节目不匹配问题
                mGsyVideoView.release();
            }

            mIvLogo.setVisibility(View.GONE);
            isPreparePlay = false;
            if (AppConfig.isDebug) {
                //测试环境显示地址
                mTvAddress.setVisibility(AppConfig.isDebug ? View.VISIBLE : View.GONE);
                mTvAddress.setText((AppConfig.isPreRelease ? "预发布：" : "测试：") + mCustomInfo.sourceName);
            }
            showNoticeDialog();

            playVideo(mCustomInfo.sourceUrl);

        } else {

            if (null == mCurrentChannel) {
                return;
            }

            long currentTime = new Date().getTime();
            if (lastTime > 0) {
                if (mLastChannel.id != mCurrentChannel.id) {
                    int dexTime = (int) (currentTime - lastTime);

                    JLog.e(TAG, "**********->dexTime:" + dexTime);

                    if (dexTime >= 60 * 1000) {
                        //超过一分钟算做观看
                        HashMap<String, String> map = new HashMap<>();
                        map.put(mCurrentChannel.channel, String.valueOf(dexTime));
                        MobclickAgent.onEvent(mContext, "tv_watch_long", map);
                    }

                    lastTime = currentTime;
                    mLastChannel = mCurrentChannel;

                    //换台了，显示界面提示框
                    showNoticeDialog();

                } else {
                    //没换台，切源播放
                    JLog.e(TAG, "******->没换台，切源播放");

                    //换台了，显示界面提示框
                    showNoticeDialog();
                }

            } else {
                lastTime = currentTime;
                //第一次播放，显示界面提示框
                showNoticeDialog();
            }

            isPreparePlay = true;//开始准备播放
            toggleTip(true);

            mIvLogo.setVisibility(source.isInnerSource() ? View.VISIBLE : View.GONE);//是否是自有的央广源，展示logo
            if (AppConfig.isDebug) {
                //测试环境显示地址
                mTvAddress.setVisibility(AppConfig.isDebug ? View.VISIBLE : View.GONE);
                mTvAddress.setText((AppConfig.isPreRelease ? "预发布：" : "测试：") + source.id);
            }
            if (!isRetry) {
                //不需要重试时直接销毁当前播放器，避免出现画面与节目不匹配问题
                mGsyVideoView.release();
            }

            //记录当前播放的台
            if (null != mCurrentType && null != mCurrentChannel && null != mCurrentSource) {
                JLog.d("******->记录当前播放的台：" + mCurrentChannel.toString());
                SP2Util.putString(MainApplication.getContext(), AppConfig.SP_NAME, AppConfig.KEY_LAST_CHANNEL_TYPE, mCurrentType.toString());
                SP2Util.putString(MainApplication.getContext(), AppConfig.SP_NAME, AppConfig.KEY_LAST_CHANNEL, mCurrentChannel.toString());
                SP2Util.putString(MainApplication.getContext(), AppConfig.SP_NAME, AppConfig.KEY_LAST_SOURCE, mCurrentSource.toString());
                SP2Util.putInt(MainApplication.getContext(), AppConfig.SP_NAME, AppConfig.KEY_LAST_CACHE_VERSION, AppConfig.LAST_CACHE_VERSION);
            }

            //获取真实播放的源地址
            if (mCurrentChannel.isCustom && !TextUtils.isEmpty(source.source)) {
                //是自定义源
                JLog.d(TAG, "******->是自定义源！");
                playVideo(source.source);
                isPreparePlay = false;

            } else {
                addDisposable(ApiModule.getApiManager()
                                .getSourceById(source.id, false)
                                .subscribeWith(new DisposableObserver<SourceModel>() {
                                    @Override
                                    public void onNext(SourceModel value) {
                                        isPreparePlay = false;
                                        //for test
//                                value = "http://livestream.readyidu.com/live/mzdywan.flv";
                                        if (value.isSuccess()) {
                                            playVideo(value.getSource());

                                        } else {
                                            //维护中
                                            toggleErrorUI(true, value.getSource());
                                            hideNoticeDialog();
                                        }
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        isPreparePlay = false;
                                        JLog.e(TAG, "******->获取播放地址失败！");
                                        JLog.e(TAG, e);
                                        handelError(1);
                                    }

                                    @Override
                                    public void onComplete() {
                                        isPreparePlay = false;
                                    }
                                })
                );
            }
        }
    }

    /**
     * 播放视频
     *
     * @param path 视频地址
     */
    private void playVideo(String path) {
        JLog.d("******->当前播放地址：" + path);

        HashMap<String, String> map = new HashMap<>();//header参数
        int playerType = 0;//默认使用七牛，1使用EXO

        //解析path，是否需要添加参数
        String[] paths = path.split("#");
        String realPath = paths[0];

        if (paths.length == 2) {
            String[] paths2 = paths[1].split("\\$");
            if (paths2.length == 2) {
                try {
                    playerType = Integer.parseInt(paths2[1]);
                } catch (Exception e) {
                    JLog.e(e);
                }
            }

            try {
                JSONObject object = new JSONObject(paths2[0]);
                Iterator<String> iterator = object.keys();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    String value = object.optString(key);
                    map.put(key, value);
                }

                JLog.d("******->header参数：" + map);

            } catch (Exception e) {
                JLog.e(e);
            }

        } else {
            //以$分割
            String[] paths2 = paths[0].split("\\$");
            realPath = paths2[0];
            if (paths2.length == 2) {
                try {
                    playerType = Integer.parseInt(paths2[1]);
                } catch (Exception e) {
                    JLog.e(e);
                }
            }
        }

        //处理内部源链接
//        if (null != mCurrentSource && mCurrentSource.isInnerSource()) {
//            realPath = Utils.encryptionInnerUrl(realPath);
//        }

        GSYVideoType.enableMediaCodec();
//        GSYVideoManager.instance().setLogLevel(AppConfig.isOpenLog ? IjkMediaPlayer.IJK_LOG_VERBOSE : IjkMediaPlayer.IJK_LOG_SILENT);
        GSYVideoManager.instance().setLogLevel(IjkMediaPlayer.IJK_LOG_SILENT);
        GSYVideoType.setShowType(GSYVideoType.SCREEN_MATCH_FULL);
        if (playerType == 1) {
            GSYVideoManager.instance().setVideoType(this, GSYVideoType.IJKEXOPLAYER2);

        } else {
            GSYVideoManager.instance().setVideoType(this, GSYVideoType.IJKPLAYER);
        }

        //seek精准设置
//        VideoOptionModel videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1);
//        List<VideoOptionModel> list = new ArrayList<>();
//        list.add(videoOptionModel);
//        GSYVideoManager.instance().setOptionModelList(list);
//        mGsyVideoView.setLooping(isInDibbling);

        //播放
        mGsyVideoView.setUp(realPath, false, null, map, null);
        mGsyVideoView.startPlayLogic();
    }


    private void sendLivePlayer() {
        Intent intent = new Intent("com.readyidu.live.player");
        intent.putExtra("live", true);
        intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        sendBroadcast(intent);
    }

    @Override
    public void onResume() {
        super.onResume();

        sendLivePlayer();

        if (isGetChannelList) {
            if (mIsActivityPaused) {
                try {
                    mGsyVideoView.startPlayLogic();

                } catch (Exception e) {
                    JLog.e(TAG, e);
                }
            }
        }

        mIsActivityPaused = false;

        if (isClickFengmi) {
            //点击蜂蜜后返回
            isClickFengmi = false;
            JLog.d(TAG, "********isClickFengmi");

            if (null != tvDialog && tvDialog.isShowing()) {
                tvDialog.setIsClickFengmi(false);
                PlayEvent.PressKeyOnDialog key = new PlayEvent.PressKeyOnDialog(AppConfig.PRESS_CODE_SELECT_TV);
                EventBus.getDefault().post(key);
            }
        }

        //关闭蜜蜂视频
        sendBroadcast(new Intent("action_close_play"));
    }

    @Override
    public void onPause() {
        super.onPause();
        //关闭部分弹窗
        PlayOrderActivity.dismiss();//预约弹窗
        UpdateManager.hide();//升级提示弹窗
        hideDialog(selectMatchTvDialog);
        hideDialog(sourceDialog);
        hideDialog(mHintDialog);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        PlayOrdersMananger.clear();
//        hideDialog(mHintDialog);
//        UpdateManager.hide();
        UpdateManager.unRegister(mCheckUpdateReceiver, this);
        mHandler.removeCallbacksAndMessages(null);
        if (networkReceiver != null) {
            unregisterReceiver(networkReceiver);
        }

        try {
            mGsyVideoView.release();

        } catch (Exception e) {
            JLog.e(TAG, e);
        }
    }

    public String isShow(String head) {
        try {
            File file = new File(Environment.getExternalStoragePublicDirectory("") + "/.show_header_toast");
            ACache mCache = ACache.get(file);
            String type = mCache.getAsString(head);
            return type;

        } catch (Exception e) {
            JLog.e(TAG, e);
        }

        return null;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            checkPlayTime();
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        //点击中间OK按键
        if (isGetChannelList && (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER)) {
            String isShow = isShow("header_toast");
            if (!TextUtils.isEmpty(isShow) && isShow.equals("1")) {
                String nowType = isShow("now_type");
                JLog.d(nowType + "=========nowType");

                if (!TextUtils.isEmpty(nowType)) {
                    switch (nowType) {
                        case "体温":
                            RouterUtils.getInstance().goToHealthInfo(this, 2);
                            break;
                        case "血压":
                            RouterUtils.getInstance().goToHealthInfo(this, 1);
                            break;
                        case "血糖":
                            RouterUtils.getInstance().goToHealthInfo(this, 3);
                            break;
                        case "测量":
                            RouterUtils.getInstance().goToDeviceManager(this);
                            break;
                        case "聊天":
                            RouterUtils.getInstance().gotoChatApp(this, 0);
                            break;
                    }
                }
                return true;
            }

            JLog.d("TvDialogTime", "**********开始时间：" + System.currentTimeMillis());
            showSelectTvDialog();//显示选台弹窗

            return true;
        }

        if (isGetChannelList
                && !isPlayCustom
//                && mViewBgError.getVisibility() == View.GONE
                && (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)) {
            //非点播模式显示切源弹窗
            hideNoticeDialog();
            showSelectSourceDialog();

            return true;
        }

        if (isGetChannelList && (keyCode == KeyEvent.KEYCODE_DPAD_DOWN)) {
//            if (!isPreparePlay) {
            //切台
            PlayControlUtil.handlerPressSwitch(0, mPlayInfoListener);
//            }
            return true;
        }

        if (isGetChannelList && (keyCode == KeyEvent.KEYCODE_DPAD_UP)) {
//            if (!isPreparePlay) {
            //切台
            PlayControlUtil.handlerPressSwitch(1, mPlayInfoListener);
//            }
            return true;
        }

        return super.onKeyUp(keyCode, event);
    }

    private OnGetCustomTypeListener customTypeListener = new OnGetCustomTypeListener() {
        @Override
        public void getCustomList(List<CustomTypeInfo> value) {
            if (null != value) {
                mCustomTypeList = value;

            } else {
                mCustomTypeList = null;
            }

            refreshCustomChannel();
        }
    };

    private PlayControlUtil.PlayInfoListener mPlayInfoListener = new PlayControlUtil.PlayInfoListener() {
        @Override
        public boolean isPlayCustom() {
            return isPlayCustom;
        }

        @Override
        public ArrayList<ChannelType> getChannelTypeList() {
            return mChannelTypes;
        }

        @Override
        public ChannelType getCurrentChannelType() {
            return mCurrentType;
        }

        @Override
        public Channel getCurrentChannel() {
            return mCurrentChannel;
        }

        @Override
        public RespSource getCurrentSource() {
            return mCurrentSource;
        }

        @Override
        public void onPlayEnd() {
            //已经切到最后一个
            toggleErrorUI(true, null);
            hideNoticeDialog();
        }

        @Override
        public List<CustomTypeInfo> getCustomTypeList() {
            return mCustomTypeList;
        }

        @Override
        public CustomTypeInfo getCurrentCustomType() {
            return mCustomTypeInfo;
        }

        @Override

        public CustomInfo getCurrentCustomInfo() {
            return mCustomInfo;
        }
    };

    private void showError() {
        mViewBgError.setVisibility(View.VISIBLE);
        mViewBgError.getChildAt(0).setVisibility(View.VISIBLE);
        mViewBgError.getChildAt(1).setVisibility(View.GONE);
    }

    private void showWeihu(String date) {
        mViewBgError.setVisibility(View.VISIBLE);
        mViewBgError.getChildAt(0).setVisibility(View.GONE);
        RelativeLayout layout = (RelativeLayout) mViewBgError.getChildAt(1);
        layout.setVisibility(View.VISIBLE);
        TextView tv = (TextView) layout.getChildAt(1);
        tv.setText(String.format("( %s结束维护 )", date));
    }

    private void toggleErrorUI(boolean isNeedShow, String date) {
        if (isNeedShow) {
            toggleTip(false);
            if (date == null) {
                showError();
            } else {
                showWeihu(date);
            }
            mGsyVideoView.release();
            mHandler.removeMessages(WHAT_CHANGE_CHANNEL);
            //20s无操作自动换台
            mHandler.sendEmptyMessageDelayed(WHAT_CHANGE_CHANNEL, AppConfig.TIME_WHAT);

        } else {
            mHandler.removeMessages(WHAT_CHANGE_CHANNEL);
            mViewBgError.setVisibility(View.GONE);
        }
    }

    // 退出时间
    private long mExitTime = 0;
    // 点击返回键的时间间隔
    private static int TIMES = 2000;

    @Override
    public void onBackPressed() {
        if (mViewNotice.getVisibility() == View.VISIBLE) {
            //隐藏提示
            hideNoticeDialog();
            return;
        }

        if ((System.currentTimeMillis() - mExitTime) > TIMES) {
            ToastUtil.showToast("再按一次退出视频播放！");
            mExitTime = System.currentTimeMillis();

        } else {
            finish();
        }
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case AppConfig.PRESS_CODE_SELECT_TV:
                    hideDialog(tvDialog);
                    break;

                case AppConfig.PRESS_CODE_SELECT_SOURCE:
                    hideDialog(sourceDialog);
                    break;

                case AppConfig.PRESS_CODE_SELECT_NOTICE:
                    hideNoticeDialog();
                    break;

                case WHAT_TIME_OUT:
                    JLog.e("***************->播放超时了!");
                    mHandler.removeMessages(WHAT_TIME_OUT);
                    handelError(3);
                    break;

                case WHAT_CHANGE_CHANNEL:
                    JLog.d(TAG, "******->重试ing！");
                    //hideDialog(tvDialog);

                    if (isPlayCustom) {
                        preparePlay(mCurrentSource, true);//重试

                    } else {
                        PlayControlUtil.handlerPlayError(mPlayInfoListener, true);
                    }
                    break;

                case WHAT_HANDLE_DATA_ERROR:
                    //获取列表失败，退出应用
                    isGetChannelList = false;
                    toggleBgCover(true);
                    handleGetChannelResult();
                    break;

                case WHAT_HANDLE_DATA_SUCCESS:
                    try {
                        ArrayList<ChannelType> value = null;
                        if (null != msg.obj) {
                            value = (ArrayList<ChannelType>) msg.obj;
                        }
                        if (null != value && value.size() > 0) {
                            isGetChannelList = true;
                            mChannelTypes = value;
                        }

                    } catch (Exception e) {
                        JLog.e(TAG, e);
                    }

                    toggleBgCover(false);
                    handleGetChannelResult();

                    //数据请求成功后
                    mHandler.sendEmptyMessageDelayed(HANDLE_CHECK_UPDATE, 5000);
                    checkPlayTime();
                    break;

                case HANDLE_LONG_TIME_LOOK:
                    if (PlayOrdersMananger.isLongPlay(mContext)) {
                        showHintDialog();
                    }
                    checkPlayTime();
                    break;

                case HANDLE_CHECK_UPDATE:
                    mHandler.removeMessages(HANDLE_CHECK_UPDATE);
                    UpdateManager.checkUpdate(mContext);
                    break;

                case WHAT_TIME_CHECK_BLACK:
                    judgmentIsBlack();
                    break;

                default:
                    break;
            }

            return true;
        }
    });

    private void showNoticeDialog() {
        mViewNotice.setVisibility(View.VISIBLE);
        mViewNotice.setTvShowName(isPlayCustom ? mCustomInfo.sourceName : Utils.simplifyName(mCurrentChannel.channel));
        toggleOptTip(true);

        if (isPlayCustom) {
            mViewNotice.resetUI();

        } else {
            final List<ProgramInfo> list = ProgramListUtils.getTodayProgramListById(mCurrentChannel.id);
            if (null == list || list.size() == 0) {
                mViewNotice.resetUI();
                //getPlayBill(mCurrentChannel.id);

            } else {
                handlePlayBillView(list);
            }

            getPlayBill(mCurrentChannel.id);
        }

        mHandler.removeMessages(AppConfig.PRESS_CODE_SELECT_NOTICE);
        mHandler.sendEmptyMessageDelayed(AppConfig.PRESS_CODE_SELECT_NOTICE, AppConfig.TIME_RIGHT_DISMISS);
    }

    private void hideNoticeDialog() {
        if (mViewNotice.getVisibility() != View.GONE) {
            mViewNotice.setVisibility(View.GONE);
            mViewNotice.resetUI();
        }

        mHandler.removeMessages(AppConfig.PRESS_CODE_SELECT_NOTICE);
        toggleOptTip(false);
    }

    private void hideDialog(BaseDialog dialog) {
        try {
            if (null != dialog && dialog.isShowing()) {
                dialog.dismiss();
            }
        } catch (Exception e) {
            JLog.e(e);
        }
    }

    SelectTvDialog tvDialog;

    private boolean isClickDialog = false;

    /**
     * 显示选择栏目界面
     */
    public void showSelectTvDialog() {
        hideDialog(tvDialog);

        tvDialog = new SelectTvDialog(mContext, R.style.TransparentDialog, mPlayInfoListener, getOptChannelListener(), customTypeListener);
        tvDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mHandler.removeMessages(AppConfig.PRESS_CODE_SELECT_TV);
                if (!isClickDialog) {
                    hideNoticeDialog();
                }
                isClickDialog = false;
                mViewNotice.setAlpha(1);
                mTvTime.setAlpha(1);
            }
        });
        tvDialog.show();

        //几秒钟无操作后自动隐藏
        mHandler.removeMessages(AppConfig.PRESS_CODE_SELECT_TV);
        mHandler.sendEmptyMessageDelayed(AppConfig.PRESS_CODE_SELECT_TV, AppConfig.TIME_DISMISS);

        showNoticeDialog();

        mHandler.removeMessages(AppConfig.PRESS_CODE_SELECT_NOTICE);
    }

    private void toggleOptTip(boolean isShow) {
        if (isShow) {
            mTvTime.startUpdate();
            mTvTime.setVisibility(View.VISIBLE);

        } else {
            mTvTime.stopUpdate();
            mTvTime.setVisibility(View.GONE);
        }
    }

    SelectSourceDialog sourceDialog;

    /**
     * 显示选择切源界面
     */
    public void showSelectSourceDialog() {
        hideDialog(sourceDialog);

        sourceDialog = new SelectSourceDialog(mContext, R.style.TransparentDialog, mCurrentChannel.sources, mCurrentSource, mCurrentChannel);
        sourceDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mHandler.removeMessages(AppConfig.PRESS_CODE_SELECT_SOURCE);
            }
        });
        sourceDialog.show();

        //几秒钟无操作后自动隐藏
        mHandler.sendEmptyMessageDelayed(AppConfig.PRESS_CODE_SELECT_SOURCE, AppConfig.TIME_DISMISS);
    }

    SelectMatchTvDialog selectMatchTvDialog;

    /**
     * 显示选择匹配的源界面
     */
    private void showSelectMatchTvDialog(ArrayList<Channel> tvList) {
        hideDialog(selectMatchTvDialog);
        selectMatchTvDialog = new SelectMatchTvDialog(mContext, R.style.TransparentDialog, mChannelTypes, tvList);
        selectMatchTvDialog.show();
    }

    AddSourceDialog addSourceDialog;

    /**
     * 显示添加源界面
     */
    private void showAddSourceDialog() {
        hideDialog(addSourceDialog);
        addSourceDialog = new AddSourceDialog(mContext, R.style.TransparentDialog);
        addSourceDialog.show();
    }

    DeleteSourceDialog deleteSourceDialog;

    /**
     * 显示删除源界面
     */
    private void showDeleteSourceDialog(Channel channel) {
        hideDialog(deleteSourceDialog);
        deleteSourceDialog = new DeleteSourceDialog(mContext, R.style.TransparentDialog, channel);
        deleteSourceDialog.show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPressKeyOnDialogEvent(PlayEvent.PressKeyOnDialog event) {
        mHandler.removeMessages(event.type);
        mHandler.removeMessages(AppConfig.PRESS_CODE_SELECT_NOTICE);
        if (!event.isNeedKeep) {
            mHandler.sendEmptyMessageDelayed(event.type, AppConfig.TIME_DISMISS);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSelectFenMiEvent(PlayEvent.SelectFenMiEvent event) {
        this.isClickFengmi = true;
        mHandler.removeMessages(AppConfig.PRESS_CODE_SELECT_TV);
        mHandler.removeMessages(AppConfig.PRESS_CODE_SELECT_NOTICE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSelectCustomChannelEvent(PlayEvent.SelectCustomChannelEvent event) {
        //播放自建频道
        try {
            if (null != event.typeInfo && null != event.info && !TextUtils.isEmpty(event.info.sourceUrl)) {
                if (null != mCustomTypeInfo
                        && mCustomTypeInfo.userId == event.typeInfo.userId
                        && null != mCustomInfo
                        && event.info.sourceUrl.equals(mCustomInfo.sourceUrl)
                        && isPlayCustom) {
                    //正在播放

                    return;
                }

                isClickDialog = true;
                mCustomTypeInfo = event.typeInfo;
                mCustomInfo = event.info;
                isPlayCustom = true;

                //播放
                preparePlay(null, false);

            }

        } catch (Exception e) {
            JLog.e(TAG, e);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSelectChannelEvent(PlayEvent.SelectChannelEvent event) {
        if (null != event) {
            mCurrentType = event.channelType;

            //判断是播放三级频道还是二级频道
            if (null != event.subChannel) {
                //如果播放的是三级
                JLog.d(TAG, "******onSelectChannelEvent 1->将要播放三级栏目！");

                //判断当前是否在播放
                if (null != mCurrentChannel && mCurrentChannel.id == event.channel.id
                        && null != mCurrentSubChannel && mCurrentSubChannel.id == event.subChannel.id
                        && !isPlayCustom) {

                    isClickDialog = false;
                    JLog.d(TAG, "******onSelectChannelEvent 2->当前节目在播放！");

                } else {
                    JLog.d(TAG, "******onSelectChannelEvent 3->当前节目不在播放，去播放三级栏目！");

                    PlayEvent.ChangeSourceEvent sourceEvent = new PlayEvent.ChangeSourceEvent(event.channel, event.subChannel.sources.get(0));
                    sourceEvent.subChannel = event.subChannel;
                    sourceEvent.isPlayCustom = isPlayCustom;
                    onChangeSourceEvent(sourceEvent);
                    isClickDialog = event.isClickDialog;
                }

            } else {
                //如果播放的是二级
                JLog.d(TAG, "******onSelectChannelEvent 4->将要播放二级栏目！");

                //判断当前是否在播放
                if (null != mCurrentChannel && mCurrentChannel.id == event.channel.id
                        && null == mCurrentSubChannel
                        && !isPlayCustom) {

                    isClickDialog = false;
                    JLog.d(TAG, "******onSelectChannelEvent 5->当前节目在播放！");

                } else {
                    JLog.d(TAG, "******onSelectChannelEvent 6->当前节目不在播放，去播放二级栏目！");
                    PlayEvent.ChangeSourceEvent sourceEvent = new PlayEvent.ChangeSourceEvent(event.channel, event.channel.sources.get(0));
                    sourceEvent.isPlayCustom = isPlayCustom;
                    onChangeSourceEvent(sourceEvent);
                    isClickDialog = event.isClickDialog;
                }
            }

            isPlayCustom = false;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChangeSourceEvent(PlayEvent.ChangeSourceEvent event) {
        if (null != event && null != event.channel && null != event.source) {
            //判断是否是来自第三级的播放请求
            isPlayCustom = false;

            if (null != event.subChannel) {
                JLog.d(TAG, "******onChangeSourceEvent 1->准备播放第三级栏目！");

                //判断当前源是否在播放
                if (null != mCurrentSubChannel && mCurrentSubChannel.id == event.subChannel.id
                        && null != mCurrentSource && mCurrentSource.id == event.source.id
                        && !event.isPlayCustom) {
                    isClickDialog = false;
                    JLog.d(TAG, "******onChangeSourceEvent 2->当前子源已在播放！");

                    if (event.isNeedForcePlay) {
                        //需要强制重放
                        preparePlay(mCurrentSource, true);
                    }

                } else {
                    JLog.d(TAG, "******onChangeSourceEvent 3->当前子源不在播放，准备播放！");

                    errCount = 0;
                    mCurrentChannel = event.channel;
                    mCurrentSubChannel = event.subChannel;
                    mCurrentSource = event.source;

                    preparePlay(mCurrentSource, false);
                }

            } else {
                JLog.d(TAG, "******onChangeSourceEvent 4->准备播放第二级栏目！");

                //判断当前源是否在播放
                if (null != mCurrentChannel && event.channel.id == mCurrentChannel.id
                        && null == mCurrentSubChannel && event.source.id == mCurrentSource.id
                        && !event.isPlayCustom) {

                    isClickDialog = false;
                    JLog.d(TAG, "******onChangeSourceEvent 5->当前子源已在播放！");

                    if (event.isNeedForcePlay) {
                        //需要强制重放
                        preparePlay(mCurrentSource, true);
                    }

                } else {
                    JLog.d(TAG, "******onChangeSourceEvent 6->当前子源不在播放，准备播放！");
                    errCount = 0;
                    mCurrentChannel = event.channel;
                    mCurrentSubChannel = null;
                    mCurrentSource = event.source;

                    preparePlay(mCurrentSource, false);
                }
            }

            isPlayCustom = false;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowAddSourceDialogEvent(PlayEvent.ShowAddSourceDialog event) {
        showAddSourceDialog();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSaveSourceEvent(PlayEvent.SaveSourceEvent event) {
        if (null != event) {
            saveCustomSource(event.name, event.source);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowDeleteSourceDialogEvent(PlayEvent.ShowDeleteSourceDialog event) {
        if (null != event) {
            showDeleteSourceDialog(event.channel);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeleteSourceEvent(PlayEvent.DeleteSourceEvent event) {
        if (null != event) {
            deleteCustomSource(event.channel);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBindDeviceEvent(PlayEvent.BindDeviceEvent event) {
        if (null != tvDialog && tvDialog.isShowing()) {
            tvDialog.getBindPhoneList();
            tvDialog.getCustomList();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSynCustomEvent(PlayEvent.SynCustomEvent event) {
        if (null != tvDialog && tvDialog.isShowing()) {
            tvDialog.getCustomList();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshCacheEvent(PlayEvent.RefreshCache event) {
        final int type = null == event ? 0 : 1;
        addDisposable(ApiModule.getApiManager()
                .getAllChannelDirect()
                .subscribeWith(new DisposableObserver<ArrayList<ChannelType>>() {
                    @Override
                    public void onNext(ArrayList<ChannelType> value) {
                        if (null != value && value.size() > 0) {
                            mChannelTypes = value;

                            refreshChannel(type);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        JLog.e(e);
                    }

                    @Override
                    public void onComplete() {
                    }
                })
        );
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResetEvent(PlayEvent.ResetEvent event) {
        //清除缓存
        //TODO 重置某些数据
        hideDialog(tvDialog);
        hideNoticeDialog();
        clearDisposable();
        mHandler.removeCallbacksAndMessages(null);
        ContentUtils.getInstance().deleteAll(getApplicationContext());//清空消息

        mTvMatchStr = "";//获取匹配的频道,外部传来的数据，用于匹配台
        mTvMatchId = "";
        mChannelTypes = null;//频道列表，所有界面的数据源
        isGetChannelList = false;//是否获取到了频道列表
        mIsActivityPaused = false;
        mCurrentType = null;//当前栏目
        mCurrentChannel = null;//当前节目
        mCurrentSubChannel = null;//当前子节目
        mLastChannel = null;//上一个节目
        mCurrentSource = null;//当前播放源

        isPreparePlay = false;//是否准备播放
        errCount = 0;//当前源播放错误次数
        lastTime = -1;//上一次播放时间

        mViewNotice.setAlpha(1);
        mTvTime.setAlpha(1);

        try {
            mGsyVideoView.release();

            ApiModule.getApiManager().clearCache();
            SP2Util.putString(MainApplication.getContext(), AppConfig.SP_NAME, AppConfig.KEY_LAST_CHANNEL_TYPE, "");
            SP2Util.putString(MainApplication.getContext(), AppConfig.SP_NAME, AppConfig.KEY_LAST_CHANNEL, "");
            SP2Util.putString(MainApplication.getContext(), AppConfig.SP_NAME, AppConfig.KEY_LAST_SOURCE, "");

            SPUtil.putInt(MainApplication.getContext(), AppConfig.SP_SETTING_NAME, AppConfig.KEY_SETTING_SCALE_MODE, 0);
            SPUtil.putInt(MainApplication.getContext(), AppConfig.SP_SETTING_NAME, AppConfig.KEY_SETTING_DECODE_MODE, 0);

            ApiModule.getApiManager().clearDb();

            writeMessage("0");

        } catch (Exception e) {
            JLog.e(e);
        }

        getChannelList();
    }

    public void writeMessage(String message_open) {
        File file = new File(Environment.getExternalStoragePublicDirectory("") + "/.live_message_open");
        if (!file.exists()) {
            file.mkdirs();
        }
        ACache mCache = ACache.get(file);
        mCache.put("message_open", message_open);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSelectScaleEvent(PlayEvent.SelectScaleEvent event) {
        if (event.type == 2) {
            GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_DEFAULT);

        } else {
            GSYVideoType.setShowType(GSYVideoType.SCREEN_MATCH_FULL);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSelectMenuEvent(PlayEvent.SelectMenuEvent event) {
        if (event.menuType == MenuType.TV_LIVE || event.menuType == MenuType.TV_MESSAGE) {
            //显示
            mViewNotice.setAlpha(1);
            mTvTime.setAlpha(1);

        } else if (event.menuType == MenuType.PHONE_LIVE || event.menuType == MenuType.TV_CUSTOM || event.menuType == MenuType.TV_SETTING) {
            //隐藏
            mViewNotice.setAlpha(0);
            mTvTime.setAlpha(0);
        }
    }

    /**
     * 刷新源
     *
     * @param fromType 1来自推送
     */
    private void refreshChannel(int fromType) {
        try {
            ChannelType tmpChannelType = null;
            Channel tmpChannel = null;
            RespSource tmpSources = null;

            if (null != mCurrentType) {
                for (ChannelType type : mChannelTypes) {
                    if (type.id == mCurrentType.id) {
                        tmpChannelType = type;
                        break;
                    }
                }
            }
            if (null != mCurrentChannel) {
                if (null != tmpChannelType) {
                    for (Channel channel : tmpChannelType.channels) {
                        if (channel.id == mCurrentChannel.id) {
                            tmpChannel = channel;
                            break;
                        }
                    }
                }
            }
            if (null != mCurrentSource) {
                if (null != tmpChannel) {
                    for (RespSource sources : tmpChannel.sources) {
                        if (mCurrentSource.id == sources.id) {
                            tmpSources = mCurrentSource;
                            break;
                        }
                    }
                }
            }

            int type = Utils.isValidCurrentData(mChannelTypes, mCurrentType, mCurrentChannel, mCurrentSource);
            if (type == 0) {
                JLog.d(TAG, "******->有效的数据，无需刷新！");
                mCurrentType = tmpChannelType;
                mCurrentChannel = tmpChannel;
                mCurrentSource = tmpSources;

            } else if (type == 1) {
                JLog.d(TAG, "******->有效的数据，但是需要刷新源！");
                mCurrentType = tmpChannelType;
                mCurrentChannel = tmpChannel;
                mCurrentSource = mCurrentChannel.sources.get(0);

                //重新播放
                preparePlay(mCurrentSource, true);

            } else if (type == 2) {
                JLog.d(TAG, "******->有效的数据，需刷新！");
                mCurrentType = tmpChannelType;
                mCurrentSource = mCurrentChannel.sources.get(0);
                mCurrentType = Utils.getSelectType(mCurrentChannel, mChannelTypes);

                //重新播放
                preparePlay(mCurrentSource, true);

            } else {
                JLog.d(TAG, "******->无效的数据，需刷新！");
                mCurrentType = mChannelTypes.get(0);//跳过我的收藏
                mCurrentChannel = mCurrentType.channels.get(0);
                mCurrentSource = mCurrentChannel.sources.get(0);

                //重新播放
                preparePlay(mCurrentSource, false);
            }

            if (1 == fromType) {
                //无刷新
                hideDialog(sourceDialog);
                hideDialog(tvDialog);
                hideDialog(selectMatchTvDialog);
                hideNoticeDialog();
                mHandler.removeMessages(AppConfig.PRESS_CODE_SELECT_SOURCE);
                mHandler.removeMessages(AppConfig.PRESS_CODE_SELECT_TV);
            }

            //更新记录当前播放的数据
            SP2Util.putString(MainApplication.getContext(), AppConfig.SP_NAME, AppConfig.KEY_LAST_CHANNEL_TYPE, mCurrentType.toString());
            SP2Util.putString(MainApplication.getContext(), AppConfig.SP_NAME, AppConfig.KEY_LAST_CHANNEL, mCurrentChannel.toString());
            SP2Util.putString(MainApplication.getContext(), AppConfig.SP_NAME, AppConfig.KEY_LAST_SOURCE, mCurrentSource.toString());
            SP2Util.putInt(MainApplication.getContext(), AppConfig.SP_NAME, AppConfig.KEY_LAST_CACHE_VERSION, AppConfig.LAST_CACHE_VERSION);

        } catch (Exception e) {
            JLog.e(TAG, e);
        }
    }

    private void reportSource(int sourceId, boolean isLive) {
        if (AppConfig.isNeedReport) {
            if (null == mCurrentType || mCurrentType.id == AppConfig.CUSTOM_TYPE_ID) {
                //自定义源不用上报
                return;
            }
            addDisposable(ApiModule.getApiManager()
                    .reportSource(sourceId, isLive)
                    .subscribeWith(new DisposableObserver<String>() {
                        @Override
                        public void onNext(String value) {
                            JLog.d("******->举报成功！");
                        }

                        @Override
                        public void onError(Throwable e) {
                            JLog.e(e);
                        }

                        @Override
                        public void onComplete() {
                        }
                    })
            );
        }
    }

    private void getPlayBill(final long channelId) {
        addDisposable(ApiModule.getApiManager()
                .getPlayBill(channelId)
                .subscribeWith(new DisposableObserver<RespPlayBill>() {
                    @Override
                    public void onNext(RespPlayBill value) {
                        if (null != value && null != value.todayProgram) {
                            if (null != mCurrentChannel && channelId == mCurrentChannel.id) {
                                ProgramListUtils.setTodayProgramListById(mCurrentChannel.id, value.todayProgram);
                                handlePlayBillView(value.todayProgram);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        JLog.e(e);
                    }

                    @Override
                    public void onComplete() {
                    }
                })
        );
    }

    private void handlePlayBillView(List<ProgramInfo> todayProgram) {
        try {
            if (null == todayProgram || todayProgram.size() == 0) {
                return;
            }
            //当前时间点
            long time = getTime(getTime());
            int firstIndex = -1;
            int dex;
            int size = todayProgram.size();
            for (int i = 0; i < size; i++) {
                ProgramInfo info = todayProgram.get(i);
                dex = (int) (time - getTime(info.showTime));
                if (firstIndex == -1 && dex < 0) {
                    firstIndex = i;
                }
            }

            if (firstIndex == -1) {
                //当前时间在最后面
                mViewNotice.setTvShowContent(
                        todayProgram.get(size - 1).channelName,
                        todayProgram.get(size - 1).showTime,
                        null, null);

            } else if (firstIndex == 0) {
                //当前时间在最前面
                mViewNotice.setTvShowContent(
                        null, null,
                        todayProgram.get(0).channelName,
                        todayProgram.get(0).showTime);

            } else {
                mViewNotice.setTvShowContent(
                        todayProgram.get(firstIndex - 1).channelName,
                        todayProgram.get(firstIndex - 1).showTime,
                        todayProgram.get(firstIndex).channelName,
                        todayProgram.get(firstIndex).showTime);
            }

        } catch (Exception e) {
            JLog.e(e);
        }
    }

    private SimpleDateFormat dateFm = new SimpleDateFormat("HH:mm", Locale.SIMPLIFIED_CHINESE);

    private String getTime() {
        return dateFm.format(new Date().getTime());
    }

    private long getTime(String time) throws ParseException {
        return dateFm.parse(time).getTime();
    }

    public ChannelListener getOptChannelListener() {
        return new ChannelListener() {
            @Override
            public void optChannel(int type, Channel channel) {
                // 1 删除自定义源；2 添加收藏；3 取消收藏
                switch (type) {
                    case 1:
                        deleteCustomSource(channel);
                        break;

                    case 2:
                        addCollectionChannel(channel);
                        break;

                    case 3:
                        delCollectionChannel(channel);
                        break;
                }
            }
        };
    }

    /**
     * 刷新自建源
     */
    private void refreshCustomChannel() {
        //TODO
    }

    /**
     * 保存自定义源
     */
    private void saveCustomSource(String name, String source) {
        addDisposable(ApiModule.getApiManager()
                .saveCustomSource(name, source)
                .subscribeWith(new DisposableObserver<Channel>() {
                    @Override
                    public void onNext(Channel channel) {
                        //刷新源列表
                        refreshSourceList(channel, 0);
                    }

                    @Override
                    public void onError(Throwable e) {
                        JLog.e(e);
                    }

                    @Override
                    public void onComplete() {
                    }
                }));
    }

    /**
     * 删除自定义源
     */
    private void deleteCustomSource(final Channel channel) {
        addDisposable(ApiModule.getApiManager()
                .delCustomSource(channel.id)
                .subscribeWith(new DisposableObserver<Boolean>() {
                    @Override
                    public void onNext(Boolean value) {
                        hideDialog(tvDialog);
                        //刷新源列表
                        refreshSourceList(channel, 1);
                    }

                    @Override
                    public void onError(Throwable e) {
                        JLog.e(e);
                    }

                    @Override
                    public void onComplete() {
                    }
                }));
    }

    private void refreshSourceList(final Channel channel, final int type) {
        addDisposable(ApiModule.getApiManager()
                .refreshSourceList()
                .subscribeWith(new DisposableObserver<ChannelType>() {
                    @Override
                    public void onNext(ChannelType value) {
                        if (null != value && null != mChannelTypes && mChannelTypes.size() > 0) {
                            if (value.id == AppConfig.CUSTOM_TYPE_ID
                                    && value.id == mChannelTypes.get(mChannelTypes.size() - 1).id) {
                                mChannelTypes.set(mChannelTypes.size() - 1, value);

                                if (type == 1) {
                                    //删台
                                    if (null != channel && channel.id == mCurrentChannel.id) {
                                        //切台
                                        PlayControlUtil.handlerPressSwitch(0, mPlayInfoListener);
                                    }

                                } else if (type == 0) {
                                    //加台
                                    if (null != channel) {
                                        //直接播放刚添加的频道
                                        EventBus.getDefault().post(new PlayEvent.SelectChannelEvent(mChannelTypes.get(mChannelTypes.size() - 1), channel));
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        JLog.e(e);
                    }

                    @Override
                    public void onComplete() {
                    }
                }));
    }

    /**
     * 添加收藏源
     */
    private void addCollectionChannel(final Channel channel) {
        addDisposable(ApiModule.getApiManager()
                .addCollectionChannel(channel)
                .subscribeWith(new DisposableObserver<Channel>() {
                    @Override
                    public void onNext(Channel channel) {
                        refreshCollectionChannel(channel, 1, false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        JLog.e(e);
                    }

                    @Override
                    public void onComplete() {
                    }
                }));
    }

    /**
     * 删除收藏源
     */
    private void delCollectionChannel(final Channel channel) {
        addDisposable(ApiModule.getApiManager()
                .delCollectionChannel(channel.id)
                .subscribeWith(new DisposableObserver<Boolean>() {
                    @Override
                    public void onNext(Boolean value) {
                        //刷新源列表
                        refreshCollectionChannel(channel, 2, value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        JLog.e(e);
                    }

                    @Override
                    public void onComplete() {
                    }
                }));
    }

    /**
     * 刷新收藏数据
     *
     * @param collectChannel
     * @param optType        1添加收藏；2删除收藏
     * @param value          是否删除成功
     */
    private void refreshCollectionChannel(Channel collectChannel, int optType, boolean value) {
        try {
            if (optType == 1) {
                //刷新状态
                for (ChannelType channelType : mChannelTypes) {
                    for (Channel channel : channelType.channels) {
                        if (channel.id == collectChannel.id) {
                            channel.collectionStatus = 1;
                        }
                    }
                }

                //添加数目
                mChannelTypes.get(0).channels.add(0, collectChannel);

            } else if (optType == 2) {
                //刷新状态
                for (ChannelType channelType : mChannelTypes) {
                    for (Channel channel : channelType.channels) {
                        if (channel.id == collectChannel.id) {
                            channel.collectionStatus = 0;
                        }
                    }
                }

                //删除
                for (Channel channel : mChannelTypes.get(0).channels) {
                    if (channel.id == collectChannel.id) {
                        mChannelTypes.get(0).channels.remove(channel);
                        break;
                    }
                }

                if (collectChannel.id == mCurrentChannel.id) {
                    //删除的收藏是当前播放的
                    mCurrentType = Utils.getSelectType(mCurrentChannel, mChannelTypes);
                }
            }

        } catch (Exception e) {
            JLog.e(e);
        }
    }

    /**
     * 处理错误
     *
     * @param type 1地址请求出错 2播放器出错 3播放超时
     */
    private void handelError(int type) {
        if (isPlayCustom) {
            PlayControlUtil.handlerPlayError(mPlayInfoListener, false);

        } else {
            JLog.e(TAG, "******->typeId:" + mCurrentType.id + "||channelId:" + mCurrentChannel.id + "||source:" + mCurrentSource);
            reportSource(mCurrentSource.id, true);
            if (!mIsActivityPaused) {
                if (mCurrentSource.isInnerSource() && errCount < MAX_ERROR_COUNT) {
                    //重试
                    errCount++;
                    JLog.e(TAG, "******重试->errCount:" + errCount);
                    preparePlay(mCurrentSource, true);

                } else {
                    errCount = 0;
                    JLog.e(TAG, "******出错->errCount:" + errCount);
                    //切换播放源或者自动换台
                    PlayControlUtil.handlerPlayError(mPlayInfoListener, false);
                }
            }
        }
    }

    /**
     * 切源提示
     *
     * @param needShow 显示还是隐藏
     */
    private void toggleTip(boolean needShow) {
        mStatusView.setVisibility(needShow ? View.VISIBLE : View.GONE);
    }

    private void registerNetReceiver() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(AppConfig.ACTION_CLOSE_PLAY_GSGD);//关闭通知
        networkReceiver = new MyReceiver();
        registerReceiver(networkReceiver, filter);
    }

    private class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (null != intent) {
                String action = intent.getAction();
                if (AppConfig.ACTION_CLOSE_PLAY_GSGD.equals(action)) {
                    pauseVideo();
                }
            }
        }
    }

    private void pauseVideo() {
        try {
            JLog.e(TAG, "************pauseVideo");
            mIsActivityPaused = true;
            clearDisposable();
            mGsyVideoView.release();

            mHandler.removeMessages(WHAT_TIME_OUT);
            mHandler.removeMessages(WHAT_TIME_CHECK_BLACK);

        } catch (Exception e) {
            JLog.e(TAG, e);
        }
    }

    /**
     * 判断是否黑屏
     */
    private void judgmentIsBlack() {
        try {
            Bitmap bitmap = mGsyVideoView.getRenderProxy().getShot();
            if (null == bitmap) {
                return;
            }

            addDisposable(ApiModule.getApiManager()
                    .judgmentIsBlack(bitmap)
                    .subscribeWith(
                            new DisposableObserver<Boolean>() {

                                @Override
                                public void onNext(Boolean isBlack) {
                                    if (isBlack) {
                                        handelError(3);
                                    }
                                }

                                @Override
                                public void onError(Throwable e) {

                                }

                                @Override
                                public void onComplete() {

                                }
                            }
                    ));

        } catch (Exception e) {
            JLog.e(TAG, e);
        }
    }

}
