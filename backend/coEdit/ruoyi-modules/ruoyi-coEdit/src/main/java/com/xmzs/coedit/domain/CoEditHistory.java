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
 * 编辑历史对象 co_edit_history
 *
 * @author zuleed
 * @date 2025-11-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("co_edit_history")
@Schema(description = "编辑历史对象")
public class CoEditHistory extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 历史ID
     */
    @Schema(description = "历史ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 文件ID
     */
    @Schema(description = "文件ID")
    private Long fileId;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 用户名称
     */
    @Schema(description = "用户名称")
    private String userName;

    /**
     * 会话Key
     */
    @Schema(description = "会话Key")
    private String sessionKey;

    /**
     * 操作类型：INSERT-插入 DELETE-删除 UPDATE-更新 FORMAT-格式化
     */
    @Schema(description = "操作类型")
    private String operationType;

    /**
     * 修改前内容
     */
    @Schema(description = "修改前内容")
    private String contentBefore;

    /**
     * 修改后内容
     */
    @Schema(description = "修改后内容")
    private String contentAfter;

    /**
     * 位置数据（JSON格式）
     */
    @Schema(description = "位置数据（JSON格式）")
    private String positionData;

    /**
     * 操作时间
     */
    @Schema(description = "操作时间")
    private Date operationTime;

}

