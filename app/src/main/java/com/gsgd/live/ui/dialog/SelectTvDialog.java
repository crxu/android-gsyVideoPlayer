package com.gsgd.live.ui.dialog;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.gsgd.live.AppConfig;
import com.gsgd.live.MainApplication;
import com.gsgd.live.data.api.ApiException;
import com.gsgd.live.data.api.ApiModule;
import com.gsgd.live.data.events.PlayEvent;
import com.gsgd.live.data.listener.AbstractOnMenuDialogImpl;
import com.gsgd.live.data.listener.ChannelListener;
import com.gsgd.live.data.listener.OnGetCustomTypeListener;
import com.gsgd.live.data.listener.OnMenuChangeListener;
import com.gsgd.live.data.model.CustomTypeInfo;
import com.gsgd.live.data.response.RespDevice;
import com.gsgd.live.ui.base.BaseDialog;
import com.gsgd.live.ui.leftMenu.LeftMenuView;
import com.gsgd.live.ui.leftMenu.MenuContentView;
import com.gsgd.live.ui.leftMenu.MenuType;
import com.gsgd.live.utils.MeasureUtil;
import com.gsgd.live.utils.PlayControlUtil;
import com.gsgd.live.utils.PlayOrdersMananger;
import com.jiongbull.jlog.JLog;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import hdpfans.com.R;
import io.reactivex.observers.DisposableObserver;

/**
 * 选择视频栏目
 */
public class SelectTvDialog extends BaseDialog {

    private static final int WHAT_SYS_TIME = 9999;//系统时间

    @BindView(R.id.view_left_menu)
    LeftMenuView mLeftMenuView;
    @BindView(R.id.view_content_menu)
    MenuContentView mMenuContentView;
    @BindView(R.id.tv_time)
    TextView mTvTime;

    private ChannelListener mChannelListener;
    private PlayControlUtil.PlayInfoListener mPlayInfoListener;
    private OnGetCustomTypeListener customTypeListener;

    public SelectTvDialog(@NonNull Context context, @StyleRes int themeResId, PlayControlUtil.PlayInfoListener infoListener, ChannelListener listener, OnGetCustomTypeListener customTypeListener) {
        super(context, themeResId);

        this.mPlayInfoListener = infoListener;
        this.mChannelListener = listener;
        this.customTypeListener = customTypeListener;
        PlayOrdersMananger.clearOrder();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.dialog_select_tv;
    }

    @Override
    protected void screenAdapter() {
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.height = MeasureUtil.getScreenSize(MainApplication.getContext()).y;
        params.width = MeasureUtil.getScreenSize(MainApplication.getContext()).x;
        window.setAttributes(params);
        window.setGravity(Gravity.LEFT | Gravity.TOP);
        window.setWindowAnimations(R.style.dialogWindowAnim_menu);
    }

    @Override
    protected void initParams() {
        super.initParams();

        JLog.d("TvDialogTime", "**********initParams时间：" + System.currentTimeMillis());

        mLeftMenuView.setOnMenuChangeListener(menuChangeListener);//设置菜单监听
        if (mPlayInfoListener.isPlayCustom()) {
            mLeftMenuView.setLeftMenuSelected(MenuType.TV_CUSTOM);
            mLeftMenuView.handleGetFocus();

        } else {
            mLeftMenuView.setLeftMenuSelected(MenuType.TV_LIVE);
        }

        //延时刷新界面优化卡顿
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mMenuContentView.refreshTvLiveUI(mPlayInfoListener, mChannelListener, menuDialogListener);//初始化电视直播
                mMenuContentView.refreshPhoneLiveUI(menuDialogListener);//初始化手机直播
                mMenuContentView.refreshMessageUI(menuDialogListener);//初始化消息通知
                mMenuContentView.refreshCustomUI(mPlayInfoListener, menuDialogListener);//初始化自建频道
                mMenuContentView.refreshSettingUI();//初始化设置中心
            }
        }, 300);

        mHandler.sendEmptyMessage(WHAT_SYS_TIME);
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_SYS_TIME:
                    updateDateTime();
                    break;
            }
            return false;
        }
    });

    private void updateDateTime() {
        mHandler.removeMessages(WHAT_SYS_TIME);
        mTvTime.setText(DateFormat.format("HH:mm:ss", System.currentTimeMillis()));
        mHandler.sendEmptyMessageDelayed(WHAT_SYS_TIME, 1000);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        try {
            AppConfig.isMenuDialogEnable = false;
            AppConfig.isClickFengmi = false;
            mHandler.removeCallbacksAndMessages(null);
            mMenuContentView.dismiss();

        } catch (Exception e) {
            JLog.e(e);
        }
    }

    @Override
    public boolean dispatchKeyEvent(@NonNull KeyEvent event) {
        PlayEvent.PressKeyOnDialog key = new PlayEvent.PressKeyOnDialog(AppConfig.PRESS_CODE_SELECT_TV);
        key.isNeedKeep = MenuType.TV_LIVE != mLeftMenuView.getCurrentMenuType() || AppConfig.isClickFengmi;
        EventBus.getDefault().post(key);

        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (!AppConfig.isMenuDialogEnable) {
            //数据没加载前屏蔽按键
            return true;
        }
        //处理预约点播的事件，其他事件全部屏蔽
        if (mMenuContentView.getMenuOrderContentView().getVisibility() == View.VISIBLE) {
            boolean isHandle = mMenuContentView.getMenuOrderContentView().handleOnKeyDown(keyCode);
            if (isHandle) return true;
        }
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (mMenuContentView.isCanBack()) {
                    dismiss();
                }
                return true;

            case KeyEvent.KEYCODE_DPAD_UP:
            case KeyEvent.KEYCODE_DPAD_DOWN:
                if (mLeftMenuView.isHasFocus()) {
                    mLeftMenuView.handleUpOrDownFocus(keyCode);
                    return true;
                }

                if (mMenuContentView.isHasFocus()) {
                    boolean result = super.onKeyDown(keyCode, event);
                    mMenuContentView.handleUpOrDownFocus(mLeftMenuView.getCurrentMenuType(), keyCode, result);
                }

                return true;

            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (mMenuContentView.isHasFocus()) {
                    boolean isLoss = mMenuContentView.handleLeftFocus(mLeftMenuView.getCurrentMenuType());
                    if (isLoss) {//处理完成后是否丢失焦点
                        mLeftMenuView.handleGetFocus();
                    }
                }

                return true;

            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (mLeftMenuView.isHasFocus()) {
                    boolean isGetFocus = mMenuContentView.handleGetFocus(mLeftMenuView.getCurrentMenuType());//获取焦点
                    if (isGetFocus) {//处理完成后是否获取到焦点
                        mLeftMenuView.handleLossFocus();//失去焦点
                    }

                    return true;
                }

                if (mMenuContentView.isHasFocus()) {
                    mMenuContentView.handleRightFocus(mLeftMenuView.getCurrentMenuType());
                }

                return true;

            default:
                return super.onKeyDown(keyCode, event);
        }
    }

    /**
     * 左侧菜单切换监听
     */
    private OnMenuChangeListener menuChangeListener = new OnMenuChangeListener() {
        @Override
        public void onMenuChange(MenuType menuType) {
            mMenuContentView.switchContentByMenuType(menuType);
            if (menuType == MenuType.PHONE_LIVE || menuType == MenuType.TV_CUSTOM || menuType == MenuType.TV_SETTING) {
                mTvTime.setVisibility(View.VISIBLE);
            } else {
                mTvTime.setVisibility(View.GONE);
            }
            EventBus.getDefault().post(new PlayEvent.SelectMenuEvent(menuType));
        }
    };

    /**
     * 部分业务逻辑回调
     */
    private AbstractOnMenuDialogImpl menuDialogListener = new AbstractOnMenuDialogImpl() {
        @Override
        public void onDismiss() {
            dismiss();
        }

        @Override
        public void onGetLeftMenuFocus() {
            mLeftMenuView.handleGetFocus();
        }

        @Override
        public void onLossLeftMenuFocus() {
            mLeftMenuView.handleLossFocus();
        }

        @Override
        public void onToggleUnreadMsg(boolean hasUnread) {
            mLeftMenuView.toggleUnreadMsg(hasUnread);
        }

        @Override
        public void onGetBindQrCode() {
            getBindQrCode();
        }

        @Override
        public void onGetBindPhoneList() {
            getBindPhoneList();
        }

        @Override
        public void onUnBindPhone(int userId) {
            unBindPhone(userId);
        }

        @Override
        public void onGetCustomList() {
            getCustomList();
        }

        @Override
        public MenuType getCurrentMenuType() {
            return mLeftMenuView.getCurrentMenuType();
        }
    };

    public void setIsClickFengmi(boolean isSelect) {
        AppConfig.isClickFengmi = isSelect;
    }

    /**
     * 获取二维码
     */
    private void getBindQrCode() {
        addDisposable(ApiModule.getApiManager()
                .getTvQrCode()
                .subscribeWith(new DisposableObserver<String>() {
                    @Override
                    public void onNext(String value) {
                        mMenuContentView.loadQrCode(value);
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

    /**
     * 获取绑定手机的设备列表
     */
    public void getBindPhoneList() {
        addDisposable(ApiModule.getApiManager()
                .getBindDevices()
                .subscribeWith(new DisposableObserver<List<RespDevice>>() {
                    @Override
                    public void onNext(List<RespDevice> value) {
                        mMenuContentView.setDeviceList(value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        JLog.e(e);
                        if (e instanceof ApiException) {
                            ApiException exc = (ApiException) e;

                            if (ApiException.CODE_OK_RESULT_NULL == exc.code()) {
                                mMenuContentView.setDeviceList(null);
                            }
                        }
                    }

                    @Override
                    public void onComplete() {
                    }
                })
        );
    }

    /**
     * 解绑手机
     */
    private void unBindPhone(final int userId) {
        addDisposable(ApiModule.getApiManager()
                .unBindDevice(userId)
                .subscribeWith(new DisposableObserver<String>() {
                    @Override
                    public void onNext(String value) {
                        mMenuContentView.unBindPhone(userId, true);
                        getCustomList();
                    }

                    @Override
                    public void onError(Throwable e) {
                        JLog.e(e);
                        mMenuContentView.unBindPhone(userId, false);
                    }

                    @Override
                    public void onComplete() {
                    }
                })
        );
    }

    /**
     * 获取自建频道数据
     */
    public void getCustomList() {
        addDisposable(ApiModule.getApiManager()
                .getCustomList()
                .subscribeWith(new DisposableObserver<List<CustomTypeInfo>>() {
                    @Override
                    public void onNext(List<CustomTypeInfo> value) {
                        mMenuContentView.setCustomList(value);
                        if (null != customTypeListener) {
                            customTypeListener.getCustomList(value);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        JLog.e(e);
                        if (e instanceof ApiException) {
                            ApiException exc = (ApiException) e;

                            if (ApiException.CODE_OK_RESULT_NULL == exc.code()) {
                                mMenuContentView.setCustomList(null);
                                if (null != customTypeListener) {
                                    customTypeListener.getCustomList(null);
                                }
                            }
                        }
                    }

                    @Override
                    public void onComplete() {
                    }
                })
        );
    }

}
