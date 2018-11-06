package com.gsgd.live.data.model;

import com.gsgd.live.data.response.RespChannel;
import com.gsgd.live.data.response.RespChannelType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangqy
 * @Description
 * @date 2017/12/19
 */
public class ConfigInfo {

    public ConfigDetailInfo data;

    public class ConfigDetailInfo {

        public String version;
        public List<RespChannelType> typeList = new ArrayList<>();
        public List<RespChannel> channels = new ArrayList<>();
        public List<RespChannel> movieList = new ArrayList<>();//电影频道
    }

}
