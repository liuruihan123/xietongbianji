package com.xmzs.coedit.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.lang.tree.Tree;
import com.xmzs.coedit.domain.bo.CoFolderBo;
import com.xmzs.coedit.domain.vo.CoFolderVo;
import com.xmzs.coedit.service.ICoFolderService;
import com.xmzs.common.core.domain.R;
import com.xmzs.common.core.validate.AddGroup;
import com.xmzs.common.core.validate.EditGroup;
import com.xmzs.common.excel.utils.ExcelUtil;
import com.xmzs.common.idempotent.annotation.RepeatSubmit;
import com.xmzs.common.log.annotation.Log;
import com.xmzs.common.log.enums.BusinessType;
import com.xmzs.common.mybatis.core.page.PageQuery;
import com.xmzs.common.mybatis.core.page.TableDataInfo;
import com.xmzs.common.web.core.BaseController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 文件夹管理
 *
 * @author zuleed
 * @date 2025-11-04
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/coedit/folder")
@Tag(name = "文件夹管理", description = "文件夹管理")
public class CoFolderController extends BaseController {

    private final ICoFolderService folderService;

    /**
     * 查询文件夹列表
     */
    // @SaCheckPermission("coedit:folder:list")  // 协同编辑模块基础功能,允许所有登录用户访问
    @GetMapping("/list")
    @Operation(summary = "查询文件夹列表", description = "查询文件夹列表（分页）")
    public TableDataInfo<CoFolderVo> list(CoFolderBo bo, PageQuery pageQuery) {
        return folderService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出文件夹列表
     */
    // @SaCheckPermission("coedit:folder:export")  // 协同编辑模块基础功能,允许所有登录用户访问
    @Log(title = "文件夹", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @Operation(summary = "导出文件夹列表", description = "导出文件夹列表")
    public void export(CoFolderBo bo, HttpServletResponse response) {
        List<CoFolderVo> list = folderService.queryList(bo);
        ExcelUtil.exportExcel(list, "文件夹", CoFolderVo.class, response);
    }

    /**
     * 获取文件夹详细信息
     */
    // @SaCheckPermission("coedit:folder:query")  // 协同编辑模块基础功能,允许所有登录用户访问
    @GetMapping("/{id}")
    @Operation(summary = "获取文件夹详细信息", description = "根据文件夹ID获取详细信息")
    public R<CoFolderVo> getInfo(@Parameter(description = "文件夹ID", required = true) @NotNull(message = "文件夹ID不能为空") @PathVariable Long id) {
        return R.ok(folderService.queryById(id));
    }

    /**
     * 新增文件夹
     */
    // @SaCheckPermission("coedit:folder:add")  // 协同编辑模块基础功能,允许所有登录用户访问
    @Log(title = "文件夹", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    @Operation(summary = "新增文件夹", description = "新增文件夹")
    public R<Void> add(@Validated(AddGroup.class) @RequestBody CoFolderBo bo) {
        return toAjax(folderService.insertByBo(bo));
    }

    /**
     * 修改文件夹
     */
    // @SaCheckPermission("coedit:folder:edit")  // 协同编辑模块基础功能,允许所有登录用户访问
    @Log(title = "文件夹", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    @Operation(summary = "修改文件夹", description = "修改文件夹")
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody CoFolderBo bo) {
        return toAjax(folderService.updateByBo(bo));
    }

    /**
     * 删除文件夹
     */
    // @SaCheckPermission("coedit:folder:remove")  // 协同编辑模块基础功能,允许所有登录用户访问
    @Log(title = "文件夹", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    @Operation(summary = "删除文件夹", description = "删除文件夹（软删除）")
    public R<Void> remove(@Parameter(description = "文件夹ID数组", required = true) @NotEmpty(message = "文件夹ID不能为空") @PathVariable Long[] ids) {
        return toAjax(folderService.deleteWithValidByIds(List.of(ids), true));
    }

    /**
     * 获取文件夹树形结构
     */
    // @SaCheckPermission("coedit:folder:list")  // 协同编辑模块基础功能,允许所有登录用户访问
    @GetMapping("/tree/{workspaceId}")
    @Operation(summary = "获取文件夹树形结构", description = "根据工作空间ID获取文件夹树形结构")
    public R<List<Tree<Long>>> tree(@Parameter(description = "工作空间ID", required = true) @NotNull(message = "工作空间ID不能为空") @PathVariable Long workspaceId) {
        return R.ok(folderService.selectFolderTreeList(workspaceId));
    }

    /**
     * 创建文件夹
     */
    // @SaCheckPermission("coedit:folder:add")  // 协同编辑模块基础功能,允许所有登录用户访问
    @Log(title = "创建文件夹", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping("/create")
    @Operation(summary = "创建文件夹", description = "在指定位置创建文件夹")
    public R<CoFolderVo> create(
        @Parameter(description = "工作空间ID", required = true) @NotNull(message = "工作空间ID不能为空") @RequestParam Long workspaceId,
        @Parameter(description = "父文件夹ID") @RequestParam(required = false) Long parentId,
        @Parameter(description = "文件夹名称", required = true) @NotNull(message = "文件夹名称不能为空") @RequestParam String folderName) {
        
        CoFolderVo folder = folderService.createFolder(workspaceId, parentId, folderName);
        return R.ok(folder);
    }

    /**
     * 重命名文件夹
     */
    // @SaCheckPermission("coedit:folder:edit")  // 协同编辑模块基础功能,允许所有登录用户访问
    @Log(title = "重命名文件夹", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping("/{id}/rename")
    @Operation(summary = "重命名文件夹", description = "重命名文件夹")
    public R<Void> rename(
        @Parameter(description = "文件夹ID", required = true) @NotNull(message = "文件夹ID不能为空") @PathVariable Long id,
        @Parameter(description = "新名称", required = true) @NotNull(message = "新名称不能为空") @RequestParam String newName) {
        return toAjax(folderService.renameFolder(id, newName));
    }

    /**
     * 移动文件夹
     */
    // @SaCheckPermission("coedit:folder:edit")  // 协同编辑模块基础功能,允许所有登录用户访问
    @Log(title = "移动文件夹", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping("/{id}/move")
    @Operation(summary = "移动文件夹", description = "移动文件夹到指定位置")
    public R<Void> move(
        @Parameter(description = "文件夹ID", required = true) @NotNull(message = "文件夹ID不能为空") @PathVariable Long id,
        @Parameter(description = "目标父文件夹ID") @RequestParam(required = false) Long targetParentId) {
        return toAjax(folderService.moveFolder(id, targetParentId));
    }

    /**
     * 复制文件夹
     */
    // @SaCheckPermission("coedit:folder:add")  // 协同编辑模块基础功能,允许所有登录用户访问
    @Log(title = "复制文件夹", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping("/{id}/copy")
    @Operation(summary = "复制文件夹", description = "复制文件夹到指定位置")
    public R<CoFolderVo> copy(
        @Parameter(description = "文件夹ID", required = true) @NotNull(message = "文件夹ID不能为空") @PathVariable Long id,
        @Parameter(description = "目标父文件夹ID") @RequestParam(required = false) Long targetParentId,
        @Parameter(description = "新名称") @RequestParam(required = false) String newName) {
        
        CoFolderVo folder = folderService.copyFolder(id, targetParentId, newName);
        return R.ok(folder);
    }

    /**
     * 查询工作空间下的文件夹列表
     */
    // @SaCheckPermission("coedit:folder:list")  // 协同编辑模块基础功能,允许所有登录用户访问
    @GetMapping("/workspace/{workspaceId}")
    @Operation(summary = "查询工作空间下的文件夹列表", description = "根据工作空间ID查询文件夹列表")
    public R<List<CoFolderVo>> listByWorkspace(
        @Parameter(description = "工作空间ID", required = true) @NotNull(message = "工作空间ID不能为空") @PathVariable Long workspaceId) {
        return R.ok(folderService.queryByWorkspaceId(workspaceId));
    }

    /**
     * 查询父文件夹下的子文件夹列表
     */
    // @SaCheckPermission("coedit:folder:list")  // 协同编辑模块基础功能,允许所有登录用户访问
    @GetMapping("/parent/{parentId}")
    @Operation(summary = "查询父文件夹下的子文件夹列表", description = "根据父文件夹ID查询子文件夹列表")
    public R<List<CoFolderVo>> listByParent(
        @Parameter(description = "父文件夹ID", required = true) @NotNull(message = "父文件夹ID不能为空") @PathVariable Long parentId) {
        return R.ok(folderService.queryByParentId(parentId));
    }

    /**
     * 检查文件夹是否存在
     */
    // @SaCheckPermission("coedit:folder:query")  // 协同编辑模块基础功能,允许所有登录用户访问
    @GetMapping("/exists")
    @Operation(summary = "检查文件夹是否存在", description = "检查文件夹名称是否已存在")
    public R<Boolean> checkExists(
        @Parameter(description = "工作空间ID", required = true) @NotNull(message = "工作空间ID不能为空") @RequestParam Long workspaceId,
        @Parameter(description = "父文件夹ID") @RequestParam(required = false) Long parentId,
        @Parameter(description = "文件夹名称", required = true) @NotNull(message = "文件夹名称不能为空") @RequestParam String folderName) {
        return R.ok(folderService.checkFolderExists(workspaceId, parentId, folderName));
    }

    /**
     * 检查文件夹是否有子文件夹
     */
    // @SaCheckPermission("coedit:folder:query")  // 协同编辑模块基础功能,允许所有登录用户访问
    @GetMapping("/{id}/hasChildren")
    @Operation(summary = "检查文件夹是否有子文件夹", description = "检查文件夹是否有子文件夹")
    public R<Boolean> hasChildren(
        @Parameter(description = "文件夹ID", required = true) @NotNull(message = "文件夹ID不能为空") @PathVariable Long id) {
        return R.ok(folderService.hasChildFolders(id));
    }

    /**
     * 检查文件夹是否有文件
     */
    // @SaCheckPermission("coedit:folder:query")  // 协同编辑模块基础功能,允许所有登录用户访问
    @GetMapping("/{id}/hasFiles")
    @Operation(summary = "检查文件夹是否有文件", description = "检查文件夹是否有文件")
    public R<Boolean> hasFiles(
        @Parameter(description = "文件夹ID", required = true) @NotNull(message = "文件夹ID不能为空") @PathVariable Long id) {
        return R.ok(folderService.hasFiles(id));
    }

}

