package com.xmzs.common.office;

import fr.opensagres.poi.xwpf.converter.xhtml.Base64EmbedImgManager;
import fr.opensagres.xdocreport.core.utils.Base64Utility;

import java.io.IOException;

/**
 * @ClassName : Base64ImgManager
 * @Description ：处理word中图片，使用base64
 * @Author : zxy
 * @Date : 2023/10/30 13:39
 * @Version : 1.0
 */
public class Base64ImgManager extends Base64EmbedImgManager {
    public static final String EMBED_IMG_SRC_PREFIX = "data:image/png;base64,";

    private byte[] picture;

    @Override
    public void extract(String imagePath, byte[] imageData) throws IOException {
        this.picture = imageData;
    }

    @Override
    public String resolve(String uri) {
        try {
            StringBuilder sb = (new StringBuilder(this.picture.length + EMBED_IMG_SRC_PREFIX.length())).append(EMBED_IMG_SRC_PREFIX).append(Base64Utility.encode(this.picture));
            return sb.toString();
        } catch (Exception e) {
            return super.resolve(uri);
        }
    }
}
