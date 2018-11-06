package com.gsgd.live.data.events;

import com.gsgd.live.data.model.Channel;
import com.gsgd.live.data.model.ChannelType;
import com.gsgd.live.data.model.CustomInfo;
import com.gsgd.live.data.model.CustomTypeInfo;
import com.gsgd.live.data.response.RespSource;
import com.gsgd.live.ui.leftMenu.MenuType;

public class PlayEvent {

    /**
     * 切换播放地址
     */
    public static class ChangeSourceEvent {

        public Channel channel;
        public Channel subChannel;
        public RespSource source;
        public boolean isNeedForcePlay = false;
        public boolean isPlayCustom = false;

        public ChangeSourceEvent(Channel channel, RespSource source) {
            this.channel = channel;
            this.source = source;
        }

        public void setNeedForcePlay(boolean isNeedForcePlay) {
            this.isNeedForcePlay = isNeedForcePlay;
        }
    }

    /**
     * 选择某频道
     */
    public static class SelectChannelEvent {

        public ChannelType channelType;
        public Channel channel;
        public Channel subChannel;
        public boolean isClickDialog;

        public SelectChannelEvent(ChannelType channelType, Channel channel) {
            this.channelType = channelType;
            this.channel = channel;
        }

        public void setClickDialog(boolean isClickDialog) {
            this.isClickDialog = isClickDialog;
        }
    }

    /**
     * 选择自建频道
     */
    public static class SelectCustomChannelEvent {

        public CustomTypeInfo typeInfo;
        public CustomInfo info;

        public SelectCustomChannelEvent(CustomTypeInfo typeInfo, CustomInfo info) {
            this.typeInfo = typeInfo;
            this.info = info;
        }
    }

    /**
     * 在弹框界面按了按键
     */
    public static class PressKeyOnDialog {

        public int type;
        public boolean isNeedKeep;

        public PressKeyOnDialog(int type) {
            this.type = type;
        }
    }

    /**
     * 显示添加源界面
     */
    public static class ShowAddSourceDialog {

        public ShowAddSourceDialog() {
        }
    }

    /**
     * 保存源信息
     */
    public static class SaveSourceEvent {

        public String name;
        public String source;

        public SaveSourceEvent(String name, String source) {
            this.name = name;
            this.source = source;
        }
    }

    /**
     * 显示是否删除源界面
     */
    public static class ShowDeleteSourceDialog {

        public Channel channel;

        public ShowDeleteSourceDialog(Channel channel) {
            this.channel = channel;
        }
    }

    /**
     * 删除源
     */
    public static class DeleteSourceEvent {

        public Channel channel;

        public DeleteSourceEvent(Channel channel) {
            this.channel = channel;
        }
    }

    /**
     * 刷新缓存
     */
    public static class RefreshCache {

    }

    /**
     * 清除缓存
     */
    public static class ResetEvent {

    }

    /**
     * 选择屏幕缩放模式
     */
    public static class SelectScaleEvent {

        public int type;

        public SelectScaleEvent(int type) {
            this.type = type;
        }
    }

    /**
     * 选择左侧菜单
     */
    public static class SelectMenuEvent {

        public MenuType menuType;

        public SelectMenuEvent(MenuType menuType) {
            this.menuType = menuType;
        }
    }

    /**
     * 选择蜂蜜
     */
    public static class SelectFenMiEvent {

        public SelectFenMiEvent() {
        }
    }

    /**
     * 绑定设备
     */
    public static class BindDeviceEvent {

    }

    /**
     * 同步自建源
     */
    public static class SynCustomEvent {

    }

}
