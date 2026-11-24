package com.xmzs.common.localFile.engine;

import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.lang.module.FindException;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class StorageDriver {


    private final  LocalStorage local;


    /**
     * 根据引擎类型上传文件
     *
     * @author fzr
     * @param multipartFile 文件对象
     * @param folder 文件夹
     * @param type 类型: 10=图片, 20=视频
     * @return UploadFilesVo
     */
    public UploadFilesVo upload(MultipartFile multipartFile, String folder, Integer type) {
        this.checkFile(multipartFile, type);
        String key = this.buildSaveName(multipartFile);
        UploadFilesVo vo = new UploadFilesVo();
//        LocalStorage local = new LocalStorage();
        local.upload(multipartFile, key, folder);
        String origFileName = Objects.requireNonNull(multipartFile.getOriginalFilename());
        String origFileExt  = origFileName.substring(origFileName.lastIndexOf(".")).replace(".", "");
        String newFileName  = folder + "/" + key;

        vo.setId(0);
        vo.setName(multipartFile.getOriginalFilename());
        vo.setSize(multipartFile.getSize());
        vo.setExt(origFileExt.toLowerCase());
        vo.setUrl(newFileName);
        vo.setPath(UrlUtils.toAbsoluteUrl(newFileName));
        return vo;
    }

    public File MultipartFileToFile(MultipartFile multiFile) {
        // 获取文件名
        String fileName = multiFile.getOriginalFilename();
        // 获取文件后缀
        String prefix = fileName.substring(fileName.lastIndexOf("."));
        // 若须要防止生成的临时文件重复,能够在文件名后添加随机码

        try {
            File file = File.createTempFile(fileName, prefix);
            multiFile.transferTo(file);
            return file;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据引擎类型上传文件
     *
     * @author fzr
     * @return UploadFilesVo
     */
    public UploadFilesVo upload(byte[] data, String folder, String fileName) {

        folder = folder.endsWith(File.separator) ? folder : folder + File.separator;

        String key = buildSaveNameByKey(fileName);
        local.upload(data, key, folder);

        String origFileName = key;
        String origFileExt  = origFileName.substring(origFileName.lastIndexOf(".")).replace(".", "");
        String newFileName  = folder + key;

        UploadFilesVo vo = new UploadFilesVo();
        vo.setId(0);
        vo.setName(origFileName);
        vo.setSize((long) data.length);
        vo.setExt(origFileExt.toLowerCase());
        vo.setUrl(newFileName);
        vo.setPath(UrlUtils.toAbsoluteUrl(newFileName));
        return vo;
    }


    /**
     * 根据引擎类型上传文件
     *
     * @author pwl
     * @param url 远程文件路径
     * @param folder 文件夹
     * @return UploadFilesVo
     */
    public UploadFilesVo downloadImage(String url, String folder) {
        String key = this.buildSaveName(url);
        String keyThum = this.buildSaveThumbnailName(url);
        byte[] data = HttpUtil.downloadBytes(url);
        ByteArrayInputStream bais = null;
        try {
            bais = new ByteArrayInputStream(data);
            BufferedImage scrImg = ImageIO.read(bais);
            float width = scrImg.getWidth(null);
            float scale = 450 / width;
            final byte[] thumData = ThumUtil.genThum(scrImg, scale);
            local.upload(data, key, folder);
            local.upload(thumData, keyThum, folder);

            String origFileName = Objects.requireNonNull(FileUtil.getName(url));
            String origFileExt  = origFileName.substring(origFileName.lastIndexOf(".")).replace(".", "");
            String newFileName  = folder + "/" + key;
            String newThumFileName = folder + "/" + keyThum;

            UploadFilesVo vo = new UploadFilesVo();
            vo.setId(0);
            vo.setName(origFileName);
            vo.setExt(origFileExt.toLowerCase());
            vo.setUrl(newFileName);
            vo.setPath(UrlUtils.toAbsoluteUrl(newFileName));
            vo.setThumbnailUrl(newThumFileName);
            vo.setThumbnailPath(UrlUtils.toAbsoluteUrl(newThumFileName));
            return vo;

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                bais.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 根据引擎类型上传文件
     *
     * @author pwl
     * @param url 远程文件路径
     * @param folder 文件夹
     * @return UploadFilesVo
     */
    public ThumFilesVo generateThumbnail(String url, String folder) {
        String keyThum = this.buildSaveThumbnailName(url);
        byte[] data = HttpUtil.downloadBytes(url);
        ByteArrayInputStream bais = null;
        try {
            bais = new ByteArrayInputStream(data);
            BufferedImage scrImg = ImageIO.read(bais);
            float width = scrImg.getWidth(null);
            float scale = 450 / width;
            final byte[] thumData = ThumUtil.genThum(scrImg, scale);
            local.upload(thumData, keyThum, folder);
            String origFileName = Objects.requireNonNull(FileUtil.getName(url));
            String origFileExt  = origFileName.substring(origFileName.lastIndexOf(".")).replace(".", "");
            String newThumFileName = folder + "/" + keyThum;

            ThumFilesVo vo = new ThumFilesVo();
            vo.setId(0);
            vo.setName(origFileName);
            vo.setExt(origFileExt.toLowerCase());
            vo.setThumbnailUrl(newThumFileName);
            vo.setThumbnailPath(UrlUtils.toAbsoluteUrl(newThumFileName));
            return vo;

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                bais.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    /**
     * 生成文件名称
     *
     * @author fzr
     * @param multipartFile 文件对象
     * @return String
     */
    private String buildSaveName(MultipartFile multipartFile) {
        String name = multipartFile.getOriginalFilename();
        String ext  = Objects.requireNonNull(name).substring(name.lastIndexOf("."));
        String date = TimeUtils.timestampToDate(TimeUtils.timestamp(), "yyyyMMdd");
        return date + "/" + UUID.randomUUID() + ext.toLowerCase();
    }

    /**
     * 生成文件缩略图名称
     *
     * @author fzr
     * @param multipartFile 文件对象
     * @return String
     */
    private String buildThumSaveName(MultipartFile multipartFile) {
        String name = multipartFile.getOriginalFilename();
        String ext  = Objects.requireNonNull(name).substring(name.lastIndexOf("."));
        String date = TimeUtils.timestampToDate(TimeUtils.timestamp(), "yyyyMMdd");
        return date + "/" + UUID.randomUUID() + "_thum" + ext.toLowerCase();
    }

    /**
     * 生成文件名称
     *
     * @author fzr
     * @param key 文件对象
     * @return String
     */
    private String buildSaveNameByKey(String key) {
        String ext  = Objects.requireNonNull(key).substring(key.lastIndexOf("."));
        String date = TimeUtils.timestampToDate(TimeUtils.timestamp(), "yyyyMMdd");
        return date + "/" + UUID.randomUUID() + ext.toLowerCase();
    }

    /**
     * 生成文件名称
     *
     * @author fzr
     * @param url 文件对象
     * @return String
     */
    private String buildSaveName(String url) {
        String name = FileUtil.getName(url);
        String ext  = "." + FileUtil.extName(name);
        if (ext.indexOf("?") != -1) {
            ext = ext.substring(0, ext.indexOf("?"));
        }
        String date = TimeUtils.timestampToDate(TimeUtils.timestamp(), "yyyyMMdd");
        return date + "/" + UUID.randomUUID() + ext.toLowerCase();
    }


    /**
     * 生成缩略图文件名称
     *
     * @author fzr
     * @param url 文件对象
     * @return String
     */

    private String buildSaveThumbnailName(String url) {
        String name = FileUtil.getName(url);
        String ext  = "." + FileUtil.extName(name);
        if (ext.indexOf("?") != -1) {
            ext = ext.substring(0, ext.indexOf("?"));
        }
        String date = TimeUtils.timestampToDate(TimeUtils.timestamp(), "yyyyMMdd");
        return date + "/" + UUID.randomUUID() + "_thumbnail" + ext.toLowerCase();
    }


    /**
     * 文件验证
     *
     * @author fzr
     * @param multipartFile 文件对象
     * @param type 类型: 10=图片, 20=视频
     */
    private void checkFile(MultipartFile multipartFile, Integer type) {
        String fileName = Objects.requireNonNull(multipartFile.getOriginalFilename());
        String fileExt  = fileName.substring(fileName.lastIndexOf(".")).replace(".", "").toLowerCase();
        long fileSize   = multipartFile.getSize();

        if (type == 10) {
            if (!Arrays.asList(local.uploadImageExt).contains(fileExt)) {
                throw new FindException("不被支持的扩展:" + fileExt);
            }
            if (fileSize > local.uploadImageSize) {
                throw new FindException("上传图片不能超出限制:" + (local.uploadImageSize / 1024 / 1024) + "M");
            }
        } else if (type == 20) {
            if (!Arrays.asList(local.uploadVideoExt).contains(fileExt)) {
                throw new FindException("不被支持的扩展:" + fileExt);
            }
            if (fileSize > local.uploadVideoSize) {
                throw new FindException("上传视频不能超出限制:" + (local.uploadVideoSize / 1024 / 1024) + "M");
            }
        }
    }

}
