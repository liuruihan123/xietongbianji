package com.xmzs.coedit.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xmzs.common.mybatis.core.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 文件搜索历史实体
 *
 * @author zuleed
 * @date 2025-11-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("co_search_history")
@Schema(description = "文件搜索历史实体")
public class CoSearchHistory extends BaseEntity {

    /**
     * 搜索历史ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "搜索历史ID")
    private Long id;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 工作空间ID
     */
    @Schema(description = "工作空间ID")
    private Long workspaceId;

    /**
     * 搜索关键词
     */
    @Schema(description = "搜索关键词")
    private String keyword;

    /**
     * 搜索类型：1-文件名 2-内容 3-高级搜索
     */
    @Schema(description = "搜索类型")
    private Integer searchType;

    /**
     * 文件类型
     */
    @Schema(description = "文件类型")
    private String fileType;

    /**
     * 文件夹ID
     */
    @Schema(description = "文件夹ID")
    private Long folderId;

    /**
     * 最小文件大小（字节）
     */
    @Schema(description = "最小文件大小")
    private Long minSize;

    /**
     * 最大文件大小（字节）
     */
    @Schema(description = "最大文件大小")
    private Long maxSize;

    /**
     * 开始时间
     */
    @Schema(description = "开始时间")
    private Date startTime;

    /**
     * 结束时间
     */
    @Schema(description = "结束时间")
    private Date endTime;

    /**
     * 创建者ID
     */
    @Schema(description = "创建者ID")
    private Long creatorId;

    /**
     * 结果数量
     */
    @Schema(description = "结果数量")
    private Integer resultCount;

    /**
     * 搜索耗时（毫秒）
     */
    @Schema(description = "搜索耗时")
    private Integer searchTime;

    /**
     * IP地址
     */
    @Schema(description = "IP地址")
    private String ipAddress;

    /**
     * 用户代理
     */
    @Schema(description = "用户代理")
    private String userAgent;
}

