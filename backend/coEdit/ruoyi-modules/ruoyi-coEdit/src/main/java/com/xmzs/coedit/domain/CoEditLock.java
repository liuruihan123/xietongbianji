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
 * 编辑锁对象 co_edit_lock
 *
 * @author zuleed
 * @date 2025-11-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("co_edit_lock")
@Schema(description = "编辑锁对象")
public class CoEditLock extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 锁ID
     */
    @Schema(description = "锁ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 文件ID
     */
    @Schema(description = "文件ID")
    private Long fileId;

    /**
     * 持有锁的用户ID
     */
    @Schema(description = "持有锁的用户ID")
    private Long userId;

    /**
     * 用户名称
     */
    @Schema(description = "用户名称")
    private String userName;

    /**
     * 锁唯一标识
     */
    @Schema(description = "锁唯一标识")
    private String lockKey;

    /**
     * 锁类型：0-读锁 1-写锁
     */
    @Schema(description = "锁类型：0-读锁 1-写锁")
    private Integer lockType;

    /**
     * 锁范围：0-整个文档 1-段落 2-区域
     */
    @Schema(description = "锁范围：0-整个文档 1-段落 2-区域")
    private Integer lockScope;

    /**
     * 范围数据（JSON格式）
     */
    @Schema(description = "范围数据（JSON格式）")
    private String scopeData;

    /**
     * 获取锁时间
     */
    @Schema(description = "获取锁时间")
    private Date acquireTime;

    /**
     * 过期时间
     */
    @Schema(description = "过期时间")
    private Date expireTime;

    /**
     * 锁状态：0-活跃 1-已释放 2-已过期
     */
    @Schema(description = "锁状态：0-活跃 1-已释放 2-已过期")
    private Integer status;

}

