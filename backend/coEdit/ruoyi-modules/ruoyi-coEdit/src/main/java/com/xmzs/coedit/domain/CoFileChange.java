package com.xmzs.coedit.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xmzs.common.mybatis.core.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文件变更记录对象 co_file_change
 *
 * @author zuleed
 * @date 2025-11-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("co_file_change")
@Schema(description = "文件变更记录对象")
public class CoFileChange extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 变更ID
     */
    @TableId(type = IdType.AUTO)
    @Schema(description = "变更ID")
    private Long id;

    /**
     * 历史ID
     */
    @Schema(description = "历史ID")
    private Long historyId;

    /**
     * 文件ID
     */
    @Schema(description = "文件ID")
    private Long fileId;

    /**
     * 版本号
     */
    @Schema(description = "版本号")
    private Integer version;

    /**
     * 变更类型：insert-插入 delete-删除 modify-修改
     */
    @Schema(description = "变更类型")
    private String changeType;

    /**
     * 变更位置
     */
    @Schema(description = "变更位置")
    private String position;

    /**
     * 变更内容
     */
    @Schema(description = "变更内容")
    private String content;

    /**
     * 变更前内容
     */
    @Schema(description = "变更前内容")
    private String oldContent;

    /**
     * 变更后内容
     */
    @Schema(description = "变更后内容")
    private String newContent;

    /**
     * 变更用户ID
     */
    @Schema(description = "变更用户ID")
    private Long userId;

    /**
     * 变更用户名称
     */
    @Schema(description = "变更用户名称")
    private String userName;

    /**
     * 变更数据（JSON）
     */
    @Schema(description = "变更数据（JSON）")
    private String changeData;
}

