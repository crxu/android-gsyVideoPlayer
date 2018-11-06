package com.gsgd.live.utils;

import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by bruce on 2017/2/10.
 */
public class AesUtils {

    public static final String DEVICE_KEY = "readyidu2015";
    /**
     * 密钥算法 java6支持56位密钥，bouncycastle支持64位
     */
    public static final String KEY_ALGORITHM = "AES";

    /**
     * 加密/解密算法/工作模式/填充方式
     * <p/>
     * JAVA6 支持PKCS5PADDING填充方式 Bouncy castle支持PKCS7Padding填充方式
     */
    public static final String CIPHER_ALGORITHM = "AES/ECB/PKCS7Padding";

    /**
     * 生成密钥，java6只支持56位密钥，bouncycastle支持64位密钥
     *
     * @return byte[] 二进制密钥
     */
    public static byte[] initkey(String string) throws Exception {
        return hexStringToBytes(string);
    }

    /**
     * 转换密钥
     *
     * @param key 二进制密钥
     * @return Key 密钥
     */
    public static Key toKey(byte[] key) throws Exception {
        // 实例化DES密钥
        // 生成密钥
        SecretKey secretKey = new SecretKeySpec(key, KEY_ALGORITHM);
        return secretKey;
    }

    /**
     * 加密数据
     *
     * @param data 待加密数据
     * @param key  密钥
     * @return byte[] 加密后的数据
     */
    public static byte[] encrypt(byte[] data, byte[] key) throws Exception {
        // 还原密钥
        Key k = toKey(key);
        /**
         * 实例化 使用 PKCS7PADDING 填充方式，按如下方式实现,就是调用bouncycastle组件实现
         * Cipher.getInstance(CIPHER_ALGORITHM,"BC")
         */

        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM, "BC");
        // 初始化，设置为加密模式
        cipher.init(Cipher.ENCRYPT_MODE, k);
        // 执行操作
        return cipher.doFinal(data);
    }

    /**
     * 解密数据
     *
     * @param data 待解密数据
     * @param key  密钥
     * @return byte[] 解密后的数据
     */
    public static byte[] decrypt(byte[] data, byte[] key) throws Exception {
        // 欢迎密钥
        Key k = toKey(key);
        /**
         * 实例化 使用 PKCS7PADDING 填充方式，按如下方式实现,就是调用bouncycastle组件实现
         * Cipher.getInstance(CIPHER_ALGORITHM,"BC")
         */
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        // 初始化，设置为解密模式
        cipher.init(Cipher.DECRYPT_MODE, k);
        // 执行操作
        return cipher.doFinal(data);
    }

    /**
     * AES 加密
     *
     * @param key
     * @param encodeString
     * @return
     */
    public static String aesEncodeMd5(String key, String encodeString) {
        String a = "";
        try {
            byte[] b_key = AesUtils.initkey(Md5(key).toLowerCase());
            byte[] data = AesUtils.encrypt(encodeString.getBytes(), b_key);
            for (int i = 0; i < data.length; i++) {
                String b = String.format("%x", data[i]);
                if (b.length() == 1) {
                    b = "0" + b;
                }
                a = a + b;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return a;
    }


    /**
     * AES 加密
     *
     * @param key
     * @param encodeString
     * @return
     */
    public static String aesEncode(String key, String encodeString) {
        String a = "";
        try {
            byte[] b_key = AesUtils.initkey(key.toLowerCase());
            byte[] data = AesUtils.encrypt(encodeString.getBytes(), b_key);
            for (int i = 0; i < data.length; i++) {
                String b = String.format("%x", data[i]);
                if (b.length() == 1) {
                    b = "0" + b;
                }
                a = a + b;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return a;
    }

    /**
     * 通过密钥解密
     *
     * @param key
     * @param decodeString
     * @return
     */
    public static String aesDecode(String key, String decodeString) {
        String a = "";
        byte[] data;
        try {
            byte[] b_key = AesUtils.initkey(Md5(key));
            data = AesUtils.decrypt(hexStringToBytes(decodeString), b_key);
            a = new String(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return a;
    }

    /**
     * @param args
     * @throws UnsupportedEncodingException
     * @throws Exception
     */
    public static void main(String[] args) throws UnsupportedEncodingException {
        String key = "884b2fbc1397c37a4f6fe951aa19679d";
        String string = "123546adfasdfsdf";
        String encodeString = aesEncode(key, string);
        String decodeString = aesDecode(key, encodeString);

        System.out.println("原字符串：" + string);
        System.out.println("加密：" + encodeString);
        System.out.println("解密：" + decodeString);
    }

    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    /**
     * Convert char to byte
     *
     * @param c char
     * @return byte
     */
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public static String Md5(String plainText) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte b[] = md.digest();

            int i;

            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0) i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            return buf.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}