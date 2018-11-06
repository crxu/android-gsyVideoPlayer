package com.gsgd.live.utils.download;

import com.gsgd.live.AppConfig;
import com.jiongbull.jlog.JLog;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public final class FileUtils {

    public static boolean downloadFile(String srcUrl, String path, String fileName, String hash) {
        File file = new File(AppConfig.BASE_CONFIG_PATH, fileName);
        if (file.exists() && file.isFile() /*&& Utility.isValidFile(file, hash)*/) {
            JLog.d("文件已下载好");
            return true;

        } else {
            Utility.deleteAllFiles(path);
            //开始下载
            BufferedInputStream bin = null;
            OutputStream out = null;
            try {
                File temp = new File(path, "temp");
                if (!temp.getParentFile().exists()) {
                    temp.getParentFile().mkdirs();
                }
                temp.delete();

                URL url = new URL(srcUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                bin = new BufferedInputStream(connection.getInputStream());
                out = new FileOutputStream(temp);

                int len;
                byte[] buf = new byte[4 * 1024];
                while ((len = bin.read(buf)) != -1) {
                    out.write(buf, 0, len);
                }
                out.flush();
                JLog.d("文件下载完成");

                //删除已下载的
                Utility.deleteAllFiles(AppConfig.BASE_CONFIG_PATH);

                //copy
                if (!file.exists()) {
                    file.getParentFile().mkdirs();
                }
                temp.renameTo(file);

                //删除临时文件夹
                Utility.deleteAllFiles(path);

                return true;

            } catch (Exception e) {
                JLog.e(e);

            } finally {
                CloseUtils.closeIO(bin, out);
            }
        }

        return false;
    }

    public static boolean downloadCustomFile(String srcUrl, String path, String fileName, String hash) {
        File file = new File(AppConfig.BASE_CUSTOM_PATH, fileName);
        if (file.exists() && file.isFile() /*&& Utility.isValidFile(file, hash)*/) {
            JLog.d("文件已下载好");
            return true;

        } else {
            Utility.deleteAllFiles(path);
            //开始下载
            BufferedInputStream bin = null;
            OutputStream out = null;
            try {
                File temp = new File(path, "temp");
                if (!temp.getParentFile().exists()) {
                    temp.getParentFile().mkdirs();
                }
                temp.delete();

                URL url = new URL(srcUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                bin = new BufferedInputStream(connection.getInputStream());
                out = new FileOutputStream(temp);

                int len;
                byte[] buf = new byte[4 * 1024];
                while ((len = bin.read(buf)) != -1) {
                    out.write(buf, 0, len);
                }
                out.flush();
                JLog.d("文件下载完成");

                //copy
                if (!file.exists()) {
                    file.getParentFile().mkdirs();
                }
                temp.renameTo(file);

                return true;

            } catch (Exception e) {
                JLog.e(e);

            } finally {
                CloseUtils.closeIO(bin, out);
            }
        }

        return false;
    }

}
