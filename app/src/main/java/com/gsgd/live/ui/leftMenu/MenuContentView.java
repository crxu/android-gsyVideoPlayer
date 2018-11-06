package com.gsgd.live.ui.leftMenu;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.gsgd.live.data.listener.AbstractOnMenuDialogImpl;
import com.gsgd.live.data.listener.ChannelListener;
import com.gsgd.live.data.model.CustomTypeInfo;
import com.gsgd.live.data.response.RespDevice;
import com.gsgd.live.utils.PlayControlUtil;
import com.jiongbull.jlog.JLog;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import hdpfans.com.R;

/**
 * @author zhangqy
 * @Description 菜单内容
 * @date 2017/12/22
 */
public class MenuContentView extends FrameLayout {

    @BindView(R.id.view_tv_live_content)
    MenuTLContentView mMenuTLContentView;
    @BindView(R.id.view_phone_live_content)
    MenuPLContentView mMenuPLContentView;
    @BindView(R.id.view_message_content)
    MenuMContentView mMenuMContentView;
    @BindView(R.id.view_custom_content)
    MenuCContentView mMenuCContentView;
    @BindView(R.id.view_setting_content)
    MenuSContentView mMenuSContentView;
    @BindView(R.id.view_order_play_content)
    MenuOrderContentView mMenuOrderContentView;

    public MenuContentView(@NonNull Context context) {
        this(context, null);
    }

    public MenuContentView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.view_menu_content, this);
        ButterKnife.bind(this, this);
        mMenuTLContentView.setMenuOrderContentView(mMenuOrderContentView);
    }

    /**
     * 切换内容区域
     *
     * @param menuType 菜单类型
     */
    public void switchContentByMenuType(MenuType menuType) {
        try {
            mMenuTLContentView.setVisibility(MenuType.TV_LIVE == menuType ? VISIBLE : GONE);
            mMenuPLContentView.setVisibility(MenuType.PHONE_LIVE == menuType ? VISIBLE : GONE);
            mMenuMContentView.setVisibility(MenuType.TV_MESSAGE == menuType ? VISIBLE : GONE);
            mMenuCContentView.setVisibility(MenuType.TV_CUSTOM == menuType ? VISIBLE : GONE);
            mMenuSContentView.setVisibility(MenuType.TV_SETTING == menuType ? VISIBLE : GONE);

        } catch (Exception e) {
            JLog.e(e);
        }
    }

    public void refreshTvLiveUI(PlayControlUtil.PlayInfoListener infoListener, ChannelListener listener, AbstractOnMenuDialogImpl menuDialogListener) {
        mMenuTLContentView.initData(infoListener, listener, menuDialogListener);//初始化电视直播
        mMenuTLContentView.refreshUI();
    }

    public void refreshPhoneLiveUI(AbstractOnMenuDialogImpl menuDialogListener) {
        mMenuPLContentView.initData(menuDialogListener);
    }

    public void refreshMessageUI(AbstractOnMenuDialogImpl menuDialogListener) {
        mMenuMContentView.initData(menuDialogListener);
    }

    public void refreshCustomUI(PlayControlUtil.PlayInfoListener infoListener, AbstractOnMenuDialogImpl menuDialogListener) {
        mMenuCContentView.initData(infoListener, menuDialogListener);
    }

    public void refreshSettingUI() {
        mMenuSContentView.refreshUI();
    }

    /**
     * 是否可以返回
     */
    public boolean isCanBack() {
        return mMenuSContentView.isCanBack();
    }

    /**
     * 是否有焦点
     */
    public boolean isHasFocus() {
        return mMenuTLContentView.isHasFocus()
                || mMenuPLContentView.isHasFocus()
                || mMenuMContentView.isHasFocus()
                || mMenuCContentView.isHasFocus()
                || mMenuSContentView.isHasFocus();
    }

    /**
     * 处理向上或向下事件
     */
    public void handleUpOrDownFocus(MenuType menuType, int keyCode, boolean result) {
        switch (menuType) {
            case TV_LIVE:
                mMenuTLContentView.handleUpOrDownFocus(keyCode, result);
                break;

            case PHONE_LIVE:
                mMenuPLContentView.handleUpOrDownFocus(keyCode);
                break;

            case TV_MESSAGE:
                mMenuMContentView.handleUpOrDownFocus(keyCode, result);
                break;

            case TV_CUSTOM:
                mMenuCContentView.handleUpOrDownFocus(keyCode);
                break;

            case TV_SETTING:
                mMenuSContentView.handleUpOrDownFocus(keyCode);
                break;
        }
    }

    /**
     * 重新获取焦点
     *
     * @return 是否获取到焦点
     */
    public boolean handleGetFocus(MenuType menuType) {
        switch (menuType) {
            case TV_LIVE:
                return mMenuTLContentView.handleGetFocus();

            case PHONE_LIVE:
                return mMenuPLContentView.handleGetFocus();

            case TV_MESSAGE:
                return mMenuMContentView.handleGetFocus();

            case TV_CUSTOM:
                return mMenuCContentView.handleGetFocus();

            case TV_SETTING:
                return mMenuSContentView.handleGetFocus();
        }

        return false;
    }

    /**
     * 处理向左的焦点
     */
    public boolean handleLeftFocus(MenuType menuType) {
        switch (menuType) {
            case TV_LIVE:
                return mMenuTLContentView.handleLeftFocus();

            case PHONE_LIVE:
                return mMenuPLContentView.handleLeftFocus();

            case TV_MESSAGE:
                return mMenuMContentView.handleLeftFocus();

            case TV_CUSTOM:
                return mMenuCContentView.handleLeftFocus();

            case TV_SETTING:
                return mMenuSContentView.handleLeftFocus();
        }

        return false;
    }

    /**
     * 处理向右的焦点
     */
    public void handleRightFocus(MenuType menuType) {
        switch (menuType) {
            case TV_LIVE:
                mMenuTLContentView.handleRightFocus();
                break;

            case PHONE_LIVE:
                mMenuPLContentView.handleRightFocus();
                break;

            case TV_MESSAGE:
                mMenuMContentView.handleRightFocus();
                break;

            case TV_CUSTOM:
                mMenuCContentView.handleRightFocus();
                break;

            case TV_SETTING:
                mMenuSContentView.handleRightFocus();
                break;
        }
    }

    public MenuOrderContentView getMenuOrderContentView() {
        return mMenuOrderContentView;
    }

    public void loadQrCode(String value) {
        mMenuPLContentView.loadQrCode(value);
    }

    public void setDeviceList(List<RespDevice> value) {
        mMenuPLContentView.setDeviceList(value);
    }

    public void unBindPhone(int userId, boolean isSuccess) {
        mMenuPLContentView.unBindPhone(userId, isSuccess);
    }

    public void setCustomList(List<CustomTypeInfo> value) {
        mMenuCContentView.setCustomList(value);
    }

    public void dismiss() {
        try {
            mMenuPLContentView.dismiss();
            mMenuSContentView.dismiss();

        } catch (Exception e) {
            JLog.e(e);
        }
    }
}
