package com.xmzs.coedit.util;

import cn.dev33.satoken.stp.StpUtil;
import com.xmzs.coedit.domain.bo.CoWorkspaceBo;
import com.xmzs.coedit.domain.vo.CoWorkspaceVo;
import com.xmzs.coedit.service.ICoWorkspaceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 默认工作空间助手类
 * 用于自动为用户创建默认个人工作空间
 *
 * @author zuleed
 * @date 2025-11-06
 */
@Slf4j
@Component
public class DefaultWorkspaceHelper {

    private final ICoWorkspaceService workspaceService;

    public DefaultWorkspaceHelper(ICoWorkspaceService workspaceService) {
        this.workspaceService = workspaceService;
    }

    /**
     * 获取或创建用户的默认工作空间
     * <p>
     * 处理逻辑:
     * 1. 首先查询用户是否已有默认工作空间(owner_id = userId AND is_default = 1)
     * 2. 如果找到默认工作空间,直接返回该工作空间ID
     * 3. 如果没有找到默认工作空间,自动创建一个新的默认工作空间并设置 is_default = 1
     * </p>
     *
     * @param userId 用户ID
     * @param userName 用户昵称
     * @return 工作空间ID
     */
    public Long getOrCreateDefaultWorkspace(Long userId, String userName) {
        try {
            // 1. 查询用户是否已有默认工作空间 (owner_id = userId AND is_default = 1)
            CoWorkspaceVo defaultWorkspace = workspaceService.getDefaultWorkspaceByUserId(userId);

            if (defaultWorkspace != null) {
                log.debug("用户[{}]已存在默认工作空间,ID:{}", userId, defaultWorkspace.getId());
                return defaultWorkspace.getId();
            }

            // 2. 如果没有默认工作空间,则创建一个新的默认工作空间 (is_default = 1)
            log.info("用户[{}]没有默认工作空间,开始自动创建", userId);
            return createDefaultWorkspace(userId, userName);

        } catch (Exception e) {
            log.error("获取或创建默认工作空间失败,用户ID:{}", userId, e);
            throw new RuntimeException("获取或创建默认工作空间失败: " + e.getMessage());
        }
    }

    /**
     * 创建默认个人工作空间
     *
     * @param userId 用户ID
     * @param userName 用户昵称
     * @return 工作空间ID
     */
    private Long createDefaultWorkspace(Long userId, String userName) {
        // 构建默认工作空间配置
        CoWorkspaceBo workspaceBo = new CoWorkspaceBo();

        // 基本信息
        workspaceBo.setWorkspaceName(userName + "的工作空间");
        workspaceBo.setWorkspaceDesc("个人默认工作空间");
        workspaceBo.setWorkspaceIcon("/upload/workspace_icon/work_icons (1).jpg");
        workspaceBo.setWorkspaceColor("#2563eb");

        // 所有者信息
        workspaceBo.setOwnerId(userId);
        workspaceBo.setOwnerName(userName);

        // 工作空间类型和配置
        workspaceBo.setWorkspaceType(1); // 1-个人工作空间
        workspaceBo.setMaxStorage(5368709120L); // 5GB = 5 * 1024 * 1024 * 1024
        workspaceBo.setMaxMembers(1000);
        workspaceBo.setDefaultPermission("admin");

        // 权限设置
        workspaceBo.setAllowInvite(true);
        workspaceBo.setAllowShare(true);
        workspaceBo.setAllowDownload(true);

        // 状态设置
        workspaceBo.setStatus(1); // 启用
        workspaceBo.setIsDefault(true); // 标记为默认工作空间

        // 初始化统计字段
        workspaceBo.setUsedStorage(0L);
        workspaceBo.setMemberCount(1);

        try {
            // 调用Service创建工作空间
            CoWorkspaceVo createdWorkspace = workspaceService.createWorkspace(workspaceBo);

            log.info("成功为用户[{}]创建默认工作空间,ID:{}, 名称:{}",
                userId, createdWorkspace.getId(), createdWorkspace.getWorkspaceName());

            return createdWorkspace.getId();

        } catch (Exception e) {
            log.error("创建默认工作空间失败,用户ID:{}, 用户名:{}", userId, userName, e);
            throw new RuntimeException("创建默认工作空间失败: " + e.getMessage());
        }
    }

    /**
     * 从当前登录用户获取或创建默认工作空间
     * <p>
     * 便捷方法,自动从Sa-Token获取用户信息
     * <p>
     * 处理逻辑:
     * 1. 首先查询当前用户的默认工作空间(owner_id = userId AND is_default = 1)
     * 2. 如果找到默认工作空间,直接返回该工作空间ID
     * 3. 如果没有找到默认工作空间,自动创建一个新的默认工作空间并设置 is_default = 1
     * </p>
     *
     * @return 工作空间ID
     */
    public Long getOrCreateDefaultWorkspaceForCurrentUser() {
        try {
            // 获取当前登录用户ID
            Long userId = Long.valueOf(StpUtil.getLoginIdAsString().split(":")[1]);

            // 获取用户昵称 - 这里需要从用户服务获取,暂时使用用户ID作为默认值
            // TODO: 集成用户服务获取真实昵称
            String userName = getUserNickname(userId);

            return getOrCreateDefaultWorkspace(userId, userName);

        } catch (Exception e) {
            log.error("从当前登录用户获取或创建默认工作空间失败", e);
            throw new RuntimeException("获取默认工作空间失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户昵称
     * TODO: 需要集成用户服务来获取真实的用户昵称
     *
     * @param userId 用户ID
     * @return 用户昵称
     */
    private String getUserNickname(Long userId) {
        // 临时实现:使用用户ID作为昵称
        // 实际应该调用用户服务获取用户信息
        // 例如: return userService.getUserById(userId).getNickname();

        try {
            // 尝试从Sa-Token获取用户信息
            // 如果你的系统有用户服务,请替换为实际的调用
            return "用户" + userId;
        } catch (Exception e) {
            log.warn("获取用户昵称失败,使用默认值,用户ID:{}", userId);
            return "用户" + userId;
        }
    }

    /**
     * 检查用户是否有默认工作空间
     *
     * @param userId 用户ID
     * @return true-有默认工作空间, false-没有
     */
    public boolean hasDefaultWorkspace(Long userId) {
        try {
            CoWorkspaceVo defaultWorkspace = workspaceService.getDefaultWorkspaceByUserId(userId);
            return defaultWorkspace != null;
        } catch (Exception e) {
            log.error("检查用户默认工作空间失败,用户ID:{}", userId, e);
            return false;
        }
    }
}

