package com.gsgd.live.data.model;

import com.gsgd.live.data.response.RespChannel;
import com.gsgd.live.data.response.RespPlayBill;

import java.util.ArrayList;
import java.util.List;

public class ChannelList {

    public List<ChannelInfo> channelInfoList = new ArrayList<>();

    public class ChannelInfo {

        public RespChannel channel;
        public RespPlayBill playBill;
    }

}
