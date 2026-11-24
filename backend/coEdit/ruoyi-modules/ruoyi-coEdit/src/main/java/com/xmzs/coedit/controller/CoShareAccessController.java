package com.xmzs.coedit.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.xmzs.coedit.domain.CoShareSecurity;
import com.xmzs.coedit.domain.bo.CoShareSecurityBo;
import com.xmzs.coedit.domain.vo.CoShareAccessRecordVo;
import com.xmzs.coedit.service.ICoShareAccessService;
import com.xmzs.coedit.service.ICoShareLinkService;
import com.xmzs.coedit.service.ICoShareSecurityService;
import com.xmzs.coedit.util.SharePermissionValidator;
import com.xmzs.common.core.domain.R;
import com.xmzs.common.log.annotation.Log;
import com.xmzs.common.log.enums.BusinessType;
import com.xmzs.common.mybatis.core.page.PageQuery;
import com.xmzs.common.mybatis.core.page.TableDataInfo;
import com.xmzs.common.web.core.BaseController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 共享访问控制器
 *
 * @author zuleed
 * @date 2025-11-04
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/coedit/share/access")
@Tag(name = "共享访问管理", description = "共享访问管理接口")
public class CoShareAccessController extends BaseController {

    private final ICoShareAccessService accessService;
    private final ICoShareSecurityService securityService;
    private final ICoShareLinkService shareLinkService;

    /**
     * 记录访问行为
     */
    @PostMapping("/record")
    @Operation(summary = "记录访问行为")
    public R<CoShareAccessRecordVo> recordAccess(
            @Parameter(description = "共享码", required = true) @RequestParam String shareCode,
            @Parameter(description = "访问操作", required = true) @RequestParam String action,
            @Parameter(description = "会话ID") @RequestParam(required = false) String sessionId,
            HttpServletRequest request) {

        // 获取访问者信息
        Long visitorId = null;
        String visitorName = null;
        try {
            if (StpUtil.isLogin()) {
//                visitorId = StpUtil.getLoginIdAsLong();
                visitorId = Long.valueOf(StpUtil.getLoginIdAsString().split(":")[1]);
                visitorName = StpUtil.getLoginIdAsString(); // 实际应该从用户信息中获取
            }
        } catch (Exception e) {
            // 匿名访问
        }

        // 获取IP和User-Agent
        String accessIp = getClientIP(request);
        String userAgent = request.getHeader("User-Agent");

        // 记录访问
        CoShareAccessRecordVo vo = accessService.recordAccess(
            shareCode, visitorId, visitorName, action, accessIp, userAgent, sessionId
        );

        return R.ok(vo);
    }

    /**
     * 查询共享链接的访问记录
     */
//    @SaCheckPermission("coedit:share:query")
    @GetMapping("/record/{shareCode}")
    @Operation(summary = "查询共享链接的访问记录")
    public TableDataInfo<CoShareAccessRecordVo> getAccessRecords(
            @Parameter(description = "共享码", required = true) @PathVariable String shareCode,
            PageQuery pageQuery) {

        return accessService.getAccessRecordsByShareCode(shareCode, pageQuery);
    }

    /**
     * 查询文件的访问记录
     */
//    @SaCheckPermission("coedit:share:query")
    @GetMapping("/record/file/{fileId}")
    @Operation(summary = "查询文件的访问记录")
    public TableDataInfo<CoShareAccessRecordVo> getFileAccessRecords(
            @Parameter(description = "文件ID", required = true) @PathVariable Long fileId,
            PageQuery pageQuery) {

        return accessService.getAccessRecordsByFileId(fileId, pageQuery);
    }

    /**
     * 查询我的访问记录
     */
    @GetMapping("/record/my")
    @Operation(summary = "查询我的访问记录")
    public TableDataInfo<CoShareAccessRecordVo> getMyAccessRecords(PageQuery pageQuery) {
//        Long visitorId = StpUtil.getLoginIdAsLong();
        Long visitorId = Long.valueOf(StpUtil.getLoginIdAsString().split(":")[1]);
        return accessService.getAccessRecordsByVisitorId(visitorId, pageQuery);
    }

    /**
     * 获取访问统计
     */
//    @SaCheckPermission("coedit:share:query")
    @GetMapping("/statistics/{shareCode}")
    @Operation(summary = "获取访问统计")
    public R<Map<String, Object>> getAccessStatistics(
            @Parameter(description = "共享码", required = true) @PathVariable String shareCode) {

        Map<String, Object> statistics = accessService.getAccessStatistics(shareCode);
        return R.ok(statistics);
    }

    /**
     * 获取文件访问统计
     */
//    @SaCheckPermission("coedit:share:query")
    @GetMapping("/statistics/file/{fileId}")
    @Operation(summary = "获取文件访问统计")
    public R<Map<String, Object>> getFileAccessStatistics(
            @Parameter(description = "文件ID", required = true) @PathVariable Long fileId) {

        Map<String, Object> statistics = accessService.getFileAccessStatistics(fileId);
        return R.ok(statistics);
    }

    /**
     * 检查操作权限
     */
//    @PostMapping("/permission/check")
    @Operation(summary = "检查操作权限")
    public R<Map<String, Object>> checkPermission(
            @Parameter(description = "共享码", required = true) @RequestParam String shareCode,
            @Parameter(description = "操作类型", required = true) @RequestParam String action,
            @Parameter(description = "访问密码") @RequestParam(required = false) String password,
            HttpServletRequest request) {

        Map<String, Object> result = new HashMap<>();

        // 检查共享链接是否有效
        Boolean isValid = shareLinkService.checkShareLinkValid(shareCode, password);
        if (!isValid) {
            result.put("hasPermission", false);
            result.put("message", "共享链接无效或密码错误");
            return R.ok(result);
        }

        // 获取共享链接信息
        var shareLink = shareLinkService.getShareLinkByCode(shareCode);
        if (shareLink == null) {
            result.put("hasPermission", false);
            result.put("message", "共享链接不存在");
            return R.ok(result);
        }

        // 检查IP是否被限制
        String accessIp = getClientIP(request);
        Boolean isRestricted = securityService.checkAccessRestricted(shareCode, accessIp);
        if (isRestricted) {
            result.put("hasPermission", false);
            result.put("message", "访问受限");
            return R.ok(result);
        }

        // 检查操作权限
        Boolean hasPermission = SharePermissionValidator.checkPermission(
            shareLink.getPermissionType(), action
        );

        result.put("hasPermission", hasPermission);
        result.put("message", hasPermission ? "有权限" : "无权限");
        result.put("permissionType", shareLink.getPermissionType());
        result.put("permissionTypeDesc", shareLink.getPermissionTypeDesc());

        return R.ok(result);
    }

    /**
     * 配置安全策略
     */
//    @SaCheckPermission("coedit:share:edit")
    @Log(title = "配置共享安全策略", businessType = BusinessType.INSERT)
    @PostMapping("/security/config")
    @Operation(summary = "配置安全策略")
    public R<CoShareSecurity> configSecurity(@Validated @RequestBody CoShareSecurityBo bo) {
        CoShareSecurity security = securityService.createSecurityConfig(bo);
        return R.ok(security);
    }

    /**
     * 获取安全配置
     */
//    @SaCheckPermission("coedit:share:query")
    @GetMapping("/security/{shareLinkId}")
    @Operation(summary = "获取安全配置")
    public R<CoShareSecurity> getSecurityConfig(
            @Parameter(description = "共享链接ID", required = true) @PathVariable Long shareLinkId) {

        CoShareSecurity security = securityService.getSecurityConfigByShareLinkId(shareLinkId);
        return R.ok(security);
    }

    /**
     * 更新安全配置
     */
//    @SaCheckPermission("coedit:share:edit")
    @Log(title = "更新共享安全策略", businessType = BusinessType.UPDATE)
    @PutMapping("/security/{shareLinkId}")
    @Operation(summary = "更新安全配置")
    public R<Void> updateSecurityConfig(
            @Parameter(description = "共享链接ID", required = true) @PathVariable Long shareLinkId,
            @Validated @RequestBody CoShareSecurityBo bo) {

        bo.setShareLinkId(shareLinkId);
        Boolean success = securityService.updateSecurityConfig(bo);
        return success ? R.ok() : R.fail("更新失败");
    }

    /**
     * 检查访问是否被限制
     */
    @PostMapping("/security/check")
    @Operation(summary = "检查访问是否被限制")
    public R<Map<String, Object>> checkAccessRestricted(
            @Parameter(description = "共享码", required = true) @RequestParam String shareCode,
            HttpServletRequest request) {

        String accessIp = getClientIP(request);
        Boolean isRestricted = securityService.checkAccessRestricted(shareCode, accessIp);

        Map<String, Object> result = new HashMap<>();
        result.put("isRestricted", isRestricted);
        result.put("message", isRestricted ? "访问受限" : "访问正常");

        return R.ok(result);
    }

    /**
     * 获取客户端IP地址
     */
    private String getClientIP(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

}

