package com.xmzs.common.satoken.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 会员验证注解
 * <p>
 * 用于标记需要会员权限才能访问的接口
 * 只有 user_grade != 0 的用户才能访问
 *
 * @author zhuzi
 * @date 2025-10-07
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface VipCheck {

    /**
     * 提示信息
     */
    String message() default "此功能仅限会员使用，请先升级为会员后再尝试";
}

