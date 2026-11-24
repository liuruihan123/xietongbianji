package com.xmzs.ssologin.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.xmzs.common.core.constant.Constants;
import com.xmzs.common.core.domain.model.LoginUser;
import com.xmzs.common.core.domain.model.VisitorLoginUser;
import com.xmzs.common.core.enums.DeviceType;
import com.xmzs.common.core.exception.ServiceException;
import com.xmzs.common.core.utils.MessageUtils;
import com.xmzs.common.satoken.utils.LoginHelper;
import com.xmzs.common.log.event.LogininforEvent;
import org.springframework.context.ApplicationEventPublisher;
import com.xmzs.ssologin.domain.request.GoogleLoginRequest;
import com.xmzs.ssologin.service.GoogleLoginService;
import com.xmzs.system.config.base.BaseConfig;
import com.xmzs.system.domain.bo.SysUserBo;
import com.xmzs.system.domain.vo.LoginVo;
import com.xmzs.system.domain.vo.SysUserVo;
import com.xmzs.system.service.ISysUserService;
import com.xmzs.system.service.SysLoginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Google登录服务实现
 *
 * @author AI Assistant
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class GoogleLoginServiceImpl implements GoogleLoginService {

    private final BaseConfig baseConfig;
    private final ISysUserService userService;
    private final SysLoginService loginService;
    private final RestTemplate restTemplate;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public LoginVo handleGoogleLogin(GoogleLoginRequest request) {
        try {
            // 1. 验证Google ID Token
            Map<String, Object> userInfo = verifyGoogleToken(request.getCredential(), request.getClientId());

            String email = (String) userInfo.get("email");
            String name = (String) userInfo.get("name");
            String googleId = (String) userInfo.get("sub");

            if (StrUtil.isBlank(email)) {
                throw new ServiceException("无法获取Google用户邮箱信息");
            }

            // 2. 查找或创建用户
            // TODO: 实现 selectUserByEmail 方法后启用
            // SysUserVo user = userService.selectUserByEmail(email);
            SysUserVo user = null; // 临时处理
            VisitorLoginUser loginUser;

            if (ObjectUtil.isNull(user)) {
                // 创建新用户
                loginUser = createNewUser(email, name, googleId, request.getInviteCode());
            } else {
                // 现有用户登录
                loginUser = buildLoginUser(user, googleId);
            }

            // 3. 生成token并返回
            LoginHelper.loginByDevice(loginUser, DeviceType.PC);
            recordLogininfor(loginUser.getTenantId(), loginUser.getUserName(),
                Constants.LOGIN_SUCCESS, MessageUtils.message("user.login.success"));

            LoginVo loginVo = new LoginVo();
            loginVo.setToken(StpUtil.getTokenValue());
            loginVo.setUserInfo(loginUser);

            return loginVo;

        } catch (Exception e) {
            log.error("Google登录失败", e);
            throw new ServiceException("Google登录失败: " + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> verifyGoogleToken(String idToken) {
        // 为了向后兼容，从配置中获取客户端ID
        String clientId = baseConfig.get("google", "clientId");
        if (StrUtil.isBlank(clientId)) {
            throw new ServiceException("Google客户端ID未配置");
        }
        return verifyGoogleToken(idToken, clientId);
    }

    /**
     * 验证Google ID Token（带客户端ID参数）
     *
     * @param idToken Google ID Token
     * @param clientId Google客户端ID
     * @return 用户信息Map，包含email、name等
     */
    public Map<String, Object> verifyGoogleToken(String idToken, String clientId) {
        try {
            if (StrUtil.isBlank(clientId)) {
                throw new ServiceException("Google客户端ID不能为空");
            }

            // 调用Google tokeninfo API验证token
            String url = "https://oauth2.googleapis.com/tokeninfo?id_token=" + idToken;
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                throw new ServiceException("Google token验证失败");
            }

            Map<String, Object> tokenInfo = response.getBody();

            // 验证audience (客户端ID)
            String aud = (String) tokenInfo.get("aud");
            if (!clientId.equals(aud)) {
                throw new ServiceException("Google token客户端ID不匹配");
            }

            // 验证token是否过期
            Long exp = Long.valueOf(tokenInfo.get("exp").toString());
            if (System.currentTimeMillis() / 1000 > exp) {
                throw new ServiceException("Google token已过期");
            }

            return tokenInfo;

        } catch (Exception e) {
            log.error("验证Google token失败", e);
            throw new ServiceException("验证Google token失败: " + e.getMessage());
        }
    }

    private VisitorLoginUser createNewUser(String email, String name, String googleId, String inviteCode) {
        SysUserBo newUser = new SysUserBo();
        newUser.setUserName(email);
        newUser.setEmail(email);
        newUser.setNickName(StrUtil.isNotBlank(name) ? name : "Google用户");
        newUser.setPassword(""); // Google登录用户无需密码
        newUser.setUserType("sys_user");
        newUser.setInviteCode(RandomUtil.randomString(6));

        // 设置默认使用次数
        String usageCount = baseConfig.get("usageCount", "free");
        newUser.setSubMsgCount(Integer.valueOf(usageCount));

        // 设置默认积分
        String initPoints = baseConfig.get("usageCount", "initPoints");
        newUser.setPoints(Integer.valueOf(initPoints));

        // 注册用户
        userService.registerUser(newUser, "0", inviteCode);

        // 构建登录用户信息
        VisitorLoginUser loginUser = new VisitorLoginUser();
        loginUser.setTenantId("0");
        loginUser.setUserId(newUser.getUserId());
        loginUser.setUserName(newUser.getUserName());
        loginUser.setNickName(newUser.getNickName());
        loginUser.setEmail(newUser.getEmail());
        loginUser.setUserType("sys_user");
        loginUser.setOpenid(googleId); // 使用Google ID作为openid
        loginUser.setSubMsgCount(newUser.getSubMsgCount());
        loginUser.setPoints(newUser.getPoints());
        loginUser.setInviteCode(newUser.getInviteCode());

        return loginUser;
    }

    private VisitorLoginUser buildLoginUser(SysUserVo user, String googleId) {
        VisitorLoginUser loginUser = new VisitorLoginUser();
        loginUser.setTenantId(user.getTenantId());
        loginUser.setUserId(user.getUserId());
        loginUser.setUserName(user.getUserName());
        loginUser.setNickName(user.getNickName());
        loginUser.setEmail(user.getEmail());
        loginUser.setUserType(user.getUserType());
        loginUser.setOpenid(googleId);
        loginUser.setSubMsgCount(user.getSubMsgCount());
        loginUser.setPoints(user.getPoints());
        loginUser.setUserGrade(user.getUserGrade());
        loginUser.setVipExpiryDate(user.getVipExpiryDate());
        loginUser.setInviteCode(user.getInviteCode());

        return loginUser;
    }

    /**
     * 记录登录信息
     *
     * @param tenantId 租户ID
     * @param userName 用户名
     * @param status   状态
     * @param message  消息内容
     */
    private void recordLogininfor(String tenantId, String userName, String status, String message) {
        LogininforEvent logininforEvent = new LogininforEvent();
        logininforEvent.setTenantId(tenantId);
        logininforEvent.setUserName(userName);
        logininforEvent.setStatus(status);
        logininforEvent.setMessage(message);
        eventPublisher.publishEvent(logininforEvent);
    }
}
