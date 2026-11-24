package com.xmzs.common.localFile.engine;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * packageName com.xmzs.common.localFile.engine
 *
 * @author zhuzi
 * @date 2024/12/3
 * @description TODO
 */
@Data
@Component
@ConfigurationProperties("localfile")
public class LocalFileProperties {

    private String uploadPath;
}
