package com.xmzs.knowledge.config;//package com.xmzs.knowledge.config;
//
//import com.xmzs.knowledge.common.annotation.AdminRequired;
//import com.xmzs.knowledge.common.constant.SysConstants;
//import com.xmzs.knowledge.common.exception.AuthException;
//import com.xmzs.knowledge.common.exception.SessionExpiredException;
//import com.xmzs.knowledge.domain.SysUser;
//import jakarta.servlet.http.HttpServletRequest;
//import org.aspectj.lang.JoinPoint;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Before;
//import org.aspectj.lang.annotation.Pointcut;
//import org.springframework.stereotype.Component;
//import org.springframework.web.context.request.RequestContextHolder;
//import org.springframework.web.context.request.ServletRequestAttributes;
//
//@Component
//@Aspect
//public class AdminRequiredAspect {
//
//    @Pointcut("@annotation(adminRequired)")
//    public void adminRequiredPointcut(AdminRequired adminRequired){
//
//    }
//
////    @Before(value = "adminRequiredPointcut(adminRequired)")
//    public void before(JoinPoint joinPoint,AdminRequired adminRequired){
//        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//        HttpServletRequest httpServletRequest = attributes.getRequest();
//        SysUser sysUser = (SysUser) httpServletRequest.getSession().getAttribute(SysConstants.SESSION_LOGIN_USER_KEY);
//        if (sysUser==null){
//            throw new SessionExpiredException("请先登录");
//        }else {
//            if (!sysUser.getAdminFlag()){
//                throw new AuthException("仅管理员可以操作");
//            }
//        }
//
//    }
//}
