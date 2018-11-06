package com.gsgd.live.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public final class ImageLoadUtil {

    public static void loadGif(Context context, ImageView imageView, int resId) {
        Glide.with(context)
                .asGif()
                .load(resId)
                .into(imageView);
    }

    public static void clearImage(Context context, ImageView imageView) {
        Glide.with(context).clear(imageView);
    }

    public static void loadImage(Context context, ImageView imageView, String url) {
        Glide.with(context)
                .load(url)
                .into(imageView);
    }

}
