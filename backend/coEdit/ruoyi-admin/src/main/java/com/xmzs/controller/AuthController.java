package com.xmzs.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.secure.BCrypt;
import cn.hutool.core.collection.CollUtil;
import com.xmzs.common.core.constant.Constants;
import com.xmzs.common.core.domain.R;
import com.xmzs.common.core.domain.model.*;
import com.xmzs.common.core.utils.StreamUtils;
import com.xmzs.common.core.utils.StringUtils;
import com.xmzs.common.log.annotation.Log;
import com.xmzs.common.log.enums.BusinessType;
import com.xmzs.common.satoken.utils.LoginHelper;
import com.xmzs.common.tenant.helper.TenantHelper;
import com.xmzs.system.config.base.BaseConfig;
import com.xmzs.system.domain.SysUser;
import com.xmzs.system.domain.bo.AiConversationStartersBo;
import com.xmzs.system.domain.bo.SysTenantBo;
import com.xmzs.system.domain.bo.SysUserBo;
import com.xmzs.system.domain.vo.AiConversationStartersVo;
import com.xmzs.system.domain.vo.ChatConfigVo;
import com.xmzs.system.domain.vo.LoginTenantVo;
import com.xmzs.system.domain.vo.LoginVo;
import com.xmzs.system.domain.vo.SysTenantVo;
import com.xmzs.system.domain.vo.SysUserVo;
import com.xmzs.system.domain.vo.TenantListVo;
//import com.xmzs.system.request.WxLoginRequest;
import com.xmzs.system.service.*;
//import com.xmzs.ssologin.domain.request.GoogleLoginRequest;
//import com.xmzs.ssologin.service.GoogleLoginService;


import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.stream.Collectors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 认证
 *
 * @author Lion Li
 */
@SaIgnore
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    private final SysLoginService loginService;
    private final SysRegisterService registerService;
    private final ISysTenantService tenantService;
    private final ISysUserService userService;
    private final IChatConfigService chatConfigService;
    private final IAiConversationStartersService aiConversationStartersService;

    private final WxLoginService wxLoginService;

//    private final GoogleLoginService googleLoginService;

    private final BaseConfig baseConfig;

    /**
     * 网页登录
     *
     * @param body 登录信息
     * @return 结果
     */
    @PostMapping("/login")
    public R<LoginVo> login(@Validated @RequestBody LoginBody body) {
        body.setTenantId(Constants.TENANT_ID);
        LoginVo loginVo = new LoginVo();
        // 生成令牌
        String token = loginService.login(
                body.getTenantId(),
                body.getUserName(), body.getPassword(),
                body.getCode(), body.getUuid());
        loginVo.setToken(token);
        loginVo.setUserInfo(LoginHelper.getLoginUser());
        return R.ok(loginVo);
    }

    /**
     * 短信登录
     *
     * @param body 登录信息
     * @return 结果
     */
    @PostMapping("/smsLogin")
    public R<LoginVo> smsLogin(@Validated @RequestBody SmsLoginBody body) {
        log.info("sms登录：{}",body);
        LoginVo loginVo = new LoginVo();
        // 生成令牌
        String token = loginService.smsLogin(body.getTenantId(), body.getPhonenumber(), body.getSmsCode(), body.getInviteCode());
        loginVo.setToken(token);
        loginVo.setUserInfo(LoginHelper.getLoginUser());

        return R.ok(loginVo);
    }

    /**
     * 邮件登录
     *
     * @param body 登录信息
     * @return 结果
     */
    @PostMapping("/emailLogin")
    public R<LoginVo> emailLogin(@Validated @RequestBody EmailLoginBody body) {
        log.info("email登录：{}",body);
        LoginVo loginVo = new LoginVo();
        // 生成令牌
        String token = loginService.emailLogin(body.getTenantId(), body.getEmail(), body.getEmailCode(), body.getInviteCode());
        loginVo.setToken(token);
        return R.ok(loginVo);
    }

    /**
     * 小程序登录(废弃)
     *
     * @param loginBody
     * @return 结果
     */
    @PostMapping("/visitorLogin2")
    public R<LoginVo> xcxLogin2(@RequestBody VisitorLoginBody loginBody) {
        return R.ok(loginService.visitorLogin(loginBody));
    }


    /**
     * SSO 登录
     *
     * @param token SSO 登录凭证
     * @return 登录结果
     */
    @GetMapping("/sso/login")
    @SaIgnore
    public R<LoginVo> ssoLogin(@RequestParam String token) {
        if (StringUtils.isBlank(token)) {
            return R.fail("缺少 SSO 凭据");
        }
        log.info("SSO 登录请求，token: {}", token);

//        LoginVo loginVo = new LoginVo();
//        loginVo.setToken(token);
//        loginVo.setUserInfo(LoginHelper.getLoginUser());
//
//        return R.ok(loginVo);


        LoginVo loginVo = loginService.ssoLoginWithToken(token);
        return R.ok(loginVo);
    }

    /**
     * 同步用户组织机构
     * @return 同步结果
     */
    @GetMapping("/syncUserOrg")
    @SaIgnore
    public R<Void> getUserAllOrg() {
        userService.syncAllOrgAndUsers();
        return R.ok();
    }

//    /**
//     * 小程序登录（新）
//     *
//     * @param request
//     * @return 小程序登录
//     */
//    @PostMapping("/visitorLogin")
//    public R<LoginVo> xcxLogin(@RequestBody WxLoginRequest request) {
//        log.info("小程序登录：{}",request);
//        return R.ok(wxLoginService.handleWxLogin(request));
//    }

//    /**
//     * Google登录
//     *
//     * @param request Google登录请求
//     * @return 登录结果
//     */
//    @PostMapping("/google/login")
//    public R<LoginVo> googleLogin(@Validated @RequestBody GoogleLoginRequest request) {
//        log.info("Google登录：credential={}, clientId={}",
//            request.getCredential().substring(0, Math.min(20, request.getCredential().length())) + "...",
//            request.getClientId());
//        return R.ok(googleLoginService.handleGoogleLogin(request));
//    }



    @Log(title = "修改用户基本信息", businessType = BusinessType.UPDATE)
    @PostMapping("/editUserInfo")
    public R<Void> editUserInfo(@RequestBody @Validated SysUserBo user) {
        LoginUser loginUser = LoginHelper.getLoginUser();
        user.setUserId(loginUser.getUserId());
        userService.updateUser(user);
        return R.ok("操作成功!");
    }


    /**
     * 退出登录
     */
    @PostMapping("/logout")
    public R<Void> logout() {
        loginService.logout();
        return R.ok("退出成功");
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public R<Void> register(@Validated @RequestBody RegisterBody user) {
        R r = registerService.register(user);
        return r;
    }

    /**
     * 效验用户名是否已经存在
     */
    @PostMapping("/checkUserNameUnique")
    public R<Boolean> checkUserNameUnique(@Validated @RequestBody CheckUserNameBody user) {
        return R.ok(registerService.checkUserName(user));
    }

    /**
     * 重置密码
     */
    @PostMapping("/reset/password")
    @SaIgnore
    public R<Void> resetPassWord(@Validated @RequestBody RegisterBody user) {
        registerService.resetPassWord(user);
        return R.ok();
    }


    /**
     * 修改信息
     */
    @PostMapping("/updateUserInfo")
    public R<LoginVo> updateUserInfo( @RequestBody UpdateUserInfoBody updateUserInfoBody) {
//        StpUtil.checkLogin();
//        SysUserBo sysUser = MapstructUtils.convert(user, SysUser.class);
        LoginUser loginUser = LoginHelper.getLoginUser(updateUserInfoBody.getToken());
        updateUserInfoBody.setUserId(loginUser.getUserId());
        updateUserInfoBody.setUserName(loginUser.getUserName());
        LoginUser uploginUser = loginService.updateUser(updateUserInfoBody);
        LoginVo loginVo = new LoginVo();
        loginVo.setToken(updateUserInfoBody.getToken());
        loginVo.setUserInfo(uploginUser);
        return R.ok(loginVo);
    }

    /**
     * 根据验证码修改手机号码和邮箱验证码
     * @param updateUserInfoBody
     * @return
     */
    @PostMapping("/updateInfoByCode")
    public R<LoginVo> updateInfoByCode( @RequestBody UpdateUserInfoBody updateUserInfoBody) {
//        StpUtil.checkLogin();
//        SysUserBo sysUser = MapstructUtils.convert(user, SysUser.class);
        LoginUser loginUser = LoginHelper.getLoginUser(updateUserInfoBody.getToken());
        if("sms".equals(updateUserInfoBody.getType())){
            boolean doMatch = loginService.validateSmsCode(null,updateUserInfoBody.getPhonenumber(),updateUserInfoBody.getCode());
            if(!doMatch){
                throw new NotLoginException("errorCode","","");
            }
            updateUserInfoBody.setPhonenumber(updateUserInfoBody.getPhonenumber());
        }else if("email".equals(updateUserInfoBody.getType())){
            boolean doMatch = loginService.validateEmailCode(null,updateUserInfoBody.getEmail(),updateUserInfoBody.getCode());
            if(!doMatch){
                throw new NotLoginException("errorCode","","");
            }
            updateUserInfoBody.setEmail(updateUserInfoBody.getEmail());
        }else if("nickname".equals(updateUserInfoBody.getType())){
            updateUserInfoBody.setNickName(updateUserInfoBody.getNickName());
        }

        updateUserInfoBody.setUserId(loginUser.getUserId());
        LoginUser uploginUser = loginService.updateUser(updateUserInfoBody);
        LoginVo loginVo = new LoginVo();
        loginVo.setToken(updateUserInfoBody.getToken());
        loginVo.setUserInfo(uploginUser);
        return R.ok(loginVo);
    }



    /**
     * 重置密码
     *
     */
    @Log(title = "修改密码", businessType = BusinessType.UPDATE)
    @PutMapping("/updatePwd")
    public R<Void> updatePwd(@RequestBody  ResetPasswordBody resetPasswordBody) {
        SysUserVo user = userService.selectUserById(LoginHelper.getUserId());
        String password = user.getPassword();
        if (!BCrypt.checkpw(resetPasswordBody.getOldPassword(), password)) {
            if ("en".equals(resetPasswordBody.getLg()))  return R.fail("Incorrect old password");
            else   return R.fail("旧密码不对");

        }
        if (BCrypt.checkpw(resetPasswordBody.getNewPassword(), password)) {
            if ("en".equals(resetPasswordBody.getLg()))  return R.fail("The new password cannot be the same as the old password");
            else   return R.fail("新密码不能与旧密码相同");
        }

        if (userService.resetUserPwd(user.getUserId(), BCrypt.hashpw(resetPasswordBody.getNewPassword())) > 0) {
            return R.ok();
        }
        if ("en".equals(resetPasswordBody.getLg()))  return R.fail("Abnormal password modification, please contact the administrator");
        else   return R.fail("修改密码异常，请联系管理员");
//        return R.fail("修改密码异常，请联系管理员");
    }

    /**
     * 登录页面租户下拉框
     *
     * @return 租户列表
     */
    @GetMapping("/tenant/list")
    public R<LoginTenantVo> tenantList(HttpServletRequest request) throws Exception {
        List<SysTenantVo> tenantList = tenantService.queryList(new SysTenantBo());
        // 使用静态方法转换，避免 MapStruct 的间歇性问题
        List<TenantListVo> voList = tenantList.stream()
            .map(TenantListVo::fromSysTenantVo)
            .collect(Collectors.toList());
        // 获取域名
        String host = new URL(request.getRequestURL().toString()).getHost();
        // 根据域名进行筛选
        List<TenantListVo> list = StreamUtils.filter(voList, vo -> StringUtils.equals(vo.getDomain(), host));
        // 返回对象
        LoginTenantVo vo = new LoginTenantVo();
        vo.setVoList(CollUtil.isNotEmpty(list) ? list : voList);
        vo.setTenantEnabled(TenantHelper.isEnable());
        return R.ok(vo);
    }





    /**
     * 查找某个配置内容 隐私政策 之类
     * lg cn/en
     */
    @PostMapping("/config/content")
    public R<String> getConfigContent( @RequestBody LanguageBody lb) {
        ChatConfigVo configVo =  chatConfigService.queryByConfigName(lb.getConfigName());
        String value =configVo.getConfigValue();
        return R.ok("",value);
    }


    /**
     * 查询对话开场列表
     */
    @PostMapping("/queryByBusiType")
    public R<List<AiConversationStartersVo>> queryByBusiType(@RequestBody AiConversationStartersBo bo) {
        return R.ok(aiConversationStartersService.queryByBusiTypeList(bo));
    }

    /**
     * 查询对话开场列表
     */
    @PostMapping("/getMIniAppInfo")
    public R<Map<String,String>> getMIniAppInfo(@RequestBody Map<String, String> params) {
        Map<String,String> miniConfigMap =  baseConfig.get("miniApp");
        //新map 去敏感
        Map<String,String> map = new HashMap<>();
        map.put("appstarid",miniConfigMap.get("appstarid"));
        return R.ok(map);
    }

//    /**
//     * 服务协议
//     * lg cn/en
//     */
//    @PostMapping("/agreement/service")
//    public R<String> protocolService( @RequestBody LanguageBody lb) {
//        String value = "";
//        if("cn".equals(lb.getLg())){
//            ChatConfigVo configVo =  chatConfigService.queryById(1796948003434106886l);
//            value = configVo.getConfigValue();
//        }else{
//            ChatConfigVo configVo =  chatConfigService.queryById(1796948003434106888l);
//            value = configVo.getConfigValue();
//        }
//        return R.ok("",value);
//    }
//
//    /**
//     * 隐私政策
//     * lg cn/en
//     */
//    @PostMapping("/agreement/privacy")
//    public R<String> protocolPrivacy( @RequestBody LanguageBody lb) {
//        String value = "";
//        if("cn".equals(lb.getLg())){
//            ChatConfigVo configVo =  chatConfigService.queryById(1796948003434106887l);
//            value = configVo.getConfigValue();
//        }else{
//            ChatConfigVo configVo =  chatConfigService.queryById(1796948003434106889l);
//            value = configVo.getConfigValue();
//        }
//        return R.ok("",value);
//    }

}
