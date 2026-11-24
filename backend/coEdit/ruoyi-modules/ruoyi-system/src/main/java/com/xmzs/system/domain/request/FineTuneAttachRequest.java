package com.xmzs.system.domain.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * packageName com.xmzs.system.domain.request
 *
 * @author zhuzi
 * @date 2024/7/22
 * @description TODO
 */
@Data
public class FineTuneAttachRequest {

    private String ftid;

    private MultipartFile file;
}
