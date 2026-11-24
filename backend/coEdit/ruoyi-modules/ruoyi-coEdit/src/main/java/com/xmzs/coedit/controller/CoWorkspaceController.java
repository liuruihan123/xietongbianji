package com.xmzs.coedit.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.xmzs.coedit.domain.bo.CoWorkspaceBo;
import com.xmzs.coedit.domain.vo.CoWorkspaceVo;
import com.xmzs.coedit.service.ICoWorkspaceService;
import com.xmzs.common.core.domain.R;
import com.xmzs.common.core.validate.AddGroup;
import com.xmzs.common.core.validate.EditGroup;
import com.xmzs.common.idempotent.annotation.RepeatSubmit;
import com.xmzs.common.log.annotation.Log;
import com.xmzs.common.log.enums.BusinessType;
import com.xmzs.common.mybatis.core.page.PageQuery;
import com.xmzs.common.mybatis.core.page.TableDataInfo;
import com.xmzs.common.satoken.utils.LoginHelper;
import com.xmzs.common.web.core.BaseController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 工作空间管理
 *
 * @author zuleed
 * @date 2025-11-04
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/coedit/workspace")
@Tag(name = "工作空间管理", description = "工作空间的增删改查操作")
public class CoWorkspaceController extends BaseController {

    private final ICoWorkspaceService coWorkspaceService;

    /**
     * 查询工作空间列表
     */
    @Operation(summary = "查询工作空间列表", description = "分页查询工作空间列表")
    // @SaCheckPermission("coedit:workspace:list")  // 协同编辑模块基础功能,允许所有登录用户访问
    @GetMapping("/list")
    public TableDataInfo<CoWorkspaceVo> list(CoWorkspaceBo bo, PageQuery pageQuery) {
        return coWorkspaceService.queryPageList(bo, pageQuery);
    }

    /**
     * 查询当前用户的工作空间列表
     */
    @Operation(summary = "查询当前用户的工作空间列表")
    @GetMapping("/my-workspaces")
    public R<List<CoWorkspaceVo>> myWorkspaces() {
        Long userId = LoginHelper.getUserId();
        List<CoWorkspaceVo> list = coWorkspaceService.queryUserWorkspaces(userId);
        return R.ok(list);
    }

    /**
     * 查询当前用户的默认工作空间
     */
    @Operation(summary = "查询当前用户的默认工作空间")
    @GetMapping("/default")
    public R<CoWorkspaceVo> defaultWorkspace() {
        Long userId = LoginHelper.getUserId();
        CoWorkspaceVo workspace = coWorkspaceService.queryUserDefaultWorkspace(userId);
        return R.ok(workspace);
    }

    /**
     * 导出工作空间列表
     */
    @Operation(summary = "导出工作空间列表")
    // @SaCheckPermission("coedit:workspace:export")  // 协同编辑模块基础功能,允许所有登录用户访问
    @Log(title = "工作空间", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(CoWorkspaceBo bo) {
        List<CoWorkspaceVo> list = coWorkspaceService.queryList(bo);
        // TODO: 实现导出功能
    }

    /**
     * 获取工作空间详细信息
     *
     * @param id 工作空间ID
     */
    @Operation(summary = "获取工作空间详细信息")
    @Parameter(name = "id", description = "工作空间ID", required = true)
    // @SaCheckPermission("coedit:workspace:query")  // 协同编辑模块基础功能,允许所有登录用户访问
    @GetMapping("/{id}")
    public R<CoWorkspaceVo> getInfo(@NotNull(message = "工作空间ID不能为空")
                                    @PathVariable Long id) {
        return R.ok(coWorkspaceService.queryById(id));
    }

    /**
     * 新增工作空间
     */
    @Operation(summary = "新增工作空间")
    // @SaCheckPermission("coedit:workspace:add")  // 协同编辑模块基础功能,允许所有登录用户访问
    @Log(title = "工作空间", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody CoWorkspaceBo bo) {
        // 检查工作空间名称是否唯一
        if (!coWorkspaceService.checkWorkspaceNameUnique(bo)) {
            return R.fail("工作空间名称已存在");
        }
        return toAjax(coWorkspaceService.insertByBo(bo));
    }

    /**
     * 修改工作空间
     */
    @Operation(summary = "修改工作空间")
    // @SaCheckPermission("coedit:workspace:edit")  // 协同编辑模块基础功能,允许所有登录用户访问
    @Log(title = "工作空间", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody CoWorkspaceBo bo) {
        // 检查工作空间名称是否唯一
        if (!coWorkspaceService.checkWorkspaceNameUnique(bo)) {
            return R.fail("工作空间名称已存在");
        }
        return toAjax(coWorkspaceService.updateByBo(bo));
    }

    /**
     * 删除工作空间
     *
     * @param ids 工作空间ID数组
     */
    @Operation(summary = "删除工作空间")
    @Parameter(name = "ids", description = "工作空间ID数组", required = true)
    // @SaCheckPermission("coedit:workspace:remove")  // 协同编辑模块基础功能,允许所有登录用户访问
    @Log(title = "工作空间", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "工作空间ID不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(coWorkspaceService.deleteWithValidByIds(List.of(ids), true));
    }

    /**
     * 设置默认工作空间
     *
     * @param id 工作空间ID
     */
    @Operation(summary = "设置默认工作空间")
    @Parameter(name = "id", description = "工作空间ID", required = true)
    // @SaCheckPermission("coedit:workspace:edit")  // 协同编辑模块基础功能,允许所有登录用户访问
    @Log(title = "工作空间", businessType = BusinessType.UPDATE)
    @PutMapping("/set-default/{id}")
    public R<Void> setDefault(@NotNull(message = "工作空间ID不能为空")
                              @PathVariable Long id) {
        Long userId = LoginHelper.getUserId();
        return toAjax(coWorkspaceService.setDefaultWorkspace(userId, id));
    }

    /**
     * 检查存储空间是否足够
     *
     * @param id           工作空间ID
     * @param requiredSize 需要的空间大小（字节）
     */
    @Operation(summary = "检查存储空间是否足够")
    @GetMapping("/check-storage/{id}")
    public R<Boolean> checkStorage(@PathVariable Long id,
                                    @RequestParam Long requiredSize) {
        Boolean available = coWorkspaceService.checkStorageAvailable(id, requiredSize);
        return R.ok(available);
    }

    /**
     * 检查成员数量是否已满
     *
     * @param id 工作空间ID
     */
    @Operation(summary = "检查成员数量是否已满")
    @GetMapping("/check-member-limit/{id}")
    public R<Boolean> checkMemberLimit(@PathVariable Long id) {
        Boolean reached = coWorkspaceService.checkMemberLimitReached(id);
        return R.ok(reached);
    }

    /**
     * 获取工作空间统计信息
     *
     * @param id 工作空间ID
     */
    @Operation(summary = "获取工作空间统计信息")
    @GetMapping("/{id}/stats")
    public R<java.util.Map<String, Object>> getWorkspaceStats(@PathVariable Long id) {
        java.util.Map<String, Object> stats = coWorkspaceService.getWorkspaceStats(id);
        return R.ok(stats);
    }
}

