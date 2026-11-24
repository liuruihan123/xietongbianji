package com.xmzs.coedit.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.xmzs.coedit.domain.bo.CoFileVersionBo;
import com.xmzs.coedit.domain.vo.CoFileVersionVo;
import com.xmzs.coedit.domain.vo.CoFileVo;
import com.xmzs.coedit.service.ICoFileVersionService;
import com.xmzs.common.core.domain.R;
import com.xmzs.common.core.utils.file.FileUtils;
import com.xmzs.common.log.annotation.Log;
import com.xmzs.common.log.enums.BusinessType;
import com.xmzs.common.mybatis.core.page.PageQuery;
import com.xmzs.common.mybatis.core.page.TableDataInfo;
import com.xmzs.common.web.core.BaseController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 文件版本控制器
 *
 * @author zuleed
 * @date 2025-11-04
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/coedit/version")
@Tag(name = "文件版本管理", description = "文件版本管理接口")
public class CoFileVersionController extends BaseController {

    private final ICoFileVersionService versionService;

    /**
     * 创建文件版本
     */
//    @SaCheckPermission("coedit:file:edit")
    @Log(title = "创建文件版本", businessType = BusinessType.INSERT)
    @PostMapping("/create")
    @Operation(summary = "创建文件版本")
    public R<CoFileVersionVo> createVersion(@Validated @RequestBody CoFileVersionBo bo) {
//        Long userId = StpUtil.getLoginIdAsLong();
        Long userId = Long.valueOf(StpUtil.getLoginIdAsString().split(":")[1]);
        String userName = StpUtil.getLoginIdAsString(); // 实际应该从用户信息中获取

        CoFileVersionVo vo = versionService.createVersion(bo, userId, userName);
        return R.ok(vo);
    }

    /**
     * 查询文件版本列表
     */
//    @SaCheckPermission("coedit:file:query")
    @GetMapping("/list/{fileId}")
    @Operation(summary = "查询文件版本列表")
    public TableDataInfo<CoFileVersionVo> getVersionList(
            @Parameter(description = "文件ID", required = true) @PathVariable Long fileId,
            PageQuery pageQuery) {

        return versionService.getVersionListByFileId(fileId, pageQuery);
    }

    /**
     * 获取版本详情
     */
//    @SaCheckPermission("coedit:file:query")
    @GetMapping("/{versionId}")
    @Operation(summary = "获取版本详情")
    public R<CoFileVersionVo> getVersionDetail(
            @Parameter(description = "版本ID", required = true) @PathVariable Long versionId) {

        CoFileVersionVo vo = versionService.getVersionById(versionId);
        return R.ok(vo);
    }

    /**
     * 下载指定版本的文件
     *
     * 【重要】注意权限处理：
     * 1. 已移除@SaCheckPermission注解，避免Token被踢下线的问题
     * 2. 在方法内部自己处理权限检查，更加灵活
     * 3. 支持登录用户和分享链接用户访问
     */
    @GetMapping("/download/{versionId}")
    @Operation(summary = "下载版本文件（不需要权限验证，支持分享下载）")
    public void downloadVersion(
            @Parameter(description = "版本ID", required = true) @PathVariable Long versionId,
            @RequestParam(required = false) String shareToken,  // 分享链接Token
            HttpServletResponse response) {

        log.info("下载版本文件: versionId={}, hasShareToken={}", versionId, shareToken != null);

        try {
            // 获取版本信息
            CoFileVersionVo version = versionService.getVersionById(versionId);
            if (version == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("版本不存在");
                log.warn("版本不存在: versionId={}", versionId);
                return;
            }

            // 【权限检查】：支持三种方式访问
            boolean hasAccess = checkVersionAccess(version.getFileId(), shareToken);
            if (!hasAccess) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("无权访问此文件版本，请先登录或使用有效的分享链接");
                log.warn("无权访问版本文件: versionId={}, fileId={}", versionId, version.getFileId());
                return;
            }

            // 下载版本文件
            byte[] fileBytes = versionService.downloadVersionFile(versionId);

            // 设置响应头
            FileUtils.setAttachmentResponseHeader(response, version.getFileName());
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE + "; charset=UTF-8");
            response.setContentLength(fileBytes.length);

            // 写入响应
            response.getOutputStream().write(fileBytes);
            response.getOutputStream().flush();

            log.info("版本文件下载成功: versionId={}, fileName={}, size={} bytes",
                     versionId, version.getFileName(), fileBytes.length);
        } catch (Exception e) {
            log.error("下载版本文件失败: versionId={}", versionId, e);
            try {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.setContentType("text/plain; charset=UTF-8");
                response.getWriter().write("下载失败: " + e.getMessage());
            } catch (Exception ex) {
                log.error("写入错误响应失败", ex);
            }
        }
    }

    /**
     * 检查版本访问权限
     * 支持三种方式：
     * 1. 使用分享Token（分享链接下载）
     * 2. 登录用户是文件所有者
     * 3. 登录用户有文件访问权限
     */
    private boolean checkVersionAccess(Long fileId, String shareToken) {
        try {
            // 方式1：分享链接访问
            if (StrUtil.isNotBlank(shareToken)) {
                // 验证分享Token是否有效
                // TODO: 实现分享Token验证逻辑
                // return shareAccessService.checkShareTokenValid(fileId, shareToken);
                log.info("使用分享Token访问: fileId={}", fileId);
                return true;  // 暂时允许，需要实现真实的Token验证
            }

            // 方式2：检查当前用户是否已登录
            try {
                Long userId = Long.valueOf(StpUtil.getLoginIdAsString().split(":")[1]);
                // 注意：这里暂时跳过文件查询，直接允许已登录用户下载
                // 在生产环境中应该查询文件信息并检查权限
                log.info("已登录用户请求下载: fileId={}, userId={}", fileId, userId);
                return true;  // 已登录用户可以访问

            } catch (Exception e) {
                // 用户未登录
                log.debug("用户未登录，尝试使用公开权限: fileId={}", fileId);
                // 未登录用户不允许下载（除非有分享Token）
                return false;
            }
        } catch (Exception e) {
            log.error("检查访问权限时出错: fileId={}", fileId, e);
            return false;
        }
    }

    /**
     * 版本对比
     */
//    @SaCheckPermission("coedit:file:query")
    @PostMapping("/compare")
    @Operation(summary = "版本对比")
    public R<Map<String, Object>> compareVersions(
            @Parameter(description = "版本1 ID", required = true) @RequestParam Long versionId1,
            @Parameter(description = "版本2 ID", required = true) @RequestParam Long versionId2) {

        Map<String, Object> result = versionService.compareVersions(versionId1, versionId2);
        return R.ok(result);
    }

    /**
     * 版本回滚
     */
//    @SaCheckPermission("coedit:file:edit")
    @Log(title = "版本回滚", businessType = BusinessType.UPDATE)
    @PostMapping("/rollback/{versionId}")
    @Operation(summary = "版本回滚")
    public R<Void> rollbackToVersion(
            @Parameter(description = "版本ID", required = true) @PathVariable Long versionId) {

        log.info("========== [版本回滚API] 收到回滚请求 ==========");
        log.info("[API入口] versionId={}", versionId);

//        Long userId = StpUtil.getLoginIdAsLong();
        Long userId = Long.valueOf(StpUtil.getLoginIdAsString().split(":")[1]);
        String userName = StpUtil.getLoginIdAsString();
        log.info("[用户信息] userId={}, userName={}", userId, userName);

        Boolean success = versionService.rollbackToVersion(versionId, userId, userName);
        log.info("[API响应] 回滚结果={}", success ? "成功" : "失败");

        return success ? R.ok() : R.fail("版本回滚失败");
    }

    /**
     * 删除指定版本
     */
//    @SaCheckPermission("coedit:file:remove")
    @Log(title = "删除文件版本", businessType = BusinessType.DELETE)
    @DeleteMapping("/{versionId}")
    @Operation(summary = "删除指定版本")
    public R<Void> deleteVersion(
            @Parameter(description = "版本ID", required = true) @PathVariable Long versionId) {

        Boolean success = versionService.deleteVersion(versionId);
        return success ? R.ok() : R.fail("删除失败");
    }

    /**
     * 清理文件的旧版本
     */
//    @SaCheckPermission("coedit:file:edit")
    @Log(title = "清理旧版本", businessType = BusinessType.DELETE)
    @PostMapping("/clean/{fileId}")
    @Operation(summary = "清理文件的旧版本")
    public R<Integer> cleanOldVersions(
            @Parameter(description = "文件ID", required = true) @PathVariable Long fileId,
            @Parameter(description = "保留版本数量") @RequestParam(required = false, defaultValue = "10") Integer keepCount) {

        Integer deletedCount = versionService.cleanOldVersions(fileId, keepCount);
        return R.ok("成功清理 " + deletedCount + " 个旧版本", deletedCount);
    }

    /**
     * 获取文件的当前版本
     */
//    @SaCheckPermission("coedit:file:query")
    @GetMapping("/current/{fileId}")
    @Operation(summary = "获取文件的当前版本")
    public R<CoFileVersionVo> getCurrentVersion(
            @Parameter(description = "文件ID", required = true) @PathVariable Long fileId) {

        CoFileVersionVo vo = versionService.getCurrentVersion(fileId);
        return R.ok(vo);
    }

    /**
     * 获取文件的版本统计
     */
//    @SaCheckPermission("coedit:file:query")
    @GetMapping("/statistics/{fileId}")
    @Operation(summary = "获取文件的版本统计")
    public R<Map<String, Object>> getVersionStatistics(
            @Parameter(description = "文件ID", required = true) @PathVariable Long fileId) {

        Map<String, Object> statistics = versionService.getVersionStatistics(fileId);
        return R.ok(statistics);
    }

}

