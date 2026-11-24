package com.xmzs.common.satoken.aspect;

import cn.hutool.core.util.ObjectUtil;
import com.xmzs.common.core.domain.model.LoginUser;
import com.xmzs.common.core.exception.ServiceException;
import com.xmzs.common.satoken.annotation.VipCheck;
import com.xmzs.common.satoken.utils.LoginHelper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 会员权限验证切面
 * <p>
 * 拦截带有 @VipCheck 注解的方法或类
 * 验证当前用户是否为会员（user_grade != 0）
 *
 * @author zhuzi
 * @date 2025-10-07
 */
@Slf4j
@Aspect
@Component
public class VipCheckAspect {

    /**
     * 在方法执行前进行会员权限验证
     */
    @Before("@within(com.xmzs.common.satoken.annotation.VipCheck) || @annotation(com.xmzs.common.satoken.annotation.VipCheck)")
    public void doBefore(JoinPoint joinPoint) {
        try {
            // 获取当前登录用户
            LoginUser loginUser = LoginHelper.getLoginUser();

            if (ObjectUtil.isNull(loginUser)) {
                throw new ServiceException("用户未登录，请先登录");
            }

            // 检查用户等级
            String userGrade = loginUser.getUserGrade();

            // userGrade 为 "0" 表示免费用户，需要升级为会员
            if ("0".equals(userGrade)) {
                // 获取自定义提示信息
                String message = getVipCheckMessage(joinPoint);
                log.warn("用户 {} (ID: {}) 尝试访问会员功能但权限不足",
                    loginUser.getUserName(), loginUser.getUserId());
                throw new ServiceException(message);
            }

            log.debug("会员权限验证通过: 用户 {} (ID: {}), 等级: {}",
                loginUser.getUserName(), loginUser.getUserId(), userGrade);

        } catch (ServiceException e) {
            // 重新抛出业务异常
            throw e;
        } catch (Exception e) {
            log.error("会员权限验证失败", e);
            throw new ServiceException("会员权限验证失败: " + e.getMessage());
        }
    }

    /**
     * 获取 @VipCheck 注解中的自定义提示信息
     */
    private String getVipCheckMessage(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // 先检查方法上的注解
        VipCheck vipCheck = method.getAnnotation(VipCheck.class);
        if (vipCheck != null) {
            return vipCheck.message();
        }

        // 再检查类上的注解
        vipCheck = joinPoint.getTarget().getClass().getAnnotation(VipCheck.class);
        if (vipCheck != null) {
            return vipCheck.message();
        }

        // 返回默认提示信息
        return "此功能仅限会员使用，请先升级为会员后再尝试";
    }
}

