package com.xmzs.coedit.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.xmzs.coedit.domain.bo.CoWorkspaceMemberBo;
import com.xmzs.coedit.domain.vo.CoWorkspaceMemberVo;
import com.xmzs.coedit.service.ICoWorkspaceMemberService;
import com.xmzs.common.core.domain.R;
import com.xmzs.common.core.validate.AddGroup;
import com.xmzs.common.core.validate.EditGroup;
import com.xmzs.common.log.annotation.Log;
import com.xmzs.common.log.enums.BusinessType;
import com.xmzs.common.mybatis.core.page.PageQuery;
import com.xmzs.common.mybatis.core.page.TableDataInfo;
import com.xmzs.common.web.core.BaseController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 工作空间成员管理Controller
 *
 * @author zuleed
 * @date 2025-11-04
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/coedit/workspace/member")
@Tag(name = "工作空间成员管理", description = "工作空间成员管理接口")
public class CoWorkspaceMemberController extends BaseController {

    private final ICoWorkspaceMemberService memberService;

    /**
     * 查询工作空间成员列表
     */
//    @SaCheckPermission("coedit:workspace:member:list")
    @GetMapping("/list")
    @Operation(summary = "查询工作空间成员列表", description = "分页查询工作空间成员列表")
    public TableDataInfo<CoWorkspaceMemberVo> list(CoWorkspaceMemberBo bo, PageQuery pageQuery) {
        return memberService.queryPageList(bo, pageQuery);
    }

    /**
     * 查询工作空间的所有成员
     */
    @GetMapping("/workspace/{workspaceId}")
    @Operation(summary = "查询工作空间的所有成员", description = "查询指定工作空间的所有成员")
    public R<List<CoWorkspaceMemberVo>> getWorkspaceMembers(
        @Parameter(description = "工作空间ID", required = true)
        @PathVariable Long workspaceId) {
        List<CoWorkspaceMemberVo> list = memberService.queryByWorkspaceId(workspaceId);
        return R.ok(list);
    }

    /**
     * 获取工作空间成员详细信息
     */
//    @SaCheckPermission("coedit:workspace:member:query")
    @GetMapping("/{id}")
    @Operation(summary = "获取工作空间成员详细信息", description = "根据成员ID获取详细信息")
    public R<CoWorkspaceMemberVo> getInfo(
        @Parameter(description = "成员ID", required = true)
        @PathVariable Long id) {
        return R.ok(memberService.queryById(id));
    }

    /**
     * 新增工作空间成员
     */
//    @SaCheckPermission("coedit:workspace:member:add")
    @Log(title = "工作空间成员", businessType = BusinessType.INSERT)
    @PostMapping()
    @Operation(summary = "新增工作空间成员", description = "添加新的工作空间成员")
    public R<Void> add(@Validated(AddGroup.class) @RequestBody CoWorkspaceMemberBo bo) {
        return toAjax(memberService.insertByBo(bo));
    }

    /**
     * 修改工作空间成员
     */
//    @SaCheckPermission("coedit:workspace:member:edit")
    @Log(title = "工作空间成员", businessType = BusinessType.UPDATE)
    @PutMapping()
    @Operation(summary = "修改工作空间成员", description = "修改工作空间成员信息")
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody CoWorkspaceMemberBo bo) {
        return toAjax(memberService.updateByBo(bo));
    }

    /**
     * 删除工作空间成员
     */
//    @SaCheckPermission("coedit:workspace:member:remove")
    @Log(title = "工作空间成员", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    @Operation(summary = "删除工作空间成员", description = "批量删除工作空间成员")
    public R<Void> remove(
        @Parameter(description = "成员ID数组", required = true)
        @PathVariable Long[] ids) {
        return toAjax(memberService.deleteByIds(Arrays.asList(ids)));
    }

    /**
     * 邀请成员加入工作空间
     */
//    @SaCheckPermission("coedit:workspace:member:invite")
    @Log(title = "邀请成员", businessType = BusinessType.INSERT)
    @PostMapping("/invite")
    @Operation(summary = "邀请成员加入工作空间", description = "邀请用户加入指定工作空间")
    public R<Void> inviteMember(
        @Parameter(description = "工作空间ID", required = true)
        @RequestParam Long workspaceId,
        @Parameter(description = "用户ID", required = true)
        @RequestParam Long userId,
        @Parameter(description = "成员角色", required = true)
        @RequestParam String memberRole) {
        return toAjax(memberService.inviteMember(workspaceId, userId, memberRole));
    }

    /**
     * 移除工作空间成员
     */
//    @SaCheckPermission("coedit:workspace:member:remove")
    @Log(title = "移除成员", businessType = BusinessType.DELETE)
    @DeleteMapping("/remove")
    @Operation(summary = "移除工作空间成员", description = "从工作空间中移除指定成员")
    public R<Void> removeMember(
        @Parameter(description = "工作空间ID", required = true)
        @RequestParam Long workspaceId,
        @Parameter(description = "用户ID", required = true)
        @RequestParam Long userId) {
        return toAjax(memberService.removeMember(workspaceId, userId));
    }

    /**
     * 更新成员角色
     */
//    @SaCheckPermission("coedit:workspace:member:edit")
    @Log(title = "更新成员角色", businessType = BusinessType.UPDATE)
    @PutMapping("/role")
    @Operation(summary = "更新成员角色", description = "修改工作空间成员的角色")
    public R<Void> updateRole(
        @Parameter(description = "成员ID", required = true)
        @RequestParam Long id,
        @Parameter(description = "新角色", required = true)
        @RequestParam String memberRole) {
        return toAjax(memberService.updateMemberRole(id, memberRole));
    }

    /**
     * 更新成员权限
     */
//    @SaCheckPermission("coedit:workspace:member:edit")
    @Log(title = "更新成员权限", businessType = BusinessType.UPDATE)
    @PutMapping("/permissions")
    @Operation(summary = "更新成员权限", description = "修改工作空间成员的权限")
    public R<Void> updatePermissions(
        @Parameter(description = "成员ID", required = true)
        @RequestParam Long id,
        @Parameter(description = "权限JSON", required = true)
        @RequestParam String permissions) {
        return toAjax(memberService.updateMemberPermissions(id, permissions));
    }

    /**
     * 检查用户是否为工作空间成员
     */
    @GetMapping("/check/member")
    @Operation(summary = "检查用户是否为工作空间成员", description = "验证用户是否为指定工作空间的成员")
    public R<Boolean> checkMember(
        @Parameter(description = "工作空间ID", required = true)
        @RequestParam Long workspaceId,
        @Parameter(description = "用户ID", required = true)
        @RequestParam Long userId) {
        return R.ok(memberService.isMember(workspaceId, userId));
    }

    /**
     * 检查用户是否为工作空间所有者
     */
    @GetMapping("/check/owner")
    @Operation(summary = "检查用户是否为工作空间所有者", description = "验证用户是否为指定工作空间的所有者")
    public R<Boolean> checkOwner(
        @Parameter(description = "工作空间ID", required = true)
        @RequestParam Long workspaceId,
        @Parameter(description = "用户ID", required = true)
        @RequestParam Long userId) {
        return R.ok(memberService.isOwner(workspaceId, userId));
    }

    /**
     * 检查用户是否为工作空间管理员
     */
    @GetMapping("/check/admin")
    @Operation(summary = "检查用户是否为工作空间管理员", description = "验证用户是否为指定工作空间的管理员")
    public R<Boolean> checkAdmin(
        @Parameter(description = "工作空间ID", required = true)
        @RequestParam Long workspaceId,
        @Parameter(description = "用户ID", required = true)
        @RequestParam Long userId) {
        return R.ok(memberService.isAdmin(workspaceId, userId));
    }

    /**
     * 获取工作空间成员数量
     */
    @GetMapping("/count/{workspaceId}")
    @Operation(summary = "获取工作空间成员数量", description = "查询指定工作空间的成员数量")
    public R<Integer> getMemberCount(
        @Parameter(description = "工作空间ID", required = true)
        @PathVariable Long workspaceId) {
        return R.ok(memberService.getMemberCount(workspaceId));
    }
}

