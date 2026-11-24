package com.xmzs.common.office.utils;


import cn.hutool.core.util.StrUtil;

import java.io.File;
import java.util.UUID;

/**
 * @Description :
 * @Author : zxy
 * @Date : 2023/10/30 14:36
 **/
public class OfficeUtils {
    public static void createDir(String dirPath) {
        File file = new File(dirPath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public static String dealTargetDir(String targetDir) {
        targetDir = targetDir == null ? "" : targetDir;
        if (StrUtil.isNotEmpty(targetDir)) {
            createDir(targetDir);
        }

        if (!targetDir.endsWith(File.separator)) {
            targetDir += File.separator;
        }
        return targetDir;
    }

    public static File checkFileExists(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new RuntimeException("file not exists ![filepath = " + filePath + "]");
        }

        return file;
    }

    public static String getUUID() {
        return UUID.randomUUID().toString();
    }
}
