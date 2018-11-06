package com.gsgd.live.data.listener;

import com.gsgd.live.data.model.CustomTypeInfo;

import java.util.List;

/**
 * @author zhangqy
 * @Description
 * @date 2018/1/2
 */
public interface OnGetCustomTypeListener {

    /**
     * 获取自定义列表
     */
    void getCustomList(List<CustomTypeInfo> value);
}
