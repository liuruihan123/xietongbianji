package com.xmzs.system.entity;

import lombok.Builder;
import lombok.Data;

/**
 * packageName com.xmzs.system.entity
 *
 * @author zhuzi
 * @date 2024/7/26
 * @description TODO
 */
@Data
@Builder
public class MsgResult<T> {

    /**
     * 是否成功
     */
    private boolean isSuccess;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 实际响应体
     * <p>
     * 可自行转换为 SDK 对应的
     */
    private T data;
}
