package com.xmzs.system.domain.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * packageName com.xmzs.system.domain.request
 *
 * @author zhuzi
 * @date 2024/7/22
 * @description 通用上传附件
 */
@Data
public class UploadAttachRequest {

    private String id;

    private MultipartFile file;
}
