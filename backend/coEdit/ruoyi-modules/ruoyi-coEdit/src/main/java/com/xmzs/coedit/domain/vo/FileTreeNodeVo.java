package com.xmzs.coedit.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 文件树节点视图对象
 *
 * @author zuleed
 * @date 2025-11-08
 */
@Data
@Schema(description = "文件树节点视图对象")
public class FileTreeNodeVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 节点类型: workspace-工作空间 folder-文件夹 file-文件
     */
    @Schema(description = "节点类型: workspace-工作空间 folder-文件夹 file-文件")
    private String nodeType;

    /**
     * 节点ID
     */
    @Schema(description = "节点ID")
    private Long id;

    /**
     * 节点名称
     */
    @Schema(description = "节点名称")
    private String name;

    /**
     * 父节点ID
     */
    @Schema(description = "父节点ID")
    private Long parentId;

    /**
     * 节点路径
     */
    @Schema(description = "节点路径")
    private String path;

    /**
     * 节点层级
     */
    @Schema(description = "节点层级")
    private Integer level;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private Date createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private Date updateTime;

    /**
     * 工作空间详细信息(当nodeType=workspace时)
     */
    @Schema(description = "工作空间详细信息")
    private CoWorkspaceVo workspaceInfo;

    /**
     * 文件夹详细信息(当nodeType=folder时)
     */
    @Schema(description = "文件夹详细信息")
    private CoFolderVo folderInfo;

    /**
     * 文件详细信息(当nodeType=file时)
     */
    @Schema(description = "文件详细信息")
    private CoFileVo fileInfo;

    /**
     * 子节点列表(仅文件夹节点有)
     */
    @Schema(description = "子节点列表")
    private List<FileTreeNodeVo> children = new ArrayList<>();

    /**
     * 直接子文件数量(仅文件夹节点有)
     */
    @Schema(description = "直接子文件数量")
    private Integer fileCount;

    /**
     * 直接子文件夹数量(仅文件夹节点有)
     */
    @Schema(description = "直接子文件夹数量")
    private Integer folderCount;

    /**
     * 包含所有子文件夹的文件总数(仅文件夹节点有)
     */
    @Schema(description = "包含所有子文件夹的文件总数")
    private Integer totalFileCount;

    /**
     * 是否已收藏
     */
    @Schema(description = "是否已收藏")
    private Boolean isFavorited;

    /**
     * 最后访问时间(仅文件节点有)
     */
    @Schema(description = "最后访问时间")
    private Date lastAccessTime;

    /**
     * 是否有子节点(仅文件夹节点有)
     * 用于前端判断是否显示展开图标,支持懒加载
     */
    @Schema(description = "是否有子节点")
    private Boolean hasChildren;

    /**
     * 所属工作空间ID
     * 用于前端区分文件/文件夹属于哪个工作空间
     */
    @Schema(description = "所属工作空间ID")
    private Long workspaceId;

    /**
     * 所属工作空间名称
     * 用于前端显示文件/文件夹所在的工作空间
     */
    @Schema(description = "所属工作空间名称")
    private String workspaceName;

}

