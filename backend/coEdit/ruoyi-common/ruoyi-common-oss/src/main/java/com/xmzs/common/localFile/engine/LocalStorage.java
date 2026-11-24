package com.xmzs.common.localFile.engine;

import cn.hutool.core.io.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.module.FindException;

/**
 * 本地存储
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LocalStorage {

    private final LocalFileProperties localFileProperties;

    // 上传图片限制
    public static Integer uploadImageSize = 1024 * 1024 * 10;

    // 上传视频限制
    public static Integer uploadVideoSize = 1024 * 1024 * 30;

    // 上传图片扩展
    public static String[] uploadImageExt = new String[] {"png", "jpg", "jpeg", "gif", "ico", "bmp"};

    // 上传视频扩展
    public static String[] uploadVideoExt = new String[] {"mp4", "mp3", "avi", "flv", "rmvb", "mov"};


    /**
     * 本地上传
     *
     * @author fzr
     * @param multipartFile 上传对象
     * @param key 文件名称 20220331/122.jpg
     * @param folder 文件夹
     */
    public void upload(MultipartFile multipartFile, String key, String folder) {
        // 映射目录
        String directory = localFileProperties.getUploadPath();
        if (directory == null || directory.equals("")) {
            throw new FindException("请配置上传目录");
        }

        // 文件信息
        String saveName = key.split("/")[1];
        String savePath = (directory + folder + "/" + key.split("/")[0]).replace("\\", "/");

        System.out.println(savePath);

        // 创建目录
        File fileExist = new File(savePath);
        if (!fileExist.exists()) {
            if (!fileExist.mkdirs()) {
                throw new FindException("创建上传目录失败");
            }
        }

        BufferedOutputStream bos = null;
        FileOutputStream fileOutputStream = null;
        // 保存文件
        try {
            File dest = new File(savePath, saveName);
            fileOutputStream = new FileOutputStream(dest);
            bos = new BufferedOutputStream(fileOutputStream);
            bos.write(multipartFile.getBytes());
            bos.flush();
//            multipartFile.transferTo(dest);
        } catch (Exception e) {
            throw new FindException("上传文件失败:"+e.getMessage());
        } finally {
            try {
                if (bos != null) {
                    bos.close();
                    bos = null;
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                    fileOutputStream = null;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

    }

    /**
     * 将远程文件下载到本地存储
     *
     * @author pwl
     * @param data 远程文件
     * @param key 文件名称 20220331/122.jpg
     * @param folder 文件夹
     */
    public void upload(byte[] data, String key, String folder) {
        // 映射目录
        String directory = localFileProperties.getUploadPath();
        if (directory == null || directory.equals("")) {
            throw new FindException("请配置上传目录");
        }
        // 文件信息
        String saveName = key.split("/")[1];
        String savePath = (directory + folder + "/" + key.split("/")[0]).replace("\\", "/");

        log.info("存放路径：" + savePath);

        System.out.println("存放路径：" + savePath);

        // 创建目录
        File fileExist = new File(savePath);
        if (!fileExist.exists()) {
            if (!fileExist.mkdirs()) {
                throw new FindException("创建上传目录失败");
            }
        }

        // 保存文件
        try {
            File dest = new File(savePath, saveName);
            FileUtil.writeBytes(data, dest);
        } catch (Exception e) {
            throw new FindException("上传文件失败:"+e.getMessage());
        }

    }

}
