package com.gsgd.live.utils;

import android.view.View;

public final class ScaleUtil {

    private static final int ANIM_TIME = 200;

    public static void scaleToBig(View view, float scale) {
        view.animate()
                .scaleY(scale)
                .setDuration(ANIM_TIME)
                .start();
    }

    public static void scaleToNormal(View view) {
        view.animate()
                .scaleY(1f)
                .setDuration(ANIM_TIME)
                .start();
    }

    public static void scaleToBig2(View view, float scale) {
        view.animate()
                .scaleX(scale)
                .scaleY(scale)
                .setDuration(ANIM_TIME)
                .start();
    }

    public static void scaleToNormal2(View view) {
        view.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(ANIM_TIME)
                .start();
    }

}
