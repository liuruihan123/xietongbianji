package com.xmzs.ssologin.service;

import com.xmzs.system.domain.vo.LoginVo;
import com.xmzs.ssologin.domain.request.GoogleLoginRequest;

/**
 * Google登录服务接口
 *
 * @author AI Assistant
 */
public interface GoogleLoginService {

    /**
     * 处理Google登录
     *
     * @param request Google登录请求
     * @return 登录结果
     */
    LoginVo handleGoogleLogin(GoogleLoginRequest request);

    /**
     * 验证Google ID Token
     *
     * @param idToken Google ID Token
     * @return 用户信息Map，包含email、name等
     */
    java.util.Map<String, Object> verifyGoogleToken(String idToken);
}
