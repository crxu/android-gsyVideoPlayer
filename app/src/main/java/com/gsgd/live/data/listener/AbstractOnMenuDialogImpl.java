package com.gsgd.live.data.listener;

import com.gsgd.live.ui.leftMenu.MenuType;

/**
 * @author zhangqy
 * @Description
 * @date 2017/12/23
 */
public abstract class AbstractOnMenuDialogImpl implements OnMenuDialogListener {

    @Override
    public void onDismiss() {

    }

    @Override
    public void onGetLeftMenuFocus() {

    }

    @Override
    public void onLossLeftMenuFocus() {

    }

    @Override
    public void onToggleUnreadMsg(boolean hasUnread) {

    }

    @Override
    public void onGetBindQrCode() {

    }

    @Override
    public void onGetBindPhoneList() {

    }

    @Override
    public void onUnBindPhone(int userId) {

    }

    @Override
    public void onGetCustomList() {

    }

    @Override
    public MenuType getCurrentMenuType() {
        return null;
    }

}
