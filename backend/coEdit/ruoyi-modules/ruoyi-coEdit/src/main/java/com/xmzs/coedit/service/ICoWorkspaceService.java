package com.xmzs.coedit.service;

import com.xmzs.coedit.domain.bo.CoWorkspaceBo;
import com.xmzs.coedit.domain.vo.CoWorkspaceVo;
import com.xmzs.common.mybatis.core.page.PageQuery;
import com.xmzs.common.mybatis.core.page.TableDataInfo;

import java.util.Collection;
import java.util.List;

/**
 * 工作空间Service接口
 *
 * @author zuleed
 * @date 2025-11-04
 */
public interface ICoWorkspaceService {

    /**
     * 查询工作空间
     *
     * @param id 工作空间ID
     * @return 工作空间
     */
    CoWorkspaceVo queryById(Long id);

    /**
     * 查询工作空间列表（分页）
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 工作空间列表
     */
    TableDataInfo<CoWorkspaceVo> queryPageList(CoWorkspaceBo bo, PageQuery pageQuery);

    /**
     * 查询工作空间列表
     *
     * @param bo 查询条件
     * @return 工作空间列表
     */
    List<CoWorkspaceVo> queryList(CoWorkspaceBo bo);

    /**
     * 新增工作空间
     *
     * @param bo 工作空间业务对象
     * @return 是否成功
     */
    Boolean insertByBo(CoWorkspaceBo bo);

    /**
     * 修改工作空间
     *
     * @param bo 工作空间业务对象
     * @return 是否成功
     */
    Boolean updateByBo(CoWorkspaceBo bo);

    /**
     * 校验并批量删除工作空间信息
     *
     * @param ids     工作空间ID集合
     * @param isValid 是否校验
     * @return 是否成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    /**
     * 查询用户的工作空间列表
     *
     * @param userId 用户ID
     * @return 工作空间列表
     */
    List<CoWorkspaceVo> queryUserWorkspaces(Long userId);

    /**
     * 查询用户的默认工作空间
     *
     * @param userId 用户ID
     * @return 默认工作空间
     */
    CoWorkspaceVo queryUserDefaultWorkspace(Long userId);

    /**
     * 设置默认工作空间
     *
     * @param userId      用户ID
     * @param workspaceId 工作空间ID
     * @return 是否成功
     */
    Boolean setDefaultWorkspace(Long userId, Long workspaceId);

    /**
     * 更新工作空间存储使用量
     *
     * @param workspaceId 工作空间ID
     * @param sizeChange  大小变化（字节）
     * @return 是否成功
     */
    Boolean updateStorageUsage(Long workspaceId, Long sizeChange);

    /**
     * 更新工作空间成员数量
     *
     * @param workspaceId   工作空间ID
     * @param memberChange  成员数量变化
     * @return 是否成功
     */
    Boolean updateMemberCount(Long workspaceId, Integer memberChange);

    /**
     * 检查工作空间名称是否唯一
     *
     * @param bo 工作空间业务对象
     * @return 是否唯一
     */
    Boolean checkWorkspaceNameUnique(CoWorkspaceBo bo);

    /**
     * 检查存储空间是否足够
     *
     * @param workspaceId 工作空间ID
     * @param requiredSize 需要的空间大小（字节）
     * @return 是否足够
     */
    Boolean checkStorageAvailable(Long workspaceId, Long requiredSize);

    /**
     * 检查成员数量是否已满
     *
     * @param workspaceId 工作空间ID
     * @return 是否已满
     */
    Boolean checkMemberLimitReached(Long workspaceId);

    /**
     * 根据用户ID获取默认工作空间
     *
     * @param userId 用户ID
     * @return 默认工作空间,如果不存在返回null
     */
    CoWorkspaceVo getDefaultWorkspaceByUserId(Long userId);

    /**
     * 创建工作空间并返回Vo对象
     * 包含完整的工作空间初始化流程:
     * 1. 创建工作空间记录
     * 2. 添加所有者为成员
     * 3. 初始化根文件夹
     *
     * @param bo 工作空间业务对象
     * @return 创建的工作空间Vo
     */
    CoWorkspaceVo createWorkspace(CoWorkspaceBo bo);

    /**
     * 获取工作空间统计信息
     *
     * @param workspaceId 工作空间ID
     * @return 统计信息
     */
    java.util.Map<String, Object> getWorkspaceStats(Long workspaceId);
}

