package com.xmzs.coedit.domain.bo;

import com.xmzs.coedit.domain.CoWorkspaceMember;
import com.xmzs.common.core.validate.AddGroup;
import com.xmzs.common.core.validate.EditGroup;
import com.xmzs.common.mybatis.core.domain.BaseEntity;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 工作空间成员业务对象
 *
 * @author zuleed
 * @date 2025-11-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = CoWorkspaceMember.class, reverseConvertGenerate = false)
@Schema(description = "工作空间成员业务对象")
public class CoWorkspaceMemberBo extends BaseEntity {

    /**
     * 成员ID
     */
    @NotNull(message = "成员ID不能为空", groups = {EditGroup.class})
    @Schema(description = "成员ID")
    private Long id;

    /**
     * 工作空间ID
     */
    @NotNull(message = "工作空间ID不能为空", groups = {AddGroup.class, EditGroup.class})
    @Schema(description = "工作空间ID")
    private Long workspaceId;

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空", groups = {AddGroup.class})
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 用户名称
     */
    @Size(max = 100, message = "用户名称长度不能超过{max}个字符")
    @Schema(description = "用户名称")
    private String userName;

    /**
     * 部门ID
     */
    @Schema(description = "部门ID")
    private Long deptId;

    /**
     * 部门名称
     */
    @Size(max = 100, message = "部门名称长度不能超过{max}个字符")
    @Schema(description = "部门名称")
    private String deptName;

    /**
     * 成员角色：owner-所有者 admin-管理员 editor-编辑者 viewer-查看者 member-普通成员
     */
    @NotBlank(message = "成员角色不能为空", groups = {AddGroup.class, EditGroup.class})
    @Size(max = 20, message = "成员角色长度不能超过{max}个字符")
    @Schema(description = "成员角色")
    private String memberRole;

    /**
     * 权限列表（JSON格式）
     */
    @Size(max = 500, message = "权限列表长度不能超过{max}个字符")
    @Schema(description = "权限列表")
    private String permissions;

    /**
     * 邀请人ID
     */
    @Schema(description = "邀请人ID")
    private Long inviteBy;

    /**
     * 邀请时间
     */
    @Schema(description = "邀请时间")
    private Date inviteTime;

    /**
     * 加入时间
     */
    @Schema(description = "加入时间")
    private Date joinTime;

    /**
     * 加入方式：1-邀请 2-申请 3-系统添加
     */
    @Schema(description = "加入方式")
    private Integer joinType;

    /**
     * 状态：0-已移除 1-正常 2-已禁用
     */
    @Schema(description = "状态")
    private Integer status;

    /**
     * 最后访问时间
     */
    @Schema(description = "最后访问时间")
    private Date lastAccessTime;

    /**
     * 访问次数
     */
    @Schema(description = "访问次数")
    private Integer accessCount;
}

