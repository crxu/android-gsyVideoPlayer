package com.gsgd.live.data.listener;

import com.gsgd.live.data.model.Channel;

/**
 * @author zhangqy
 * @Description 频道监听接口
 * @date 2017/10/26
 */
public interface ChannelListener {

    /**
     * @param type    1 删除自定义源；2 添加收藏；3 取消收藏
     * @param channel
     */
    void optChannel(int type, Channel channel);
}
