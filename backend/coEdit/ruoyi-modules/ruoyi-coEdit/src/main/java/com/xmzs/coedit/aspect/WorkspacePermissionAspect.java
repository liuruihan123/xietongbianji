package com.xmzs.coedit.aspect;

import cn.hutool.core.util.ObjectUtil;
import com.xmzs.coedit.annotation.WorkspacePermission;
import com.xmzs.coedit.constant.WorkspaceConstants;
import com.xmzs.coedit.domain.vo.CoWorkspaceMemberVo;
import com.xmzs.coedit.service.ICoWorkspaceMemberService;
import com.xmzs.common.core.exception.ServiceException;
import com.xmzs.common.satoken.utils.LoginHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * 工作空间权限验证切面
 * <p>
 * 拦截带有 @WorkspacePermission 注解的方法或类
 * 验证当前用户是否具有访问指定工作空间的权限
 *
 * @author zuleed
 * @date 2025-11-04
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class WorkspacePermissionAspect {

    private final ICoWorkspaceMemberService memberService;

    /**
     * 在方法执行前进行工作空间权限验证
     */
    @Before("@within(com.xmzs.coedit.annotation.WorkspacePermission) || @annotation(com.xmzs.coedit.annotation.WorkspacePermission)")
    public void doBefore(JoinPoint joinPoint) {
        try {
            // 获取当前登录用户
            Long userId = LoginHelper.getUserId();
            if (ObjectUtil.isNull(userId)) {
                throw new ServiceException("用户未登录，请先登录");
            }

            // 获取注解信息
            WorkspacePermission permission = getWorkspacePermission(joinPoint);
            if (ObjectUtil.isNull(permission)) {
                return;
            }

            // 获取工作空间ID
            Long workspaceId = getWorkspaceId(joinPoint, permission.workspaceIdParam());
            if (ObjectUtil.isNull(workspaceId)) {
                log.warn("无法从方法参数中获取工作空间ID，参数名: {}", permission.workspaceIdParam());
                throw new ServiceException("工作空间ID参数缺失");
            }

            // 查询用户在工作空间中的成员信息
            CoWorkspaceMemberVo member = memberService.queryByWorkspaceIdAndUserId(workspaceId, userId);
            if (ObjectUtil.isNull(member)) {
                log.warn("用户 {} 不是工作空间 {} 的成员", userId, workspaceId);
                throw new ServiceException(permission.message());
            }

            // 检查成员状态
            if (WorkspaceConstants.MemberStatus.NORMAL!=member.getStatus()) {
                log.warn("用户 {} 在工作空间 {} 中的状态异常: {}", userId, workspaceId, member.getStatus());
                throw new ServiceException("您在此工作空间中的状态异常，无法访问");
            }

            // 验证角色权限
            String memberRole = member.getMemberRole();
            if (permission.requireOwner()) {
                // 需要所有者权限
                if (!WorkspaceConstants.MemberRole.OWNER.equals(memberRole)) {
                    log.warn("用户 {} 不是工作空间 {} 的所有者，当前角色: {}", userId, workspaceId, memberRole);
                    throw new ServiceException("此操作需要工作空间所有者权限");
                }
            } else if (permission.requireAdmin()) {
                // 需要管理员权限（owner或admin）
                if (!WorkspaceConstants.MemberRole.OWNER.equals(memberRole)
                    && !WorkspaceConstants.MemberRole.ADMIN.equals(memberRole)) {
                    log.warn("用户 {} 不是工作空间 {} 的管理员，当前角色: {}", userId, workspaceId, memberRole);
                    throw new ServiceException("此操作需要工作空间管理员权限");
                }
            } else {
                // 检查角色级别
                if (!checkRoleLevel(memberRole, permission.requiredRole())) {
                    log.warn("用户 {} 在工作空间 {} 中的角色 {} 不满足要求的角色 {}",
                        userId, workspaceId, memberRole, permission.requiredRole());
                    throw new ServiceException(permission.message());
                }
            }

            // 更新最后访问时间
            memberService.updateLastAccessTime(workspaceId, userId);

            log.debug("工作空间权限验证通过: 用户 {}, 工作空间 {}, 角色 {}",
                userId, workspaceId, memberRole);

        } catch (ServiceException e) {
            // 重新抛出业务异常
            throw e;
        } catch (Exception e) {
            log.error("工作空间权限验证失败", e);
            throw new ServiceException("工作空间权限验证失败: " + e.getMessage());
        }
    }

    /**
     * 获取 @WorkspacePermission 注解
     */
    private WorkspacePermission getWorkspacePermission(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // 先检查方法上的注解
        WorkspacePermission permission = method.getAnnotation(WorkspacePermission.class);
        if (permission != null) {
            return permission;
        }

        // 再检查类上的注解
        permission = joinPoint.getTarget().getClass().getAnnotation(WorkspacePermission.class);
        return permission;
    }

    /**
     * 从方法参数中获取工作空间ID
     */
    private Long getWorkspaceId(JoinPoint joinPoint, String paramName) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Parameter[] parameters = method.getParameters();
        Object[] args = joinPoint.getArgs();

        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            // 检查参数名称
            if (parameter.getName().equals(paramName)) {
                Object arg = args[i];
                if (arg instanceof Long) {
                    return (Long) arg;
                } else if (arg != null) {
                    // 尝试从对象中获取workspaceId字段
                    try {
                        java.lang.reflect.Field field = arg.getClass().getDeclaredField(paramName);
                        field.setAccessible(true);
                        Object value = field.get(arg);
                        if (value instanceof Long) {
                            return (Long) value;
                        }
                    } catch (Exception e) {
                        log.debug("无法从对象中获取workspaceId字段", e);
                    }
                }
            }
        }

        return null;
    }

    /**
     * 检查角色级别是否满足要求
     * 角色级别: owner > admin > editor > viewer > member
     */
    private boolean checkRoleLevel(String currentRole, String requiredRole) {
        int currentLevel = getRoleLevel(currentRole);
        int requiredLevel = getRoleLevel(requiredRole);
        return currentLevel >= requiredLevel;
    }

    /**
     * 获取角色级别
     * owner=5, admin=4, editor=3, viewer=2, member=1
     */
    private int getRoleLevel(String role) {
        return switch (role) {
            case WorkspaceConstants.MemberRole.OWNER -> 5;
            case WorkspaceConstants.MemberRole.ADMIN -> 4;
            case WorkspaceConstants.MemberRole.EDITOR -> 3;
            case WorkspaceConstants.MemberRole.VIEWER -> 2;
            case WorkspaceConstants.MemberRole.MEMBER -> 1;
            default -> 0;
        };
    }
}

