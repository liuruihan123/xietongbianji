package com.xmzs.common.core.domain.model;

import lombok.Data;

import java.io.Serializable;

/**
 * packageName com.xmzs.common.core.domain.model
 *
 * @author zhuzi
 * @date 2024/11/27
 * @description TODO
 */
@Data
public class LoginQrCodeVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer expireSeconds;

    private String url;

    private String key;

    private String ticket;
}
