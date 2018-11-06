package com.gsgd.live.utils;

import com.gsgd.live.AppConfig;
import com.gsgd.live.data.events.PlayEvent;
import com.gsgd.live.data.model.Channel;
import com.gsgd.live.data.model.ChannelType;
import com.gsgd.live.data.model.CustomInfo;
import com.gsgd.live.data.model.CustomTypeInfo;
import com.gsgd.live.data.response.RespSource;
import com.jiongbull.jlog.JLog;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * 播放控制工具类
 */
public final class PlayControlUtil {

    public interface PlayInfoListener {

        boolean isPlayCustom();

        ArrayList<ChannelType> getChannelTypeList();

        ChannelType getCurrentChannelType();

        Channel getCurrentChannel();

        RespSource getCurrentSource();

        void onPlayEnd();

        List<CustomTypeInfo> getCustomTypeList();

        CustomTypeInfo getCurrentCustomType();

        CustomInfo getCurrentCustomInfo();

    }

    public static int DIRECTION = 0;

    /**
     * 处理切台按键
     *
     * @param direction 0表示下，1表示上
     */
    public static void handlerPressSwitch(int direction, PlayInfoListener listener) {
        try {
            DIRECTION = direction;
            int mode_switch = ParamsUtil.getSwitchMode();
            if (direction == 0) {
                //下
                if (mode_switch == 0) {
                    //上一个台
                    calculationPosition(0, listener);

                } else {
                    //下一个台
                    calculationPosition(1, listener);
                }

            } else {
                //上
                if (mode_switch == 0) {
                    //下一个台
                    calculationPosition(1, listener);

                } else {
                    //上一个台
                    calculationPosition(0, listener);
                }
            }

        } catch (Exception e) {
            JLog.e(e);
        }
    }

    /**
     * 计算将要播放的台的位置
     *
     * @param direction 0表示上一个台，1表示下一个台
     */
    private static void calculationPosition(int direction, PlayInfoListener listener) {
        if (null != listener) {
            if (listener.isPlayCustom()) {
                calculationPositionCustom(direction, listener);

            } else {
                calculationPositionDefault(direction, listener);
            }
        }
    }

    /**
     * 计算将要播放的台的位置
     *
     * @param direction 0表示上一个台，1表示下一个台
     */
    private static void calculationPositionCustom(int direction, PlayInfoListener listener) {
        //切台
        List<CustomTypeInfo> customTypeList = listener.getCustomTypeList();
        CustomTypeInfo typeInfo = listener.getCurrentCustomType();
        CustomInfo customInfo = listener.getCurrentCustomInfo();

        //计算当前type位置
        int typeSize = customTypeList.size();
        int typePosition = 0;
        for (int i = 0; i < typeSize; i++) {
            if (typeInfo.userId == customTypeList.get(i).userId) {
                typePosition = i;
                break;
            }
        }

        //计算当前播放频道位置
        int size = typeInfo.sources.size();
        int position = 0;
        for (int i = 0; i < size; i++) {
            if (customInfo.sourceUrl.equals(typeInfo.sources.get(i).sourceUrl)) {
                position = i;
                break;
            }
        }

        JLog.d("******->当前栏目位置：" + typePosition + "||当前播放频道：" + position);

        if (direction == 0) {
            //上一个台
            position = position - 1;
            if (position < 0) {
                //切换到上一个栏目的最后一个频道
                typePosition = typePosition - 1;
                if (typePosition < 0) {
                    typePosition = typeSize - 1;
                }

                typeInfo = customTypeList.get(typePosition);
                position = typeInfo.sources.size() - 1;
            }

        } else {
            //下一个台
            position = position + 1;
            if (position >= size) {
                //切换到下一个栏目的第一个频道
                typePosition = typePosition + 1;
                if (typePosition >= typeSize) {
                    typePosition = 0;
                }

                typeInfo = customTypeList.get(typePosition);
                position = 0;
            }
        }

        customInfo = typeInfo.sources.get(position);

        JLog.d("******->将要播放栏目位置：" + typePosition + "||将要播放频道：" + position);
        EventBus.getDefault().post(new PlayEvent.SelectCustomChannelEvent(typeInfo, customInfo));
    }

    /**
     * 计算将要播放的台的位置
     *
     * @param direction 0表示上一个台，1表示下一个台
     */
    private static void calculationPositionDefault(int direction, PlayInfoListener listener) {
        ArrayList<ChannelType> channelTypes = listener.getChannelTypeList();
        ChannelType mCurrentType = listener.getCurrentChannelType();
        Channel mCurrentChannel = listener.getCurrentChannel();

        //计算当前栏目位置
        int typeSize = channelTypes.size();
        int typePosition = 0;
        for (int i = 0; i < typeSize; i++) {
            if (mCurrentType.id == channelTypes.get(i).id) {
                typePosition = i;
                break;
            }
        }

        //计算当前播放频道位置
        int size = mCurrentType.channels.size();
        int position = 0;
        for (int i = 0; i < size; i++) {
            if (mCurrentType.channels.get(i).id == mCurrentChannel.id) {
                position = i;
                break;
            }
        }

        JLog.d("******->当前栏目位置：" + typePosition + "||当前播放频道：" + position);

        if (direction == 0) {
            //上一个台
            position = position - 1;
            if (position < 0) {
                //切换到上一个栏目的最后一个频道
                typePosition = typePosition - 1;
                if (typePosition < 0) {
                    typePosition = typeSize - 1;
                }

                mCurrentType = channelTypes.get(typePosition);

                if (mCurrentType.channels.size() == 0) {
                    //防止我的收藏为空
                    typePosition = typePosition - 1;
                    if (typePosition < 0) {
                        typePosition = typeSize - 1;
                    }
                    mCurrentType = channelTypes.get(typePosition);
                }

                position = mCurrentType.channels.size() - 1;
            }

        } else {
            //下一个台
            position = position + 1;
            if (position >= size) {
                //切换到下一个栏目的第一个频道
                typePosition = typePosition + 1;
                if (typePosition >= typeSize) {
                    typePosition = 0;
                }

                mCurrentType = channelTypes.get(typePosition);

                if (mCurrentType.channels.size() == 0) {
                    //防止我的收藏为空
                    typePosition = typePosition + 1;
                    if (typePosition >= typeSize) {
                        typePosition = 0;
                    }
                    mCurrentType = channelTypes.get(typePosition);
                }

                position = 0;
            }
        }

        mCurrentChannel = mCurrentType.channels.get(position);

        if (mCurrentChannel.id == AppConfig.CUSTOM_TYPE_ADD_ID) {
            JLog.d("******->跳过添加源");
            if (direction == 0) {
                //上一个台,切到第一个台
                mCurrentType = channelTypes.get(channelTypes.size() - 2);
                mCurrentChannel = mCurrentType.channels.get(mCurrentType.channels.size() - 1);

            } else {
                //下一个台
                if (mCurrentType.channels.size() > 1) {
                    //有自定义源
                    mCurrentChannel = mCurrentType.channels.get(1);

                } else {
                    if (channelTypes.get(0).channels.size() > 0) {
                        //有收藏
                        mCurrentType = channelTypes.get(0);
                        mCurrentChannel = mCurrentType.channels.get(0);

                    } else {
                        //没有收藏
                        mCurrentType = channelTypes.get(1);
                        mCurrentChannel = mCurrentType.channels.get(0);
                    }
                }
            }
        }

        JLog.d("******->将要播放栏目位置：" + typePosition + "||将要播放频道：" + position);
        EventBus.getDefault().post(new PlayEvent.SelectChannelEvent(mCurrentType, mCurrentChannel));
    }

    /**
     * 切换播放源或者自动换台
     *
     * @param isCanChange 是否可以切台
     */
    public static void handlerPlayError(PlayInfoListener listener, boolean isCanChange) {
        if (null != listener) {
            if (listener.isPlayCustom()) {
                handlerPlayErrorCustom(listener, isCanChange);

            } else {
                handlerPlayErrorDefault(listener, isCanChange);
            }
        }
    }

    /**
     * 切换播放源或者自动换台
     *
     * @param isCanChange 是否可以切台
     */
    public static void handlerPlayErrorCustom(PlayInfoListener listener, boolean isCanChange) {
        try {
            //加载当前的
            if (null != listener) {
                listener.onPlayEnd();
            }
        } catch (Exception e) {
            JLog.e(e);
        }
    }

    /**
     * 切换播放源或者自动换台
     *
     * @param isCanChange 是否可以切台
     */
    public static void handlerPlayErrorDefault(PlayInfoListener listener, boolean isCanChange) {
        try {
            Channel mCurrentChannel = listener.getCurrentChannel();
            RespSource mCurrentSource = listener.getCurrentSource();

            int position = 0;
            int size = mCurrentChannel.sources.size();
            for (int i = 0; i < size; i++) {
                if (mCurrentSource.id == mCurrentChannel.sources.get(i).id) {
                    position = i;
                    break;
                }
            }
            position = position + 1;
            if (position > size - 1) {
                if (isCanChange) {
                    //切台
                    //ToastUtil.showToastLong("当前视频播放不流畅，正在为你自动换台！");
                    //下一个台
                    //calculationPosition(1, listener);

                    //从第一个源放
                    PlayEvent.ChangeSourceEvent sourceEvent = new PlayEvent.ChangeSourceEvent(mCurrentChannel, mCurrentChannel.sources.get(0));
                    sourceEvent.isNeedForcePlay = true;
                    EventBus.getDefault().post(sourceEvent);

                } else {
                    listener.onPlayEnd();
                }

            } else {
                //切源
                ToastUtil.showToastLong("当前视频播放不流畅，正在为你自动换源！");

                EventBus.getDefault().post(new PlayEvent.ChangeSourceEvent(mCurrentChannel, mCurrentChannel.sources.get(position)));
            }

        } catch (Exception e) {
            JLog.e(e);
        }
    }

}
