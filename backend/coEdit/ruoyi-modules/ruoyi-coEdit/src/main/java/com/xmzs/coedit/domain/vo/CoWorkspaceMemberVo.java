package com.xmzs.coedit.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.xmzs.coedit.domain.CoWorkspaceMember;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 工作空间成员视图对象
 *
 * @author zuleed
 * @date 2025-11-04
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = CoWorkspaceMember.class)
@Schema(description = "工作空间成员视图对象")
public class CoWorkspaceMemberVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 成员ID
     */
    @ExcelProperty(value = "成员ID")
    @Schema(description = "成员ID")
    private Long id;

    /**
     * 工作空间ID
     */
    @ExcelProperty(value = "工作空间ID")
    @Schema(description = "工作空间ID")
    private Long workspaceId;

    /**
     * 用户ID
     */
    @ExcelProperty(value = "用户ID")
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 用户名称
     */
    @ExcelProperty(value = "用户名称")
    @Schema(description = "用户名称")
    private String userName;

    /**
     * 部门ID
     */
    @ExcelProperty(value = "部门ID")
    @Schema(description = "部门ID")
    private Long deptId;

    /**
     * 部门名称
     */
    @ExcelProperty(value = "部门名称")
    @Schema(description = "部门名称")
    private String deptName;

    /**
     * 成员角色
     */
    @ExcelProperty(value = "成员角色")
    @Schema(description = "成员角色")
    private String memberRole;

    /**
     * 权限列表（JSON格式）
     */
    @Schema(description = "权限列表")
    private String permissions;

    /**
     * 邀请人ID
     */
    @ExcelProperty(value = "邀请人ID")
    @Schema(description = "邀请人ID")
    private Long inviteBy;

    /**
     * 邀请时间
     */
    @ExcelProperty(value = "邀请时间")
    @Schema(description = "邀请时间")
    private Date inviteTime;

    /**
     * 加入时间
     */
    @ExcelProperty(value = "加入时间")
    @Schema(description = "加入时间")
    private Date joinTime;

    /**
     * 加入方式
     */
    @ExcelProperty(value = "加入方式")
    @Schema(description = "加入方式")
    private Integer joinType;

    /**
     * 状态
     */
    @ExcelProperty(value = "状态")
    @Schema(description = "状态")
    private Integer status;

    /**
     * 最后访问时间
     */
    @ExcelProperty(value = "最后访问时间")
    @Schema(description = "最后访问时间")
    private Date lastAccessTime;

    /**
     * 访问次数
     */
    @ExcelProperty(value = "访问次数")
    @Schema(description = "访问次数")
    private Integer accessCount;

    /**
     * 创建时间
     */
    @ExcelProperty(value = "创建时间")
    @Schema(description = "创建时间")
    private Date createTime;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    @Schema(description = "备注")
    private String remark;
}

