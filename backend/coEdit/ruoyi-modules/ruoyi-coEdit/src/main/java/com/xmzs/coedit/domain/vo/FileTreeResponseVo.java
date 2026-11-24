package com.xmzs.coedit.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件树响应视图对象
 * 用于返回工作空间列表和文件树的组合数据
 *
 * @author zuleed
 * @date 2025-11-13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "文件树响应视图对象")
public class FileTreeResponseVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 工作空间列表
     * 当 workspaceId 为 null 时，包含用户有权限访问的所有工作空间
     * 当 workspaceId 有值时，可以为空列表或包含当前工作空间
     */
    @Schema(description = "工作空间列表")
    private List<CoWorkspaceVo> workspaces = new ArrayList<>();

    /**
     * 文件树节点列表
     * 当 workspaceId 为 null 时，包含所有工作空间下的一级文件和文件夹（分页）
     * 当 workspaceId 有值时，包含该工作空间下的文件树（根据 parentId 决定层级）
     */
    @Schema(description = "文件树节点列表")
    private List<FileTreeNodeVo> fileTree = new ArrayList<>();

    /**
     * 总记录数（仅当 workspaceId 为 null 时有值）
     */
    @Schema(description = "总记录数")
    private Long total;

    /**
     * 当前页码（仅当 workspaceId 为 null 时有值）
     */
    @Schema(description = "当前页码")
    private Integer pageNum;

    /**
     * 每页大小（仅当 workspaceId 为 null 时有值）
     */
    @Schema(description = "每页大小")
    private Integer pageSize;

    /**
     * 总页数（仅当 workspaceId 为 null 时有值）
     */
    @Schema(description = "总页数")
    private Integer pages;

    /**
     * 便捷构造方法：只包含文件树（不分页）
     */
    public static FileTreeResponseVo ofFileTree(List<FileTreeNodeVo> fileTree) {
        FileTreeResponseVo response = new FileTreeResponseVo();
        response.setFileTree(fileTree);
        return response;
    }

    /**
     * 便捷构造方法：包含工作空间和文件树（不分页）
     */
    public static FileTreeResponseVo of(List<CoWorkspaceVo> workspaces, List<FileTreeNodeVo> fileTree) {
        FileTreeResponseVo response = new FileTreeResponseVo();
        response.setWorkspaces(workspaces);
        response.setFileTree(fileTree);
        return response;
    }

    /**
     * 便捷构造方法：包含工作空间和文件树（分页）
     */
    public static FileTreeResponseVo ofPage(List<CoWorkspaceVo> workspaces, List<FileTreeNodeVo> fileTree,
                                             Long total, Integer pageNum, Integer pageSize) {
        FileTreeResponseVo response = new FileTreeResponseVo();
        response.setWorkspaces(workspaces);
        response.setFileTree(fileTree);
        response.setTotal(total);
        response.setPageNum(pageNum);
        response.setPageSize(pageSize);
        // 计算总页数
        if (total != null && pageSize != null && pageSize > 0) {
            response.setPages((int) Math.ceil((double) total / pageSize));
        }
        return response;
    }

}

