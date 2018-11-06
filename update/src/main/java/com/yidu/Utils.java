package com.yidu;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

/**
 * @author andy on 2018/1/23
 */

public class Utils {

    /**
     * 安装apk
     *
     * @param context
     * @param file    apk路径
     */
    public static void install(Context context, File file) {
        if (context == null || file == null || !file.exists()) {
            return;
        }
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static File createDir(@NonNull File dir, @NonNull String dirName) {
        if (TextUtils.isEmpty(dirName)) {
            dirName = "temp";
        }
        File file = new File(dir, dirName);
        if (!file.exists()) {
            file.mkdir();
        }
        return file;
    }

    public static void delete(File file) {
        if (file != null && file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else {
                for (File file1 : file.listFiles()) {
                    delete(file1);
                }
            }
        }
    }


    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
