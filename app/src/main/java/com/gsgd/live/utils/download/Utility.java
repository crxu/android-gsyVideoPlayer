package com.gsgd.live.utils.download;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.jiongbull.jlog.JLog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public final class Utility {

    private final static String TAG = "Utility";

    /**
     * 判断是否存在外部存储
     */
    public static boolean isExternalStorageMounted() {
        boolean isHas = false;
        try {
            String state = Environment.getExternalStorageState();
            isHas = Environment.MEDIA_MOUNTED.equals(state);
        } catch (Exception e) {
            JLog.e(e);
        }
        return isHas;
    }

    public static boolean deleteAllFiles(String path) {
        if (TextUtils.isEmpty(path)) {
            return true;
        }

        File file = new File(path);
        if (!file.exists()) {
            return true;
        }
        if (file.isFile()) {
            return file.delete();
        }
        if (!file.isDirectory()) {
            return false;
        }
        for (File f : file.listFiles()) {
            if (f.isFile()) {
                f.delete();
            } else if (f.isDirectory()) {
                deleteAllFiles(f.getAbsolutePath());
            }
        }
        return file.delete();
    }

    /**
     * 获取当前版本名
     */
    public static String getVersionName(Context context) {
        StringBuffer sb = new StringBuffer();
        try {
            PackageManager manager = context.getPackageManager();
            String pkgName = context.getPackageName();
            PackageInfo info = manager.getPackageInfo(pkgName, 0);
            sb.append(info.versionName);// 版本名

        } catch (Exception e) {
            JLog.e(e);
        }
        return sb.toString();
    }

    /**
     * 获取版本号
     */
    public static int getVersionCode(Context context) {
        int verCode = 1;
        try {
            PackageManager manager = context.getPackageManager();
            String pkgName = context.getPackageName();
            PackageInfo info = manager.getPackageInfo(pkgName, 0);
            verCode = info.versionCode;// 开发版本名
        } catch (Exception e) {
            JLog.e(e);
        }
        return verCode;
    }

    public static boolean isValidFile(File file, String md5) {
        String fileMd5 = calculateMD5(file);
        JLog.d("******fileMd5:" + fileMd5);
        JLog.d("******md5:" + md5);

        return fileMd5.equalsIgnoreCase(md5);
    }

    private static String calculateMD5(File updateFile) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "Exception while getting digest", e);
            return null;
        }

        InputStream is;
        try {
            is = new FileInputStream(updateFile);

        } catch (Exception e) {
            Log.e(TAG, "Exception while getting FileInputStream", e);
            return null;
        }

        byte[] buffer = new byte[8192];
        int read;
        try {
            while ((read = is.read(buffer)) > 0) {
                digest.update(buffer, 0, read);
            }
            byte[] md5sum = digest.digest();
            BigInteger bigInt = new BigInteger(1, md5sum);
            String output = bigInt.toString(16);
            // Fill to 32 chars
            output = String.format("%32s", output).replace(' ', '0');
            return output;

        } catch (Exception e) {
            throw new RuntimeException("Unable to process file for MD5", e);

        } finally {
            try {
                is.close();

            } catch (Exception e) {
                Log.e(TAG, "Exception on closing MD5 input stream", e);
            }
        }
    }


    /**
     * 复制asset文件到指定目录
     *
     * @param oldPath asset下的路径
     * @param newPath SD卡下保存路径
     */
    public static void copyAssets(Context context, String oldPath, String newPath) {
        try {
            InputStream is = context.getAssets().open(oldPath);
            File file = new File(newPath);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(new File(newPath));
            byte[] buffer = new byte[1024];
            int byteCount;
            while ((byteCount = is.read(buffer)) != -1) {// 循环从输入流读取
                // buffer字节
                fos.write(buffer, 0, byteCount);// 将读取的输入流写入到输出流
            }
            fos.flush();// 刷新缓冲区
            is.close();
            fos.close();

        } catch (Exception e) {
            JLog.e(TAG, e);
        }
    }

    /**
     * 读取配置zip文件
     */
    public static String readConfigZipFile(String path) {
        try {
            ZipFile zf = new ZipFile(new File(path));
            Enumeration<?> entries = zf.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = ((ZipEntry) entries.nextElement());
                String entryName = entry.getName();

                if ("playChannel.json".equals(entryName)) {
                    InputStreamReader in = null;
                    BufferedReader br = null;

                    try {
                        in = new InputStreamReader(zf.getInputStream(entry));
                        br = new BufferedReader(in);

                        StringBuilder sb = new StringBuilder("");
                        String line;
                        while ((line = br.readLine()) != null) {
                            sb.append(line);
                        }

                        JLog.d(TAG, "******->" + sb.toString());

                        return sb.toString();

                    } finally {
                        CloseUtils.closeIO(br, in);
                    }
                }
            }

        } catch (Exception e) {
            JLog.e(TAG, e);
        }

        return null;
    }

    public static boolean deleteAllFiles(String path, List<String> nameList) {
        if (TextUtils.isEmpty(path)) {
            return true;
        }

        File file = new File(path);
        if (!file.exists()) {
            return true;
        }
        if (file.isFile() && !isInFileList(file.getName(), nameList)) {
            return file.delete();
        }
        if (!file.isDirectory()) {
            return false;
        }
        for (File f : file.listFiles()) {
            if (f.isFile() && !isInFileList(f.getName(), nameList)) {
                f.delete();
            }
        }

        return true;
    }

    private static boolean isInFileList(String name, List<String> nameList) {
        try {
            for (String s : nameList) {
                if (s.equals(name)) {
                    return true;
                }
            }

        } catch (Exception e) {
            JLog.e(TAG, e);
        }

        return false;
    }

}
