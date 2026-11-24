package com.xmzs.coedit.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.xmzs.coedit.domain.bo.CoFolderPermissionBo;
import com.xmzs.coedit.domain.vo.CoFolderPermissionVo;
import com.xmzs.coedit.service.ICoFolderPermissionService;
import com.xmzs.common.core.domain.R;
import com.xmzs.common.core.validate.AddGroup;
import com.xmzs.common.core.validate.EditGroup;
import com.xmzs.common.idempotent.annotation.RepeatSubmit;
import com.xmzs.common.log.annotation.Log;
import com.xmzs.common.log.enums.BusinessType;
import com.xmzs.common.mybatis.core.page.PageQuery;
import com.xmzs.common.mybatis.core.page.TableDataInfo;
import com.xmzs.common.web.core.BaseController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 文件夹权限管理Controller
 *
 * @author zuleed
 * @date 2025-11-05
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/coedit/folder/permission")
@Tag(name = "文件夹权限管理", description = "文件夹权限管理接口")
public class CoFolderPermissionController extends BaseController {

    private final ICoFolderPermissionService folderPermissionService;

    /**
     * 查询文件夹权限列表
     */
    // @SaCheckPermission("coedit:folder:permission:list")  // 协同编辑模块基础功能,允许所有登录用户访问
    @GetMapping("/list")
    @Operation(summary = "查询文件夹权限列表")
    public TableDataInfo<CoFolderPermissionVo> list(CoFolderPermissionBo bo, PageQuery pageQuery) {
        return folderPermissionService.queryPageList(bo, pageQuery);
    }

    /**
     * 获取文件夹的所有权限
     */
    // @SaCheckPermission("coedit:folder:permission:query")  // 协同编辑模块基础功能,允许所有登录用户访问
    @GetMapping("/folder/{folderId}")
    @Operation(summary = "获取文件夹的所有权限")
    public R<List<CoFolderPermissionVo>> getByFolderId(@PathVariable Long folderId) {
        List<CoFolderPermissionVo> list = folderPermissionService.queryByFolderId(folderId);
        return R.ok(list);
    }

    /**
     * 获取文件夹权限详细信息
     */
    // @SaCheckPermission("coedit:folder:permission:query")  // 协同编辑模块基础功能,允许所有登录用户访问
    @GetMapping("/{id}")
    @Operation(summary = "获取文件夹权限详细信息")
    public R<CoFolderPermissionVo> getInfo(@PathVariable Long id) {
        return R.ok(folderPermissionService.queryById(id));
    }

    /**
     * 获取用户对文件夹的权限
     */
    @GetMapping("/user/{folderId}/{userId}")
    @Operation(summary = "获取用户对文件夹的权限")
    public R<CoFolderPermissionVo> getUserPermission(
        @PathVariable Long folderId,
        @PathVariable Long userId) {
        CoFolderPermissionVo permission = folderPermissionService.queryUserPermission(folderId, userId);
        return R.ok(permission);
    }

    /**
     * 获取用户对文件夹的有效权限（考虑继承）
     */
    @GetMapping("/effective/{folderId}/{userId}")
    @Operation(summary = "获取用户对文件夹的有效权限")
    public R<CoFolderPermissionVo> getEffectivePermission(
        @PathVariable Long folderId,
        @PathVariable Long userId) {
        CoFolderPermissionVo permission = folderPermissionService.getEffectivePermission(folderId, userId);
        return R.ok(permission);
    }

    /**
     * 检查用户对文件夹的权限
     */
    @GetMapping("/check/{folderId}/{userId}/{operation}")
    @Operation(summary = "检查用户对文件夹的权限")
    public R<Boolean> checkPermission(
        @PathVariable Long folderId,
        @PathVariable Long userId,
        @PathVariable String operation) {
        Boolean hasPermission = folderPermissionService.checkPermission(folderId, userId, operation);
        return R.ok(hasPermission);
    }

    /**
     * 新增文件夹权限
     */
    // @SaCheckPermission("coedit:folder:permission:add")  // 协同编辑模块基础功能,允许所有登录用户访问
    @Log(title = "文件夹权限", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    @Operation(summary = "新增文件夹权限")
    public R<Void> add(@Validated(AddGroup.class) @RequestBody CoFolderPermissionBo bo) {
//        Long grantBy = StpUtil.getLoginIdAsLong();
        Long grantBy = Long.valueOf(StpUtil.getLoginIdAsString().split(":")[1]);
        bo.setGrantBy(grantBy);
        return toAjax(folderPermissionService.insertByBo(bo));
    }

    /**
     * 修改文件夹权限
     */
    // @SaCheckPermission("coedit:folder:permission:edit")  // 协同编辑模块基础功能,允许所有登录用户访问
    @Log(title = "文件夹权限", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    @Operation(summary = "修改文件夹权限")
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody CoFolderPermissionBo bo) {
        return toAjax(folderPermissionService.updateByBo(bo));
    }

    /**
     * 授予文件夹权限
     */
    // @SaCheckPermission("coedit:folder:permission:grant")  // 协同编辑模块基础功能,允许所有登录用户访问
    @Log(title = "授予文件夹权限", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PostMapping("/grant")
    @Operation(summary = "授予文件夹权限")
    public R<Void> grant(
        @Parameter(description = "文件夹ID") @RequestParam Long folderId,
        @Parameter(description = "目标ID") @RequestParam Long targetId,
        @Parameter(description = "权限类型") @RequestParam Integer permissionType,
        @Parameter(description = "权限级别") @RequestParam String permissionLevel) {
        return toAjax(folderPermissionService.grantPermission(folderId, targetId, permissionType, permissionLevel));
    }

    /**
     * 撤销文件夹权限
     */
    // @SaCheckPermission("coedit:folder:permission:revoke")  // 协同编辑模块基础功能,允许所有登录用户访问
    @Log(title = "撤销文件夹权限", businessType = BusinessType.UPDATE)
    @PostMapping("/revoke")
    @Operation(summary = "撤销文件夹权限")
    public R<Void> revoke(
        @Parameter(description = "文件夹ID") @RequestParam Long folderId,
        @Parameter(description = "目标ID") @RequestParam Long targetId,
        @Parameter(description = "权限类型") @RequestParam Integer permissionType) {
        return toAjax(folderPermissionService.revokePermission(folderId, targetId, permissionType));
    }

    /**
     * 批量设置文件夹权限
     */
    // @SaCheckPermission("coedit:folder:permission:batch")  // 协同编辑模块基础功能,允许所有登录用户访问
    @Log(title = "批量设置文件夹权限", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping("/batch/{folderId}")
    @Operation(summary = "批量设置文件夹权限")
    public R<Integer> batchSet(
        @PathVariable Long folderId,
        @RequestBody List<CoFolderPermissionBo> permissions) {
//        Long grantBy = StpUtil.getLoginIdAsLong();
        Long grantBy = Long.valueOf(StpUtil.getLoginIdAsString().split(":")[1]);
        Integer count = folderPermissionService.batchSetPermissions(folderId, permissions, grantBy);
        return R.ok("成功设置" + count + "个权限", count);
    }

    /**
     * 继承父文件夹权限
     */
    // @SaCheckPermission("coedit:folder:permission:inherit")  // 协同编辑模块基础功能,允许所有登录用户访问
    @Log(title = "继承父文件夹权限", businessType = BusinessType.INSERT)
    @PostMapping("/inherit/{folderId}/{parentFolderId}")
    @Operation(summary = "继承父文件夹权限")
    public R<Integer> inherit(
        @PathVariable Long folderId,
        @PathVariable Long parentFolderId) {
//        Long grantBy = StpUtil.getLoginIdAsLong();
        Long grantBy = Long.valueOf(StpUtil.getLoginIdAsString().split(":")[1]);
        Integer count = folderPermissionService.inheritParentPermission(folderId, parentFolderId, grantBy);
        return R.ok("成功继承" + count + "个权限", count);
    }

    /**
     * 传播权限到子文件夹
     */
    // @SaCheckPermission("coedit:folder:permission:propagate")  // 协同编辑模块基础功能,允许所有登录用户访问
    @Log(title = "传播权限到子文件夹", businessType = BusinessType.INSERT)
    @PostMapping("/propagate/{folderId}")
    @Operation(summary = "传播权限到子文件夹")
    public R<Integer> propagate(
        @PathVariable Long folderId,
        @Parameter(description = "是否递归") @RequestParam(defaultValue = "false") Boolean recursive) {
//        Long grantBy = StpUtil.getLoginIdAsLong();
        Long grantBy = Long.valueOf(StpUtil.getLoginIdAsString().split(":")[1]);
        Integer count = folderPermissionService.propagatePermissionToChildren(folderId, recursive, grantBy);
        return R.ok("成功传播" + count + "个权限", count);
    }

    /**
     * 复制权限到另一个文件夹
     */
    // @SaCheckPermission("coedit:folder:permission:copy")  // 协同编辑模块基础功能,允许所有登录用户访问
    @Log(title = "复制文件夹权限", businessType = BusinessType.INSERT)
    @PostMapping("/copy/{sourceFolderId}/{targetFolderId}")
    @Operation(summary = "复制权限到另一个文件夹")
    public R<Integer> copy(
        @PathVariable Long sourceFolderId,
        @PathVariable Long targetFolderId) {
//        Long grantBy = StpUtil.getLoginIdAsLong();
        Long grantBy = Long.valueOf(StpUtil.getLoginIdAsString().split(":")[1]);
        Integer count = folderPermissionService.copyPermissions(sourceFolderId, targetFolderId, grantBy);
        return R.ok("成功复制" + count + "个权限", count);
    }

    /**
     * 获取文件夹权限统计
     */
    // @SaCheckPermission("coedit:folder:permission:query")  // 协同编辑模块基础功能,允许所有登录用户访问
    @GetMapping("/statistics/{folderId}")
    @Operation(summary = "获取文件夹权限统计")
    public R<Map<String, Object>> getStatistics(@PathVariable Long folderId) {
        Map<String, Object> statistics = folderPermissionService.getPermissionStatistics(folderId);
        return R.ok(statistics);
    }

    /**
     * 删除文件夹权限
     */
    // @SaCheckPermission("coedit:folder:permission:remove")  // 协同编辑模块基础功能,允许所有登录用户访问
    @Log(title = "文件夹权限", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    @Operation(summary = "删除文件夹权限")
    public R<Void> remove(@PathVariable Long[] ids) {
        return toAjax(folderPermissionService.deleteByIds(Arrays.asList(ids)));
    }

    /**
     * 清理过期权限
     */
    // @SaCheckPermission("coedit:folder:permission:clean")  // 协同编辑模块基础功能,允许所有登录用户访问
    @Log(title = "清理过期权限", businessType = BusinessType.CLEAN)
    @PostMapping("/clean")
    @Operation(summary = "清理过期权限")
    public R<Integer> cleanExpired() {
        Integer count = folderPermissionService.cleanExpiredPermissions();
        return R.ok("成功清理" + count + "个过期权限", count);
    }
}

