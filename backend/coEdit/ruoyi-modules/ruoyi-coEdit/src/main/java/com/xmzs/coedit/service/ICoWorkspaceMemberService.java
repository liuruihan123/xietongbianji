package com.xmzs.coedit.service;

import com.xmzs.coedit.domain.bo.CoWorkspaceMemberBo;
import com.xmzs.coedit.domain.vo.CoWorkspaceMemberVo;
import com.xmzs.common.mybatis.core.page.PageQuery;
import com.xmzs.common.mybatis.core.page.TableDataInfo;

import java.util.Collection;
import java.util.List;

/**
 * 工作空间成员Service接口
 *
 * @author zuleed
 * @date 2025-11-04
 */
public interface ICoWorkspaceMemberService {

    /**
     * 查询工作空间成员
     *
     * @param id 成员ID
     * @return 工作空间成员
     */
    CoWorkspaceMemberVo queryById(Long id);

    /**
     * 查询工作空间成员列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 工作空间成员分页列表
     */
    TableDataInfo<CoWorkspaceMemberVo> queryPageList(CoWorkspaceMemberBo bo, PageQuery pageQuery);

    /**
     * 查询工作空间成员列表
     *
     * @param bo 查询条件
     * @return 工作空间成员列表
     */
    List<CoWorkspaceMemberVo> queryList(CoWorkspaceMemberBo bo);

    /**
     * 新增工作空间成员
     *
     * @param bo 工作空间成员业务对象
     * @return 是否成功
     */
    Boolean insertByBo(CoWorkspaceMemberBo bo);

    /**
     * 修改工作空间成员
     *
     * @param bo 工作空间成员业务对象
     * @return 是否成功
     */
    Boolean updateByBo(CoWorkspaceMemberBo bo);

    /**
     * 批量删除工作空间成员
     *
     * @param ids 成员ID集合
     * @return 是否成功
     */
    Boolean deleteByIds(Collection<Long> ids);

    /**
     * 查询工作空间的所有成员
     *
     * @param workspaceId 工作空间ID
     * @return 成员列表
     */
    List<CoWorkspaceMemberVo> queryByWorkspaceId(Long workspaceId);

    /**
     * 查询用户在工作空间中的成员信息
     *
     * @param workspaceId 工作空间ID
     * @param userId      用户ID
     * @return 成员信息
     */
    CoWorkspaceMemberVo queryByWorkspaceIdAndUserId(Long workspaceId, Long userId);

    /**
     * 邀请成员加入工作空间
     *
     * @param workspaceId 工作空间ID
     * @param userId      用户ID
     * @param memberRole  成员角色
     * @return 是否成功
     */
    Boolean inviteMember(Long workspaceId, Long userId, String memberRole);

    /**
     * 移除工作空间成员
     *
     * @param workspaceId 工作空间ID
     * @param userId      用户ID
     * @return 是否成功
     */
    Boolean removeMember(Long workspaceId, Long userId);

    /**
     * 更新成员角色
     *
     * @param id         成员ID
     * @param memberRole 新角色
     * @return 是否成功
     */
    Boolean updateMemberRole(Long id, String memberRole);

    /**
     * 更新成员权限
     *
     * @param id          成员ID
     * @param permissions 权限JSON
     * @return 是否成功
     */
    Boolean updateMemberPermissions(Long id, String permissions);

    /**
     * 更新最后访问时间
     *
     * @param workspaceId 工作空间ID
     * @param userId      用户ID
     * @return 是否成功
     */
    Boolean updateLastAccessTime(Long workspaceId, Long userId);

    /**
     * 检查用户是否为工作空间成员
     *
     * @param workspaceId 工作空间ID
     * @param userId      用户ID
     * @return 是否为成员
     */
    Boolean isMember(Long workspaceId, Long userId);

    /**
     * 检查用户是否为工作空间所有者
     *
     * @param workspaceId 工作空间ID
     * @param userId      用户ID
     * @return 是否为所有者
     */
    Boolean isOwner(Long workspaceId, Long userId);

    /**
     * 检查用户是否为工作空间管理员（包括所有者）
     *
     * @param workspaceId 工作空间ID
     * @param userId      用户ID
     * @return 是否为管理员
     */
    Boolean isAdmin(Long workspaceId, Long userId);

    /**
     * 获取工作空间成员数量
     *
     * @param workspaceId 工作空间ID
     * @return 成员数量
     */
    Integer getMemberCount(Long workspaceId);
}

