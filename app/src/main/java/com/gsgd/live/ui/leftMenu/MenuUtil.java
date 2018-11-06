package com.gsgd.live.ui.leftMenu;

import android.view.View;

import hdpfans.com.R;

/**
 * @author zhangqy
 * @Description
 * @date 2017/12/22
 */
public final class MenuUtil {

    public static void toggleFocusBg(boolean isFocus, View view) {
        if (isFocus) {
            view.setBackgroundResource(R.drawable.bg_item_cover2);

        } else {
            view.setBackgroundResource(R.drawable.bg_item_cover3);
        }
    }

    /**
     * 是否滑动至中间
     *
     * @param direction 方向 true下滑
     */
    public static boolean isNeedScrollCenter(boolean direction, int size, int position) {
        if (size > 9) {
            if (direction) {
                //下滑
                if (position < 4) {
                    //最上面四个
                    return false;
                }

                if (position < size - 5) {
                    //中间需要滚动的
                    return true;
                }

                //最下面四个
                return false;

            } else {
                //上滑
                if (position < 5) {
                    //最上面四个
                    return false;
                }

                if (position > size - 5) {
                    //最下面四个
                    return false;
                }

                return true;
            }
        }

        return false;
    }

}
