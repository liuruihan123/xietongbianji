package com.xmzs.system.util;


import com.xmzs.common.core.utils.StringUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Random;
import java.util.UUID;

/**
 * 常用工具集合
 */
public class ToolUtils {

    /**
     * 制作UUID
     *
     * @author fzr
     * @return String
     */
    public static String makeUUID(){
        return UUID.randomUUID().toString().replaceAll("-","").substring(0,32);
    }

    /**
     * 制作MD5
     *
     * @author fzr
     * @param data 需加密的数据
     * @return String
     */
    public static String makeMd5(String data){
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte [] array = md5.digest(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte item : array) {
                sb.append(Integer.toHexString((item & 0xFF) | 0x100), 1, 3);
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }



    /**
     * 返回随机字符串
     *
     * @author fzr
     * @param length 要生成的长度
     * @return String
     */
    public static String randomString(int length) {
        Random random = new Random();
        StringBuilder stringBuffer = new StringBuilder();
        String str = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        int strLength = str.length();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(strLength);
            stringBuffer.append(str.charAt(index));
        }
        return stringBuffer.toString();
    }

    /**
     * 返回随机数字字符串
     *
     * @author fzr
     * @param length 要生成的长度
     * @return String
     */
    public static String randomInt(int length) {
        Random random = new Random();
        StringBuilder stringBuffer = new StringBuilder();
        String str = "0123456789";
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(10);
            stringBuffer.append(str.charAt(index));
        }
        return stringBuffer.toString();
    }

    /**
     * 转换存储单位: KB MB GB TB
     *
     * @author fzr
     * @return String
     */
    public static String storageUnit(Long size) {
        if (size == null) {
            return "0B";
        }
        if (size < 1024) {
            return size + "B";
        } else {
            size = size / 1024;
        }
        if (size < 1024) {
            return size + "KB";
        } else {
            size = size / 1024;
        }
        if (size < 1024) {
            size = size * 100;
            return (size / 100) + "." + (size % 100) + "MB";
        } else {
            size = size * 100 / 1024;
            return (size / 100) + "." + (size % 100) + "GB";
        }
    }

    /**
     * 下载文件
     *
     * @author fzr
     * @param urlStr   (文件网址)
     * @param savePath (保存路径,如: /www/uploads/aa.png)
     * @throws IOException IO异常
     */
    public static void download(String urlStr, String savePath) throws IOException {
        ByteArrayOutputStream bos = null;
        FileOutputStream fos = null;
        InputStream inputStream = null;
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5*1000);
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            inputStream = conn.getInputStream();

            // 获取数组数据
            byte[] buffer = new byte[4*1024];
            int len;
            bos = new ByteArrayOutputStream();
            while((len = inputStream.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            byte[] getData = bos.toByteArray();

            // 新创建文件夹
            String fileName = StringUtils.substringAfterLast(savePath, "/");
            String path = savePath.replace("/"+fileName, "");
            File saveDir = new File(path);
            if(!saveDir.exists()) {
                if (!saveDir.mkdirs()) {
                    throw new IOException("创建存储文件夹失败");
                }
            }
            // 保存文件数据
            File file = new File(savePath);
            fos = new FileOutputStream(file);
            fos.write(getData);
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException ignored) {}
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ignored) {}
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ignored) {}
            }
        }
    }

    /**
     * 获取URL中的文件名
     * @param filePath 文件路径
     * @return 文件名
     */
    public static String extractFileName(String filePath) {
        // 获取最后一个斜杠的位置
        int lastIndexOfSlash = filePath.lastIndexOf("/");
        // 提取文件名
        if (lastIndexOfSlash != -1 && lastIndexOfSlash < filePath.length() - 1) {
            return filePath.substring(lastIndexOfSlash + 1);
        } else {
            return "default";
        }
    }

}
