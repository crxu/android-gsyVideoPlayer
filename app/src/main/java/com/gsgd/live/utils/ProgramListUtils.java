package com.gsgd.live.utils;

import com.gsgd.live.data.model.ProgramInfo;

import java.util.List;

/**
 * @author zhangqy
 * @Description 节目列表工具类
 * @date 2018/1/25
 */
public final class ProgramListUtils {

    private static LRUCache<Long, List<ProgramInfo>> programList = new LRUCache<>(300);

    /**
     * 获取节目列表
     */
    public static List<ProgramInfo> getTodayProgramListById(long id) {
        return programList.get(id);
    }

    /**
     * 缓存节目列表
     */
    public static void setTodayProgramListById(long id, List<ProgramInfo> list) {
        programList.put(id, list);
    }

}
