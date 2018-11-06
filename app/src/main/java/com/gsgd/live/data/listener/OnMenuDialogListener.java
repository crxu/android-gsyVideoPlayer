package com.gsgd.live.data.listener;

import com.gsgd.live.ui.leftMenu.MenuType;

/**
 * @author zhangqy
 * @Description
 * @date 2017/12/23
 */
public interface OnMenuDialogListener {

    /**
     * 关闭对话框
     */
    void onDismiss();

    void onGetLeftMenuFocus();

    void onLossLeftMenuFocus();

    /**
     * 切换消息红点提示
     */
    void onToggleUnreadMsg(boolean hasUnread);

    /**
     * 获取绑定二维码
     */
    void onGetBindQrCode();

    /**
     * 获取绑定的设备列表
     */
    void onGetBindPhoneList();

    /**
     * 解绑手机
     */
    void onUnBindPhone(int userId);

    /**
     * 获取自建频道列表
     */
    void onGetCustomList();

    /**
     * 获取当前选中的菜单类型
     */
    MenuType getCurrentMenuType();
}
