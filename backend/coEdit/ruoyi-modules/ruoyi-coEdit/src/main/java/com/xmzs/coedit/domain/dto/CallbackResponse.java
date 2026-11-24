package com.xmzs.coedit.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * OnlyOffice回调响应DTO
 *
 * @author zuleed
 * @date 2025-11-04
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CallbackResponse {

    /**
     * 错误码
     * 0: 成功
     * 其他: 错误
     */
    private Integer error;

    /**
     * 错误消息
     */
    private String message;

    /**
     * 创建成功响应
     *
     * @return 成功响应
     */
    public static CallbackResponse success() {
        return CallbackResponse.builder()
            .error(0)
            .build();
    }

    /**
     * 创建错误响应
     *
     * @param message 错误消息
     * @return 错误响应
     */
    public static CallbackResponse error(String message) {
        return CallbackResponse.builder()
            .error(1)
            .message(message)
            .build();
    }
}

