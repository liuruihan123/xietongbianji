package com.xmzs.ssologin.domain.request;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

/**
 * Google登录请求
 *
 * @author AI Assistant
 */
@Data
public class GoogleLoginRequest {

    /**
     * Google JWT凭证
     */
    @NotBlank(message = "Google凭证不能为空")
    private String credential;

    /**
     * Google客户端ID
     */
    @NotBlank(message = "Google客户端ID不能为空")
    private String clientId;

    /**
     * 邀请码（可选）
     */
    private String inviteCode;

    // 为了向后兼容，保留 idToken 字段的 getter
    public String getIdToken() {
        return credential;
    }

    public void setIdToken(String idToken) {
        this.credential = idToken;
    }
}
