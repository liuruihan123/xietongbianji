package com.xmzs.common.localFile.engine;

import cn.hutool.core.img.Img;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ThumUtil {
    private ThumUtil() {
    }
    /**
     * 获取Img流和目标地址转换jpg,返回InputStream
     */
    public static InputStream genThumToInputStream(InputStream inputStream, int width, int height) {
        try {
            BufferedImage buffer = ImageIO.read(inputStream);
            return genThumToInputStream(buffer, width, height);
        } catch (IOException e) {
            // IO流读取文件失败,
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取图片流转换Img,并进行裁剪,返回InputStream
     */
    public static InputStream genThumToInputStream(BufferedImage buffer, int width, int height) {
        try (ByteArrayOutputStream img = new ByteArrayOutputStream()) {
            Img.from(buffer)
                    .setPositionBaseCentre(false)
                    .scale(width, height)
                    .setTargetImageType("jpg")
                    .setQuality(1)
                    .write(img);
            return new ByteArrayInputStream(img.toByteArray());
        } catch (Exception e) {
            // IO流读取文件失败
            throw new RuntimeException(e);
        }
    }


    public static byte[] genThum(InputStream inputStream, int width, int height) {
        try {
            BufferedImage buffer = ImageIO.read(inputStream);
            return genThum(buffer, width, height);
        } catch (IOException e) {
            // IO流读取文件失败
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取图片流转换Img,并进行裁剪,返回InputStream
     */
    public static byte[] genThum(BufferedImage buffer, int width, int height) {
        try (ByteArrayOutputStream img = new ByteArrayOutputStream()) {
            Img.from(buffer)
                    .setPositionBaseCentre(false)
                    .scale(width, height)
                    .setTargetImageType("jpg")
                    .setQuality(1)
                    .write(img);
            return img.toByteArray();
        } catch (Exception e) {
            // IO流读取文件失败
            throw new RuntimeException(e);
        }
    }

    public static byte[] genThum(InputStream inputStream, float scale) {
        try {
            BufferedImage buffer = ImageIO.read(inputStream);
            return genThum(buffer, scale);
        } catch (IOException e) {
            // IO流读取文件失败
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取图片流转换Img,并进行裁剪,返回InputStream
     */
    public static byte[] genThum(BufferedImage buffer, float scale) {
        try (ByteArrayOutputStream img = new ByteArrayOutputStream()) {
            Img.from(buffer)
                    .setPositionBaseCentre(false)
                    .scale(scale)
                    .setTargetImageType("jpg")
                    .setQuality(1)
                    .write(img);
            return img.toByteArray();
        } catch (Exception e) {
            // IO流读取文件失败
            throw new RuntimeException(e);
        }
    }

}
