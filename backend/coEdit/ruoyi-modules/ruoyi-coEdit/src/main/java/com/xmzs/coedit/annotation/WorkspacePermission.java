package com.xmzs.coedit.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 工作空间权限验证注解
 * <p>
 * 用于标记需要工作空间权限才能访问的接口
 * 验证当前用户是否为指定工作空间的成员，并具有相应的角色权限
 *
 * @author zuleed
 * @date 2025-11-04
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface WorkspacePermission {

    /**
     * 工作空间ID参数名称（从方法参数中获取）
     * 默认为 "workspaceId"
     */
    String workspaceIdParam() default "workspaceId";

    /**
     * 需要的最低角色级别
     * owner > admin > editor > viewer > member
     * 默认为 member（只需要是成员即可）
     */
    String requiredRole() default "member";

    /**
     * 是否需要管理员权限（owner或admin）
     * 如果为true，则忽略requiredRole参数
     */
    boolean requireAdmin() default false;

    /**
     * 是否需要所有者权限
     * 如果为true，则忽略requiredRole和requireAdmin参数
     */
    boolean requireOwner() default false;

    /**
     * 提示信息
     */
    String message() default "您没有访问此工作空间的权限";
}

