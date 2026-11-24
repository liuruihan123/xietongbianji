package com.xmzs.coedit.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xmzs.coedit.constant.WorkspaceConstants;
import com.xmzs.coedit.domain.CoWorkspaceMember;
import com.xmzs.coedit.domain.bo.CoWorkspaceMemberBo;
import com.xmzs.coedit.domain.vo.CoWorkspaceMemberVo;
import com.xmzs.coedit.mapper.CoWorkspaceMemberMapper;
import com.xmzs.coedit.service.ICoWorkspaceMemberService;
import com.xmzs.coedit.service.ICoWorkspaceService;
import com.xmzs.common.core.exception.ServiceException;
import com.xmzs.common.core.utils.MapstructUtils;
import com.xmzs.common.core.utils.StringUtils;
import com.xmzs.common.mybatis.core.page.PageQuery;
import com.xmzs.common.mybatis.core.page.TableDataInfo;
import com.xmzs.common.satoken.utils.LoginHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * 工作空间成员Service业务层处理
 *
 * @author zuleed
 * @date 2025-11-04
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class CoWorkspaceMemberServiceImpl implements ICoWorkspaceMemberService {

    private final CoWorkspaceMemberMapper baseMapper;
    private final ICoWorkspaceService workspaceService;

    /**
     * 查询工作空间成员
     */
    @Override
    public CoWorkspaceMemberVo queryById(Long id) {
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询工作空间成员列表
     */
    @Override
    public TableDataInfo<CoWorkspaceMemberVo> queryPageList(CoWorkspaceMemberBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<CoWorkspaceMember> lqw = buildQueryWrapper(bo);
        Page<CoWorkspaceMemberVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询工作空间成员列表
     */
    @Override
    public List<CoWorkspaceMemberVo> queryList(CoWorkspaceMemberBo bo) {
        LambdaQueryWrapper<CoWorkspaceMember> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    /**
     * 构建查询条件
     */
    private LambdaQueryWrapper<CoWorkspaceMember> buildQueryWrapper(CoWorkspaceMemberBo bo) {
        LambdaQueryWrapper<CoWorkspaceMember> lqw = Wrappers.lambdaQuery();
        lqw.eq(ObjectUtil.isNotNull(bo.getWorkspaceId()), CoWorkspaceMember::getWorkspaceId, bo.getWorkspaceId());
        lqw.eq(ObjectUtil.isNotNull(bo.getUserId()), CoWorkspaceMember::getUserId, bo.getUserId());
        lqw.like(StringUtils.isNotBlank(bo.getUserName()), CoWorkspaceMember::getUserName, bo.getUserName());
        lqw.eq(ObjectUtil.isNotNull(bo.getDeptId()), CoWorkspaceMember::getDeptId, bo.getDeptId());
        lqw.eq(StringUtils.isNotBlank(bo.getMemberRole()), CoWorkspaceMember::getMemberRole, bo.getMemberRole());
        lqw.eq(ObjectUtil.isNotNull(bo.getStatus()), CoWorkspaceMember::getStatus, bo.getStatus());
        lqw.orderByDesc(CoWorkspaceMember::getCreateTime);
        return lqw;
    }

    /**
     * 新增工作空间成员
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertByBo(CoWorkspaceMemberBo bo) {
        // 校验
        validEntityBeforeSave(bo);

        // 检查是否已经是成员
        CoWorkspaceMemberVo existMember = queryByWorkspaceIdAndUserId(bo.getWorkspaceId(), bo.getUserId());
        if (ObjectUtil.isNotNull(existMember)) {
            throw new ServiceException("该用户已经是工作空间成员");
        }

        // 检查成员数量限制
        if (workspaceService.checkMemberLimitReached(bo.getWorkspaceId())) {
            throw new ServiceException("工作空间成员数量已达上限");
        }

        CoWorkspaceMember add = MapstructUtils.convert(bo, CoWorkspaceMember.class);

        // 设置默认值
        if (ObjectUtil.isNull(add.getStatus())) {
            add.setStatus(WorkspaceConstants.MemberStatus.NORMAL);
        }
        if (ObjectUtil.isNull(add.getJoinTime())) {
            add.setJoinTime(new Date());
        }
        if (ObjectUtil.isNull(add.getAccessCount())) {
            add.setAccessCount(0);
        }

        boolean flag = baseMapper.insert(add) > 0;

        // 更新工作空间成员数量
        if (flag) {
            workspaceService.updateMemberCount(bo.getWorkspaceId(), 1);
        }

        return flag;
    }

    /**
     * 修改工作空间成员
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateByBo(CoWorkspaceMemberBo bo) {
        validEntityBeforeSave(bo);

        CoWorkspaceMember update = MapstructUtils.convert(bo, CoWorkspaceMember.class);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(CoWorkspaceMemberBo bo) {
        // 校验工作空间是否存在
        if (ObjectUtil.isNull(workspaceService.queryById(bo.getWorkspaceId()))) {
            throw new ServiceException("工作空间不存在");
        }

        // 校验角色
        if (StringUtils.isNotBlank(bo.getMemberRole())) {
            if (!WorkspaceConstants.MemberRole.isValid(bo.getMemberRole())) {
                throw new ServiceException("无效的成员角色");
            }
        }
    }

    /**
     * 批量删除工作空间成员
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteByIds(Collection<Long> ids) {
        // 检查是否包含所有者
        List<CoWorkspaceMember> members = baseMapper.selectBatchIds(ids);
        for (CoWorkspaceMember member : members) {
            if (WorkspaceConstants.MemberRole.OWNER.equals(member.getMemberRole())) {
                throw new ServiceException("不能删除工作空间所有者");
            }
        }

        boolean flag = baseMapper.deleteBatchIds(ids) > 0;

        // 更新工作空间成员数量
        if (flag && !members.isEmpty()) {
            Long workspaceId = members.get(0).getWorkspaceId();
            workspaceService.updateMemberCount(workspaceId, -ids.size());
        }

        return flag;
    }

    /**
     * 查询工作空间的所有成员
     */
    @Override
    public List<CoWorkspaceMemberVo> queryByWorkspaceId(Long workspaceId) {
        LambdaQueryWrapper<CoWorkspaceMember> lqw = Wrappers.lambdaQuery();
        lqw.eq(CoWorkspaceMember::getWorkspaceId, workspaceId);
        lqw.eq(CoWorkspaceMember::getStatus, WorkspaceConstants.MemberStatus.NORMAL);
        lqw.orderByAsc(CoWorkspaceMember::getMemberRole);
        lqw.orderByDesc(CoWorkspaceMember::getCreateTime);
        return baseMapper.selectVoList(lqw);
    }

    /**
     * 查询用户在工作空间中的成员信息
     */
    @Override
    public CoWorkspaceMemberVo queryByWorkspaceIdAndUserId(Long workspaceId, Long userId) {
        LambdaQueryWrapper<CoWorkspaceMember> lqw = Wrappers.lambdaQuery();
        lqw.eq(CoWorkspaceMember::getWorkspaceId, workspaceId);
        lqw.eq(CoWorkspaceMember::getUserId, userId);
        lqw.eq(CoWorkspaceMember::getStatus, WorkspaceConstants.MemberStatus.NORMAL);
        return baseMapper.selectVoOne(lqw);
    }

    /**
     * 邀请成员加入工作空间
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean inviteMember(Long workspaceId, Long userId, String memberRole) {
        // 获取当前用户
        Long currentUserId = LoginHelper.getUserId();

        // 检查当前用户是否有权限邀请：是工作空间成员中的管理员，或者是工作空间创建者
        boolean hasPermission = isAdmin(workspaceId, currentUserId);
        if (!hasPermission) {
            // 检查是否是工作空间创建者
            var workspace = workspaceService.queryById(workspaceId);
            if (workspace != null && currentUserId.equals(workspace.getOwnerId())) {
                hasPermission = true;
            }
        }

        if (!hasPermission) {
            throw new ServiceException("您没有权限邀请成员");
        }

        // 创建成员Bo
        CoWorkspaceMemberBo bo = new CoWorkspaceMemberBo();
        bo.setWorkspaceId(workspaceId);
        bo.setUserId(userId);
        bo.setMemberRole(memberRole);
        bo.setInviteBy(currentUserId);
        bo.setInviteTime(new Date());
        bo.setJoinType(WorkspaceConstants.JoinType.INVITE);

        return insertByBo(bo);
    }

    /**
     * 移除工作空间成员
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean removeMember(Long workspaceId, Long userId) {
        // 获取当前用户
        Long currentUserId = LoginHelper.getUserId();

        // 检查当前用户是否有权限移除成员：是工作空间成员中的管理员，或者是工作空间创建者
        boolean hasPermission = isAdmin(workspaceId, currentUserId);
        if (!hasPermission) {
            // 检查是否是工作空间创建者
            var workspace = workspaceService.queryById(workspaceId);
            if (workspace != null && currentUserId.equals(workspace.getOwnerId())) {
                hasPermission = true;
            }
        }

        if (!hasPermission) {
            throw new ServiceException("您没有权限移除成员");
        }

        // 查询成员信息
        CoWorkspaceMemberVo member = queryByWorkspaceIdAndUserId(workspaceId, userId);
        if (ObjectUtil.isNull(member)) {
            throw new ServiceException("成员不存在");
        }

        // 不能移除所有者
        if (WorkspaceConstants.MemberRole.OWNER.equals(member.getMemberRole())) {
            throw new ServiceException("不能移除工作空间所有者");
        }

        // 更新状态为已移除
        LambdaUpdateWrapper<CoWorkspaceMember> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.eq(CoWorkspaceMember::getId, member.getId());
        updateWrapper.set(CoWorkspaceMember::getStatus, WorkspaceConstants.MemberStatus.REMOVED);

        boolean flag = baseMapper.update(null, updateWrapper) > 0;

        // 更新工作空间成员数量
        if (flag) {
            workspaceService.updateMemberCount(workspaceId, -1);
        }

        return flag;
    }

    /**
     * 更新成员角色
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateMemberRole(Long id, String memberRole) {
        // 校验角色
        if (!WorkspaceConstants.MemberRole.isValid(memberRole)) {
            throw new ServiceException("无效的成员角色");
        }

        // 查询成员信息
        CoWorkspaceMember member = baseMapper.selectById(id);
        if (ObjectUtil.isNull(member)) {
            throw new ServiceException("成员不存在");
        }

        // 不能修改所有者角色
        if (WorkspaceConstants.MemberRole.OWNER.equals(member.getMemberRole())) {
            throw new ServiceException("不能修改所有者角色");
        }

        // 获取当前用户
        Long currentUserId = LoginHelper.getUserId();

        // 检查当前用户是否有权限
        if (!isAdmin(member.getWorkspaceId(), currentUserId)) {
            throw new ServiceException("您没有权限修改成员角色");
        }

        LambdaUpdateWrapper<CoWorkspaceMember> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.eq(CoWorkspaceMember::getId, id);
        updateWrapper.set(CoWorkspaceMember::getMemberRole, memberRole);

        return baseMapper.update(null, updateWrapper) > 0;
    }

    /**
     * 更新成员权限
     */
    @Override
    public Boolean updateMemberPermissions(Long id, String permissions) {
        LambdaUpdateWrapper<CoWorkspaceMember> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.eq(CoWorkspaceMember::getId, id);
        updateWrapper.set(CoWorkspaceMember::getPermissions, permissions);

        return baseMapper.update(null, updateWrapper) > 0;
    }

    /**
     * 更新最后访问时间
     */
    @Override
    public Boolean updateLastAccessTime(Long workspaceId, Long userId) {
        LambdaUpdateWrapper<CoWorkspaceMember> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.eq(CoWorkspaceMember::getWorkspaceId, workspaceId);
        updateWrapper.eq(CoWorkspaceMember::getUserId, userId);
        updateWrapper.set(CoWorkspaceMember::getLastAccessTime, new Date());
        updateWrapper.setSql("access_count = access_count + 1");

        return baseMapper.update(null, updateWrapper) > 0;
    }

    /**
     * 检查用户是否为工作空间成员
     */
    @Override
    public Boolean isMember(Long workspaceId, Long userId) {
        CoWorkspaceMemberVo member = queryByWorkspaceIdAndUserId(workspaceId, userId);
        return ObjectUtil.isNotNull(member);
    }

    /**
     * 检查用户是否为工作空间所有者
     */
    @Override
    public Boolean isOwner(Long workspaceId, Long userId) {
        CoWorkspaceMemberVo member = queryByWorkspaceIdAndUserId(workspaceId, userId);
        if (ObjectUtil.isNull(member)) {
            return false;
        }
        return WorkspaceConstants.MemberRole.OWNER.equals(member.getMemberRole());
    }

    /**
     * 检查用户是否为工作空间管理员（包括所有者）
     */
    @Override
    public Boolean isAdmin(Long workspaceId, Long userId) {
        CoWorkspaceMemberVo member = queryByWorkspaceIdAndUserId(workspaceId, userId);
        if (ObjectUtil.isNull(member)) {
            return false;
        }
        String role = member.getMemberRole();
        return WorkspaceConstants.MemberRole.OWNER.equals(role)
            || WorkspaceConstants.MemberRole.ADMIN.equals(role);
    }

    /**
     * 获取工作空间成员数量
     */
    @Override
    public Integer getMemberCount(Long workspaceId) {
        LambdaQueryWrapper<CoWorkspaceMember> lqw = Wrappers.lambdaQuery();
        lqw.eq(CoWorkspaceMember::getWorkspaceId, workspaceId);
        lqw.eq(CoWorkspaceMember::getStatus, WorkspaceConstants.MemberStatus.NORMAL);
        return Math.toIntExact(baseMapper.selectCount(lqw));
    }
}

