package com.xmzs.coedit.controller;

import com.xmzs.coedit.config.OnlyOfficeConfig;
import com.xmzs.coedit.domain.dto.CallbackRequest;
import com.xmzs.coedit.domain.dto.CallbackResponse;
import com.xmzs.coedit.service.CallbackService;
import com.xmzs.coedit.utils.JwtTokenUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * OnlyOffice回调控制器
 *
 * @author zuleed
 * @date 2025-11-04
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/coedit/callback")
@Tag(name = "OnlyOffice回调接口", description = "处理OnlyOffice Document Server的回调请求")
public class CallbackController {

    private final CallbackService callbackService;
    private final JwtTokenUtil jwtTokenUtil;
    private final OnlyOfficeConfig onlyOfficeConfig;

    /**
     * 处理OnlyOffice回调
     *
     * @param fileId 文件ID
     * @param request 回调请求
     * @param authorization Authorization头
     * @return 回调响应
     */
    @Operation(summary = "处理OnlyOffice回调")
    @PostMapping("/{fileId}")
    public CallbackResponse handleCallback(
        @PathVariable String fileId,
        @RequestBody CallbackRequest request,
        @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        try {
            log.info("收到回调请求: fileId={}, status={}, key={}",
                fileId, request.getStatus(), request.getKey());

            // 验证JWT Token（如果启用）
            if (onlyOfficeConfig.getJwtEnabled()) {
                if (!validateToken(request, authorization)) {
                    log.error("JWT Token验证失败: fileId={}", fileId);
                    return CallbackResponse.error("JWT Token验证失败");
                }
            }

            // 处理回调
            return callbackService.handleCallback(fileId, request);

        } catch (Exception e) {
            log.error("处理回调异常: fileId={}", fileId, e);
            return CallbackResponse.error("处理回调异常: " + e.getMessage());
        }
    }

    /**
     * 处理分享链接的OnlyOffice回调
     *
     * @param shareCode 分享码
     * @param request 回调请求
     * @param authorization Authorization头
     * @return 回调响应
     */
    @Operation(summary = "处理分享链接的OnlyOffice回调")
    @PostMapping("/share/{shareCode}")
    public CallbackResponse handleShareCallback(
        @PathVariable String shareCode,
        @RequestBody CallbackRequest request,
        @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        try {
            log.info("收到分享链接回调请求: shareCode={}, status={}, key={}",
                shareCode, request.getStatus(), request.getKey());

            // 验证JWT Token（如果启用）
            if (onlyOfficeConfig.getJwtEnabled()) {
                if (!validateToken(request, authorization)) {
                    log.error("JWT Token验证失败: shareCode={}", shareCode);
                    return CallbackResponse.error("JWT Token验证失败");
                }
            }

            // 处理分享链接回调
            return callbackService.handleShareCallback(shareCode, request);

        } catch (Exception e) {
            log.error("处理分享链接回调异常: shareCode={}", shareCode, e);
            return CallbackResponse.error("处理回调异常: " + e.getMessage());
        }
    }

    /**
     * 验证JWT Token
     *
     * @param request 回调请求
     * @param authorization Authorization头
     * @return 是否验证通过
     */
    private boolean validateToken(CallbackRequest request, String authorization) {
        try {
            // 从请求体中获取token
            String token = request.getToken();

            // 如果请求体中没有token，从Authorization头中获取
            if (token == null && authorization != null) {
                token = jwtTokenUtil.extractToken(authorization);
            }

            // 验证token
            if (token != null) {
                return jwtTokenUtil.validateToken(token);
            }

            log.warn("未找到JWT Token");
            return false;

        } catch (Exception e) {
            log.error("验证JWT Token失败", e);
            return false;
        }
    }
}

