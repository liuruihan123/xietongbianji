package com.xmzs.coedit.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaTokenConsts;
import com.xmzs.coedit.config.OnlyOfficeConfig;
import com.xmzs.coedit.domain.CoFile;
import com.xmzs.coedit.domain.bo.CoShareLinkBo;
import com.xmzs.coedit.domain.dto.DocumentPermissionConfig;
import com.xmzs.coedit.domain.dto.EditorConfig;
import com.xmzs.coedit.domain.vo.CoShareLinkVo;
import com.xmzs.coedit.mapper.CoFileMapper;
import com.xmzs.coedit.service.DocumentService;
import com.xmzs.coedit.service.EditorConfigService;
import com.xmzs.coedit.service.ICoShareLinkService;
import com.xmzs.common.core.domain.R;
import com.xmzs.common.core.domain.model.LoginUser;
import com.xmzs.common.satoken.utils.LoginHelper;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 共享链接控制器
 *
 * @author zuleed
 * @date 2025-11-04
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@Controller
@RequestMapping("/coedit/share")
@Tag(name = "共享链接管理", description = "共享链接管理接口")
public class CoShareLinkController extends BaseController {

    private final ICoShareLinkService shareLinkService;
    private final DocumentService documentService;
    private final EditorConfigService editorConfigService;
    private final OnlyOfficeConfig onlyOfficeConfig;
    private final CoFileMapper fileMapper;

    /**
     * 创建共享链接
     */
    // @SaCheckPermission("coedit:share:create")  // 协同编辑模块基础功能,允许所有登录用户访问
    @Log(title = "创建共享链接", businessType = BusinessType.INSERT)
    @PostMapping("/create")
    @ResponseBody
    @Operation(summary = "创建共享链接")
    public R<CoShareLinkVo> createShareLink(@Validated @RequestBody CoShareLinkBo bo) {
        Long userId =  Long.valueOf(StpUtil.getLoginIdAsString().split(":")[1]);
        String userName = StpUtil.getLoginIdAsString(); // 实际应该从用户信息中获取

        CoShareLinkVo vo = shareLinkService.createShareLink(bo, userId, userName);
        return R.ok(vo);
    }

    /**
     * 根据共享码获取共享链接信息
     */
    @GetMapping("/{shareCode}")
    @ResponseBody
    @Operation(summary = "获取共享链接信息")
    public R<CoShareLinkVo> getShareLink(
            @Parameter(description = "共享码", required = true) @PathVariable String shareCode) {

        CoShareLinkVo vo = shareLinkService.getShareLinkByCode(shareCode);
        if (vo == null) {
            return R.fail("共享链接不存在");
        }

        return R.ok(vo);
    }

    /**
     * 根据ID获取共享链接详情
     */
    // @SaCheckPermission("coedit:share:query")  // 协同编辑模块基础功能,允许所有登录用户访问
    @GetMapping("/detail/{id}")
    @ResponseBody
    @Operation(summary = "获取共享链接详情")
    public R<CoShareLinkVo> getShareLinkDetail(
            @Parameter(description = "共享链接ID", required = true) @PathVariable Long id) {

        CoShareLinkVo vo = shareLinkService.getShareLinkById(id);
        if (vo == null) {
            return R.fail("共享链接不存在");
        }

        return R.ok(vo);
    }

    /**
     * 通过分享链接打开文档编辑器
     */
    @GetMapping("/open/{shareCode}")
    @Operation(summary = "通过分享链接打开文档编辑器")
    public String openShareEditor(
            @Parameter(description = "共享码", required = true) @PathVariable String shareCode,
            @Parameter(description = "访问密码") @RequestParam(required = false) String password,
            @Parameter(description = "访问者名称") @RequestParam(required = false) String visitorName,
            @Parameter(description = "认证Token") @RequestParam(required = false) String authToken,
            Model model,
            HttpServletRequest request) {

        // 解析authToken，获取用户信息（但不恢复登录状态）
        LoginUser tokenUser = null;
        if (authToken != null && !authToken.isEmpty()) {
            try {
                // 移除Bearer前缀（如果有）
                String rawToken = authToken;
                if (authToken.startsWith("Bearer ")) {
                    rawToken = authToken.substring(7);
                }

                log.info("【调试】开始解析authToken: token前20字符={}", rawToken.substring(0, Math.min(20, rawToken.length())));

                // 使用LoginHelper.getLoginUser()获取用户信息
                tokenUser = LoginHelper.getLoginUser(rawToken);
                log.info("【调试】LoginHelper.getLoginUser()结果: loginUser={}", tokenUser != null ? tokenUser.getLoginId() : "null");

                if (tokenUser != null) {
                    log.info("从authToken解析到用户信息: loginId={}, userId={}", tokenUser.getLoginId(), tokenUser.getUserId());
                } else {
                    log.warn("authToken无效，无法获取用户信息: token={}", rawToken.substring(0, Math.min(20, rawToken.length())) + "...");
                }
            } catch (Exception e) {
                log.warn("解析authToken失败: {}", e.getMessage(), e);
            }
        }

        try {
            // 检查用户是否已认证（优先使用从authToken解析的用户信息）
            String currentUserDisplayName = null;  // 用于在OnlyOffice编辑器中显示的用户名称
            Long currentUserId = null;
            boolean isAuthenticated = false;
            String effectiveAuthToken = null;

            // 优先使用从authToken解析的用户信息
            if (tokenUser != null) {
                isAuthenticated = true;
                currentUserId = tokenUser.getUserId();

                // 【用户显示名称配置】
                // 当前配置：优先使用昵称(nickName)，如果昵称为空则使用用户名(userName)
                // 可选配置：
                //   1. 只使用昵称：currentUserDisplayName = tokenUser.getNickName();
                //   2. 只使用用户名：currentUserDisplayName = tokenUser.getUserName();
                //   3. 使用登录ID：currentUserDisplayName = tokenUser.getLoginId();
                //   4. 自定义格式：currentUserDisplayName = tokenUser.getNickName() + "(" + tokenUser.getUserName() + ")";
                currentUserDisplayName = (tokenUser.getNickName() != null && !tokenUser.getNickName().isEmpty())
                    ? tokenUser.getNickName()
                    : tokenUser.getUserName();

                effectiveAuthToken = authToken; // 使用传入的authToken
                log.info("【调试】使用authToken中的用户信息: userId={}, displayName={}", currentUserId, currentUserDisplayName);
            } else {
                // 如果authToken无效，再检查当前会话是否已登录
                try {
                    log.info("【调试】开始检查当前会话登录状态...");
                    boolean loginStatus = StpUtil.isLogin();
                    log.info("【调试】StpUtil.isLogin()结果: {}", loginStatus);

                    if (loginStatus) {
                        isAuthenticated = true;
                        String loginIdStr = StpUtil.getLoginIdAsString();
                        log.info("【调试】StpUtil.getLoginIdAsString()结果: {}", loginIdStr);

                        currentUserId = Long.valueOf(loginIdStr.split(":")[1]);

                        // 从当前会话获取用户信息
                        LoginUser currentUser = LoginHelper.getLoginUser();

                        // 【用户显示名称配置】
                        // 当前配置：优先使用昵称(nickName)，如果昵称为空则使用用户名(userName)
                        // 可选配置同上
                        currentUserDisplayName = (currentUser.getNickName() != null && !currentUser.getNickName().isEmpty())
                            ? currentUser.getNickName()
                            : currentUser.getUserName();

                        effectiveAuthToken = StpUtil.getTokenValue(); // 使用当前会话的Token
                        log.info("检测到已认证用户访问分享链接: userId={}, displayName={}", currentUserId, currentUserDisplayName);
                    } else {
                        log.info("【调试】用户未登录，isAuthenticated=false");
                    }
                } catch (Exception e) {
                    log.warn("【调试】用户认证检查失败: {}", e.getMessage(), e);
                    isAuthenticated = false;
                }
            }

            // 验证 shareCode 格式,排除保留关键字和非法字符
            if (shareCode == null || shareCode.trim().isEmpty()) {
                log.warn("分享码为空");
                model.addAttribute("error", "分享码不能为空");
                return "error";
            }

            // 排除保留关键字(视图名称、静态资源等)
            String[] reservedKeywords = {"share-editor", "editor", "error", "index", "static", "api", "css", "js", "img"};
            for (String keyword : reservedKeywords) {
                if (shareCode.equalsIgnoreCase(keyword)) {
                    log.warn("分享码包含保留关键字: shareCode={}", shareCode);
                    model.addAttribute("error", "无效的分享码");
                    return "error";
                }
            }

            // 验证 shareCode 只包含字母和数字
            if (!shareCode.matches("^[a-zA-Z0-9]+$")) {
                log.warn("分享码格式不正确: shareCode={}", shareCode);
                model.addAttribute("error", "分享码格式不正确");
                return "error";
            }

            log.info("访问分享链接: shareCode={}, visitorName={}, hasPassword={}",
                shareCode, visitorName, password != null);

            // 1. 先获取分享链接信息,用于详细的错误提示
            CoShareLinkVo shareLink = shareLinkService.getShareLinkByCode(shareCode);
            if (shareLink == null) {
                log.warn("分享链接不存在1: shareCode={}", shareCode);
                model.addAttribute("error", "分享链接不存在,请检查链接是否正确");
                return "error"; // 返回错误页面
            }

            // 获取分享链接信息
            log.info("分享链接信息: fileId={}, shareType={}, status={}, permissionType={}",
                shareLink.getFileId(), shareLink.getShareType(), shareLink.getStatus(), shareLink.getPermissionType());

            // 2. 验证分享链接
            Boolean isValid = shareLinkService.checkShareLinkValid(shareCode, password);
            if (!isValid) {
                // 提供更详细的错误信息
                String errorMsg = "分享链接无效";
                if (shareLink.getStatus() != 0) {
                    errorMsg = "分享链接已被禁用或已过期";
                } else if (shareLink.getExpireTime() != null && shareLink.getExpireTime().before(new Date())) {
                    errorMsg = "分享链接已过期";
                } else if (shareLink.getMaxAccessCount() != null
                    && shareLink.getAccessCount() >= shareLink.getMaxAccessCount()) {
                    errorMsg = "分享链接访问次数已达上限";
                } else if (shareLink.getShareType() == 1) {
                    errorMsg = "请输入正确的访问密码";
                }
                log.warn("分享链接验证失败: shareCode={}, reason={}", shareCode, errorMsg);
                model.addAttribute("error", errorMsg);
                return "error";
            }
            if (shareLink == null) {
                model.addAttribute("error", "分享链接不存在");
                return "error";
            }

            // 2. 获取文件信息
            CoFile file = fileMapper.selectById(shareLink.getFileId());
            if (file == null) {
                model.addAttribute("error", "文件不存在");
                return "error";
            }

            // 3. 增加访问次数
            shareLinkService.incrementAccessCount(shareCode);

            // 4. 确定用户身份和名称（effectiveAuthToken已在上面确定）
            String finalUserDisplayName;  // 最终在OnlyOffice编辑器中显示的用户名称
            String userMode;

            log.info("【调试】开始确定用户身份: isAuthenticated={}, visitorName={}, hasToken={}", isAuthenticated, visitorName, effectiveAuthToken != null);

            if (isAuthenticated && visitorName == null) {
                // 已认证用户且未强制指定访客名称，使用真实身份
                finalUserDisplayName = currentUserDisplayName;
                userMode = "authenticated";
                log.info("【调试】使用已认证用户身份: effectiveAuthToken前20字符={}", effectiveAuthToken != null ? effectiveAuthToken.substring(0, Math.min(20, effectiveAuthToken.length())) : "null");
                log.info("使用已认证用户身份: userId={}, displayName={}, hasToken={}", currentUserId, currentUserDisplayName, effectiveAuthToken != null);
            } else {
                // 访客模式或强制指定了访客名称
                finalUserDisplayName = visitorName != null ? visitorName : "访客";
                userMode = "visitor";
                // 访客模式下authToken为null，需要允许匿名下载
                effectiveAuthToken = null;
                log.info("【调试】进入访客模式分支: isAuthenticated={}, visitorName={}", isAuthenticated, visitorName);
                log.info("使用访客模式: displayName={}", finalUserDisplayName);
            }

            // 5. 生成文档访问URL (使用正确的authToken)
            String fileUrl = documentService.getDocumentUrl(String.valueOf(file.getId()), onlyOfficeConfig.getServerUrl(), effectiveAuthToken);

            // 6. 获取文档Key
            // 修复: 使用documentService.getDocumentKey()确保与正常编辑使用相同的documentKey
            // 这样所有用户（无论是文档所有者还是通过分享链接访问的用户）都能看到同一份文档
            String documentKey = documentService.getDocumentKey(String.valueOf(file.getId()));
            log.info("【调试】分享链接使用的documentKey: {}", documentKey);

            // 7. 根据分享链接的权限配置创建编辑器配置
            // 从数据库字段构建权限配置对象
            DocumentPermissionConfig permissionConfig = DocumentPermissionConfig.fromShareLink(
                shareLink.getAllowDownload() == 1,
                shareLink.getAllowPrint() == 1,
                shareLink.getAllowCopy() == 1,
                shareLink.getAllowEdit() == 1,
                shareLink.getAllowComment() == 1
            );

            EditorConfig config = editorConfigService.createShareLinkConfig(
                file.getId(),
                file.getFileName(),
                fileUrl,
                documentKey,
                permissionConfig,       // 使用细粒度权限配置
                shareCode,
                currentUserId,          // 传递用户ID
                finalUserDisplayName    // 传递最终确定的用户显示名称（昵称或用户名）
            );

            // 8. 设置模型属性（与 EditorController 保持一致）
            model.addAttribute("config", config);
            model.addAttribute("documentServerUrl", onlyOfficeConfig.getDocumentServerUrl());
            model.addAttribute("fileId", file.getId().toString());
            model.addAttribute("fileName", file.getFileName());
            model.addAttribute("shareCode", shareCode);
            model.addAttribute("permissionType", shareLink.getPermissionType());
            model.addAttribute("userMode", userMode);
            model.addAttribute("isAuthenticated", isAuthenticated);
            model.addAttribute("currentUserId", currentUserId);

            log.info("分享链接打开编辑器成功: shareCode={}, fileId={}, fileName={}, permissionType={}, userMode={}, isAuthenticated={}",
                shareCode, file.getId(), file.getFileName(), shareLink.getPermissionType(), userMode, isAuthenticated);

            // 8. 返回分享编辑器页面
            return "share-editor"; // 返回OnlyOffice分享编辑器页面
//            return shareCode; // 返回OnlyOffice分享编辑器页面

        } catch (Exception e) {
            log.error("打开分享链接编辑器失败: shareCode={}", shareCode, e);
            model.addAttribute("error", "打开文档失败: " + e.getMessage());
            return "error";
        }
    }

    /**
     * 获取文件的所有共享链接
     */
    // @SaCheckPermission("coedit:share:query")  // 协同编辑模块基础功能,允许所有登录用户访问
    @GetMapping("/file/{fileId}")
    @ResponseBody
    @Operation(summary = "获取文件的所有共享链接")
    public TableDataInfo<CoShareLinkVo> getFileShareLinks(
            @Parameter(description = "文件ID", required = true) @PathVariable Long fileId,
            PageQuery pageQuery) {

        return shareLinkService.getShareLinksByFileId(fileId, pageQuery);
    }

    /**
     * 获取我创建的共享链接
     */
    // @SaCheckPermission("coedit:share:query")  // 协同编辑模块基础功能,允许所有登录用户访问
    @GetMapping("/my")
    @ResponseBody
    @Operation(summary = "获取我创建的共享链接")
    public TableDataInfo<CoShareLinkVo> getMyShareLinks(PageQuery pageQuery) {
        Long userId =  Long.valueOf(StpUtil.getLoginIdAsString().split(":")[1]);
        return shareLinkService.getShareLinksByCreatorId(userId, pageQuery);
    }

    /**
     * 更新共享链接
     */
    // @SaCheckPermission("coedit:share:edit")  // 协同编辑模块基础功能,允许所有登录用户访问
    @Log(title = "更新共享链接", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}")
    @ResponseBody
    @Operation(summary = "更新共享链接")
    public R<Void> updateShareLink(
            @Parameter(description = "共享链接ID", required = true) @PathVariable Long id,
            @Validated @RequestBody CoShareLinkBo bo) {

        bo.setId(id);
        Boolean success = shareLinkService.updateShareLink(bo);
        return success ? R.ok() : R.fail("更新失败");
    }

    /**
     * 禁用共享链接
     */
    // @SaCheckPermission("coedit:share:edit")  // 协同编辑模块基础功能,允许所有登录用户访问
    @Log(title = "禁用共享链接", businessType = BusinessType.UPDATE)
    @PutMapping("/disable/{id}")
    @ResponseBody
    @Operation(summary = "禁用共享链接")
    public R<Void> disableShareLink(
            @Parameter(description = "共享链接ID", required = true) @PathVariable Long id) {

        Boolean success = shareLinkService.disableShareLink(id);
        return success ? R.ok() : R.fail("禁用失败");
    }

    /**
     * 删除共享链接
     */
    // @SaCheckPermission("coedit:share:remove")  // 协同编辑模块基础功能,允许所有登录用户访问
    @Log(title = "删除共享链接", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    @ResponseBody
    @Operation(summary = "删除共享链接")
    public R<Void> deleteShareLink(
            @Parameter(description = "共享链接ID", required = true) @PathVariable Long id) {

        Boolean success = shareLinkService.deleteShareLink(id);
        return success ? R.ok() : R.fail("删除失败");
    }

    /**
     * 批量删除共享链接
     */
    // @SaCheckPermission("coedit:share:remove")  // 协同编辑模块基础功能,允许所有登录用户访问
    @Log(title = "批量删除共享链接", businessType = BusinessType.DELETE)
    @DeleteMapping("/batch")
    @ResponseBody
    @Operation(summary = "批量删除共享链接")
    public R<Void> deleteShareLinks(
            @Parameter(description = "共享链接ID列表", required = true) @RequestBody List<Long> ids) {

        Boolean success = shareLinkService.deleteShareLinks(ids);
        return success ? R.ok() : R.fail("删除失败");
    }

    /**
     * 访问共享链接（验证并增加访问次数）- 路径参数方式
     */
    @PostMapping("/access/{shareCode}")
    @ResponseBody
    @Operation(summary = "访问共享链接(路径参数)")
    public R<Map<String, Object>> accessShareLink(
            @Parameter(description = "共享码", required = true) @PathVariable String shareCode,
            @Parameter(description = "访问密码") @RequestParam(required = false) String password) {

        return processShareLinkAccess(shareCode, password);
    }

    /**
     * 访问共享链接（验证并增加访问次数）- Body参数方式(兼容前端)
     */
    @PostMapping("/access")
    @ResponseBody
    @Operation(summary = "访问共享链接(Body参数)")
    public R<Map<String, Object>> accessShareLinkByBody(
            @RequestBody Map<String, String> params) {

        String shareCode = params.get("shareCode");
        String password = params.get("sharePassword");

        if (shareCode == null || shareCode.isEmpty()) {
            return R.fail("共享码不能为空");
        }

        return processShareLinkAccess(shareCode, password);
    }

    /**
     * 处理共享链接访问的通用逻辑
     */
    private R<Map<String, Object>> processShareLinkAccess(String shareCode, String password) {
        // 检查链接是否有效
        Boolean isValid = shareLinkService.checkShareLinkValid(shareCode, password);

        Map<String, Object> result = new HashMap<>();
        result.put("isValid", isValid);

        if (!isValid) {
            result.put("message", "共享链接无效或密码错误");
            return R.ok(result);
        }

        // 增加访问次数
        shareLinkService.incrementAccessCount(shareCode);

        // 获取共享链接信息
        CoShareLinkVo vo = shareLinkService.getShareLinkByCode(shareCode);
        result.put("shareLink", vo);
        result.put("message", "访问成功");

        return R.ok(result);
    }

    /**
     * 检查共享链接是否有效
     */
    @GetMapping("/check/{shareCode}")
    @ResponseBody
    @Operation(summary = "检查共享链接是否有效")
    public R<Map<String, Object>> checkShareLink(
            @Parameter(description = "共享码", required = true) @PathVariable String shareCode,
            @Parameter(description = "访问密码") @RequestParam(required = false) String password) {

        Boolean isValid = shareLinkService.checkShareLinkValid(shareCode, password);

        Map<String, Object> result = new HashMap<>();
        result.put("isValid", isValid);

        if (isValid) {
            CoShareLinkVo vo = shareLinkService.getShareLinkByCode(shareCode);
            result.put("shareLink", vo);
        }

        return R.ok(result);
    }

}

