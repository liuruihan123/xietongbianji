package com.xmzs.coedit.controller;

import com.xmzs.coedit.domain.vo.FileTreeNodeVo;
import com.xmzs.coedit.domain.vo.FileTreeResponseVo;
import com.xmzs.coedit.service.ICoFileTreeService;
import com.xmzs.coedit.util.DefaultWorkspaceHelper;
import com.xmzs.common.core.domain.R;
import com.xmzs.common.satoken.utils.LoginHelper;
import com.xmzs.common.web.core.BaseController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 文件树管理
 *
 * @author zuleed
 * @date 2025-11-08
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/coedit/file-tree")
@Tag(name = "文件树管理", description = "文件树管理接口")
public class CoFileTreeController extends BaseController {

    private final ICoFileTreeService fileTreeService;
    private final DefaultWorkspaceHelper defaultWorkspaceHelper;

    /**
     * 获取工作空间的文件树(支持懒加载)
     * <p>
     * 处理逻辑:
     * 1. 如果传入了 workspaceId,则返回指定工作空间的文件树
     * 2. 如果未传入 workspaceId,则返回用户有权限访问的所有工作空间及其一级文件树（支持分页和筛选）
     * </p>
     *
     * @param workspaceId 工作空间ID(可选),为null时返回所有工作空间
     * @param parentId 父文件夹ID,为null时返回根级文件夹和文件,否则返回指定文件夹下的内容
     * @param workspaceType 工作空间类型(可选): 1-个人空间 2-部门空间 3-团队空间, null或0时忽略此条件
     * @param ownershipType 所有权类型(可选): 1-我创建的 2-我加入的, null或0时返回全部
     * @param pageNum 页码(可选),默认1,仅当workspaceId为null时生效
     * @param pageSize 每页大小(可选),默认10,仅当workspaceId为null时生效
     * @return 文件树响应数据
     */
    @Operation(summary = "获取工作空间的文件树(支持懒加载)",
               description = "workspaceId为null时返回所有工作空间及其一级文件树(支持分页和筛选),workspaceId有值时返回指定工作空间的文件树,parentId为null时返回根级,否则返回指定文件夹下的内容")
    @GetMapping("/workspace")
    public R<FileTreeResponseVo> getWorkspaceFileTree(
        @Parameter(description = "工作空间ID,为null时返回所有工作空间") @RequestParam(required = false) Long workspaceId,
        @Parameter(description = "父文件夹ID,为null时返回根级") @RequestParam(required = false) Long parentId,
        @Parameter(description = "工作空间类型: 1-个人空间 2-部门空间 3-团队空间") @RequestParam(required = false) Integer workspaceType,
        @Parameter(description = "所有权类型: 1-我创建的 2-我加入的") @RequestParam(required = false) Integer ownershipType,
        @Parameter(description = "页码,默认1") @RequestParam(required = false, defaultValue = "1") Integer pageNum,
        @Parameter(description = "每页大小,默认10") @RequestParam(required = false, defaultValue = "10") Integer pageSize) {

        Long userId = LoginHelper.getUserId();
        log.info("获取工作空间文件树: workspaceId={}, userId={}, parentId={}, workspaceType={}, ownershipType={}, pageNum={}, pageSize={}",
                 workspaceId, userId, parentId, workspaceType, ownershipType, pageNum, pageSize);

        FileTreeResponseVo response = fileTreeService.getFileTreeResponse(
            workspaceId, userId, parentId, workspaceType, ownershipType, pageNum, pageSize);
        return R.ok(response);
    }

    /**
     * 获取指定工作空间的文件树(路径参数方式)
     * 支持懒加载,parentId通过查询参数传递
     *
     * @param workspaceId 工作空间ID(路径参数)
     * @param parentId 父文件夹ID,为null时返回根级文件夹和文件
     * @return 当前层级的文件夹和文件列表
     */
    @Operation(summary = "获取指定工作空间的文件树(路径参数方式)",
               description = "通过路径参数指定工作空间ID,parentId为null时返回根级,否则返回指定文件夹下的内容")
    @GetMapping("/workspace/{workspaceId}")
    public R<List<FileTreeNodeVo>> getWorkspaceFileTreeByPath(
        @Parameter(description = "工作空间ID") @PathVariable Long workspaceId,
        @Parameter(description = "父文件夹ID,为null时返回根级") @RequestParam(required = false) Long parentId) {

        Long userId = LoginHelper.getUserId();
        log.info("获取工作空间文件树: workspaceId={}, userId={}, parentId={}", workspaceId, userId, parentId);

        List<FileTreeNodeVo> tree = fileTreeService.getFileTree(workspaceId, userId, parentId);
        return R.ok(tree);
    }

    /**
     * 获取文件夹下的文件树
     */
    @Operation(summary = "获取文件夹下的文件树")
    @GetMapping("/folder/{folderId}")
    public R<List<FileTreeNodeVo>> getFolderTree(
        @Parameter(description = "文件夹ID") @PathVariable Long folderId,
        @Parameter(description = "是否包含子文件夹") @RequestParam(defaultValue = "true") Boolean includeSubfolders) {

        Long userId = LoginHelper.getUserId();
        List<FileTreeNodeVo> tree = fileTreeService.getFolderTree(folderId, userId, includeSubfolders);
        return R.ok(tree);
    }

    /**
     * 获取用户的所有文件树
     */
    @Operation(summary = "获取用户的所有文件树")
    @GetMapping("/user")
    public R<List<FileTreeNodeVo>> getUserFileTree() {
        Long userId = LoginHelper.getUserId();
        List<FileTreeNodeVo> tree = fileTreeService.getUserFileTree(userId);
        return R.ok(tree);
    }

}

