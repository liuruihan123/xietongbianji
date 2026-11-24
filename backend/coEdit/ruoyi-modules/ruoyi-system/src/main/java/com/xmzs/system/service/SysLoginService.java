package com.xmzs.system.service;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xmzs.common.core.config.SsoConfig;
import com.xmzs.common.core.constant.Constants;
import com.xmzs.common.core.constant.GlobalConstants;
import com.xmzs.common.core.constant.TenantConstants;
import com.xmzs.common.core.domain.dto.RoleDTO;
import com.xmzs.common.core.domain.model.*;
import com.xmzs.common.core.enums.*;
import com.xmzs.common.core.exception.ServiceException;
import com.xmzs.common.core.exception.base.BaseException;
import com.xmzs.common.core.exception.user.CaptchaException;
import com.xmzs.common.core.exception.user.CaptchaExpireException;
import com.xmzs.common.core.exception.user.UserException;
import com.xmzs.common.core.utils.*;
import com.xmzs.common.log.event.LogininforEvent;
import com.xmzs.common.redis.utils.RedisUtils;
import com.xmzs.common.satoken.utils.LoginHelper;
import com.xmzs.common.tenant.exception.TenantException;
import com.xmzs.common.tenant.helper.TenantHelper;
import com.xmzs.system.cache.ScanLoginCache;
import com.xmzs.system.config.base.BaseConfig;
import com.xmzs.system.config.base.CategoryNames;
import com.xmzs.system.domain.SysUser;
import com.xmzs.system.domain.bo.AiInvitationsBo;
import com.xmzs.system.domain.bo.SysUserBo;
import com.xmzs.system.domain.vo.AiInvitationsVo;
import com.xmzs.system.domain.vo.LoginVo;
import com.xmzs.system.domain.vo.SysTenantVo;
import com.xmzs.system.domain.vo.SysUserVo;
import com.xmzs.system.mapper.SysUserMapper;
import com.xmzs.system.util.HttpUtils;
import com.xmzs.system.util.ToolUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.beans.PropertyDescriptor;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.Duration;
import java.util.*;
import java.util.function.Supplier;
import org.springframework.util.ObjectUtils;

/**
 * 登录校验方法
 *
 * @author Lion Li
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class SysLoginService {

    @Resource
    private final SysUserMapper userMapper;
    private final ISysPermissionService permissionService;
    private final ISysTenantService tenantService;
    private final ISysUserService userService;
    private final BaseConfig baseConfig;
    private final IAiInvitationsService invitationsService;

    @Value("${user.password.maxRetryCount}")
    private Integer maxRetryCount;

    @Value("${user.password.lockTime}")
    private Integer lockTime;

    /**
     * 登录验证
     *
     * @param userName 用户名
     * @param password 密码
     * @param code     验证码
     * @param uuid     唯一标识
     * @return 结果
     */
    public String login(String tenantId, String userName, String password, String code, String uuid) {
        SysUserVo user = loadUserByUserName(tenantId, userName);
        System.out.println(BCrypt.checkpw(password, user.getPassword()));

        checkLogin(LoginType.PASSWORD, tenantId, userName, () -> !BCrypt.checkpw(password, user.getPassword()));
        // 此处可根据登录用户的数据不同 自行创建 loginUser
        LoginUser loginUser = buildLoginUser(user);
        // 生成token
        LoginHelper.loginByDevice(loginUser, DeviceType.PC);

        recordLogininfor(loginUser.getTenantId(), userName, Constants.LOGIN_SUCCESS, MessageUtils.message("user.login.success"));
        recordLoginInfo(user.getUserId());
        return StpUtil.getTokenValue();
    }

    /**
     * 手机号登录
     * @param tenantId
     * @param phonenumber
     * @param smsCode
     * @return
     */
    public String smsLogin(String tenantId, String phonenumber, String smsCode,String inviteCode) {
        // 校验租户
        checkTenant(tenantId);
        //效验验证码
        boolean doMatch =  validateSmsCode(tenantId, phonenumber, smsCode);
        if(!doMatch){
            throw new NotLoginException("errorCode","","");
        }

        // 通过手机号查找用户 ,如果用户不存在则直接创建
        SysUserVo user = loadUserByPhonenumber(tenantId, phonenumber, inviteCode);

        checkLogin(LoginType.SMS, tenantId, user.getUserName(), () -> !doMatch);
        // 此处可根据登录用户的数据不同 自行创建 loginUser
        LoginUser loginUser = buildLoginUser(user);
        // 生成token
        LoginHelper.loginByDevice(loginUser, DeviceType.PC);

        recordLogininfor(loginUser.getTenantId(), user.getUserName(), Constants.LOGIN_SUCCESS, MessageUtils.message("user.login.success"));
        recordLoginInfo(user.getUserId());
        return StpUtil.getTokenValue();
    }



    /**
     * 邮箱登录
     * @param tenantId
     * @param email
     * @param emailCode
     * @return
     */
    public String emailLogin(String tenantId, String email, String emailCode,String inviteCode) {
        // 校验租户
        checkTenant(tenantId);

        //效验验证码
        boolean doMatch =  validateEmailCode(tenantId, email, emailCode);
        if(!doMatch){
            throw new NotLoginException("errorCode","","");
        }

        // 通过手机号查找用户 ,如果用户不存在则直接创建
        SysUserVo user = loadUserByEmail(tenantId, email, inviteCode);

        checkLogin(LoginType.EMAIL, tenantId, user.getUserName(), () -> !doMatch);
        // 此处可根据登录用户的数据不同 自行创建 loginUser
        LoginUser loginUser = buildLoginUser(user);
        // 生成token
        LoginHelper.loginByDevice(loginUser, DeviceType.PC);

        recordLogininfor(loginUser.getTenantId(), user.getUserName(), Constants.LOGIN_SUCCESS, MessageUtils.message("user.login.success"));
        recordLoginInfo(user.getUserId());
        return StpUtil.getTokenValue();
    }


    /**
     * 小程序登录
     *
     * @param loginBody
     * @return String
     * @Date 2023/5/18
     **/
    public LoginVo visitorLogin(VisitorLoginBody loginBody) {
        String openid = "";
        String unionid = "";

        String phoneNumber = loginBody.getPhoneNumer();
        if(StringUtils.isEmpty(phoneNumber)){
            throw new NotLoginException("手机号码获取失败","","");
        }


        // PC端游客登录
//        if(LoginUserType.PC.getCode().equals(loginBody.getType())){
//            openid = loginBody.getCode();
//        }else {
            // 小程序登录 - 暂时注释掉，因为依赖的方法不可用
            /*
            try {
                WxMaService wxMaService =  wxMnpDriver.initMnp();
                WxMaJscode2SessionResult session = wxMaService.getUserService().getSessionInfo(loginBody.getCode());
                openid = session.getOpenid();
                unionid = session.getUnionid();
                log.info("小程序登录获取的openid:{} ,以及unionid:{}",openid,unionid);
            } catch (
                WxErrorException e) {
                log.error(e.getMessage(), e);
            } finally {
                // 清理ThreadLocal
                WxMaConfigHolder.remove();
            }
            */
            // 临时解决方案：使用手机号作为openid
            openid = phoneNumber;
//        }

        // 使用 openid 查询绑定用户 如未绑定用户 则根据业务自行处理 例如 创建默认用户
//        SysUserVo user = userService.selectUserByOpenId(openid);
        SysUserVo user  =  userService.selectUserByPhonenumber(phoneNumber);

        if(ObjectUtils.isEmpty(user)){
            user =new SysUserVo();
            user.setUserName(phoneNumber);
            user.setPhonenumber(phoneNumber);
        }

//        VisitorLoginUser loginUser = new VisitorLoginUser();
        VisitorLoginUser loginUser = handleUserLogin(user,openid,unionid,loginBody.getInviteCode());

        // 生成token
        LoginHelper.loginByDevice(loginUser, DeviceType.XCX);
        recordLogininfor(loginUser.getTenantId(), loginUser.getUserName(), Constants.LOGIN_SUCCESS, MessageUtils.message("user.login.success"));
        LoginVo loginVo = new LoginVo();
        // 生成令牌
        loginVo.setToken(StpUtil.getTokenValue());
        loginVo.setUserInfo(loginUser);
        return loginVo;
    }

    //处理用户登录
    private VisitorLoginUser handleUserLogin(SysUserVo user,String openid,String unionid,String inviteCode){
        String userType = "sys_user";
        VisitorLoginUser loginUser = new VisitorLoginUser();

        if (ObjectUtils.isEmpty(user)||user.getUserId()==null) {
            SysUserBo sysUser = new SysUserBo();
            // 处理未登录用户自增
//            String name = "user-" + RandomUtil.randomInt(10000);
            String name = RandomUtil.randomString(10);
            if(StringUtils.isEmpty(user.getUserName())){
                sysUser.setUserName(name);
            }else {
                sysUser.setUserName(user.getUserName());
            }

            if(StringUtils.isEmpty(user.getNickName())){
                sysUser.setNickName("user-" +name);
            }else{
                sysUser.setNickName(user.getNickName());
            }
            user.setInviteCode(RandomUtil.randomString(6));
            sysUser.setAvatar(user.getAvatar());
            // 设置默认昵称
//            sysUser.setNickName(name);
            // 设置默认密码
            sysUser.setPassword(BCrypt.hashpw("123456"));
            // 设置微信openId
//            sysUser.setOpenId(openid);
            sysUser.setOpenid(openid);
            sysUser.setUnionid(unionid);
            sysUser.setUserType(userType);
            sysUser.setPhonenumber(user.getPhonenumber());
            sysUser.setSubMsgCount(Integer.valueOf(baseConfig.get("usageCount","free")));
            log.info("输入用户点数:{}",Integer.valueOf(baseConfig.get("usageCount","initPoints")));
            sysUser.setPoints(Integer.valueOf(baseConfig.get("usageCount","initPoints")));
            // 注册用户,设置默认租户为0
            System.out.println(sysUser.getUserName()+"~~");
            SysUser registerUser = userService.registerUser(sysUser, "0",inviteCode);
            //邀请用户奖励处理
            inviteHandler(inviteCode,registerUser,"reg");


            // 构建登录用户信息
            loginUser.setTenantId("0");
            loginUser.setUserId(registerUser.getUserId());
            loginUser.setUserName(registerUser.getUserName());
            loginUser.setUserType(UserType.APP_USER.getUserType());
            loginUser.setOpenid(openid);
            loginUser.setUnionid(unionid);
            loginUser.setNickName(registerUser.getNickName());

            loginUser.setAvatar(sysUser.getAvatar());
            loginUser.setSubMsgCount(sysUser.getSubMsgCount());
            log.info("输入用户点数:{}",sysUser.getPoints());
            loginUser.setPoints(sysUser.getPoints());
            loginUser.setUserGrade(sysUser.getUserGrade());
            loginUser.setVipExpiryDate(sysUser.getVipExpiryDate());
            loginUser.setEmail(sysUser.getEmail());
            loginUser.setPhonenumber(sysUser.getPhonenumber());
            loginUser.setUserType(userType);
            loginUser.setInviteCode(sysUser.getInviteCode());

        } else {
            SysUser sysUser = new SysUser();
            sysUser.setUserId(user.getUserId());
            sysUser.setOpenid(openid);
            sysUser.setUnionid(unionid);
            sysUser.setUpdateTime(new Date());
            sysUser.setPhonenumber(user.getPhonenumber());

            //更新用户信息
            userMapper.updateById(sysUser);

            // 此处可根据登录用户的数据不同 自行创建 loginUser
            loginUser.setTenantId(user.getTenantId());
            loginUser.setUserId(user.getUserId());
            loginUser.setUserName(user.getUserName());
            loginUser.setUserType(user.getUserType());
            loginUser.setNickName(user.getNickName());
            loginUser.setAvatar(user.getWxAvatar());
            loginUser.setOpenid(openid);
            loginUser.setUserType(userType);
            loginUser.setAvatar(user.getAvatar());
            loginUser.setSubMsgCount(user.getSubMsgCount());
            log.info("输入用户点数:{}",user.getPoints());
            loginUser.setPoints(user.getPoints());
            loginUser.setUserGrade(user.getUserGrade());
            loginUser.setVipExpiryDate(user.getVipExpiryDate());
            loginUser.setEmail(user.getEmail());
            loginUser.setPhonenumber(user.getPhonenumber());
            loginUser.setInviteCode(sysUser.getInviteCode());
        }
        return loginUser;
    }
    /**
     * 扫码链接
     *
     * @author zuleed
     * @param url
     *    回调地址
     * @return String
     */
    public String scanCodeUrl(String url, HttpSession session) {
        // 获取AppId
        String appId = baseConfig.get(CategoryNames.OP_CHANNEL, "appId");

        // 微信开放平台授权
        String baseUrl = "https://open.weixin.qq.com/connect/qrconnect" +
            "?appid=%s" +
            "&redirect_uri=%s" +
            "&response_type=code" +
            "&scope=snsapi_login" +
            "&state=%s" +
            "#wechat_redirect";

        // 回调地址
        String redirectUrl = "https://mianshistar.com";
        try {
            redirectUrl = URLEncoder.encode(redirectUrl, "UTF-8");
        } catch ( UnsupportedEncodingException e) {
            throw new BaseException(e.getMessage()+"");
        }

        // 防止csrf攻击
        String state = ToolUtils.makeUUID().replaceAll("-", "");
        ScanLoginCache.set(session.getId(), state);

        //生成QrcodeUrl
        return String.format(baseUrl, appId, redirectUrl, state);
    }
    /**
     * 获取扫码参数
     * @param session
     * @return
     */
    public Map<String,String> getScanCodeConfig(HttpSession session) {
        // 获取AppId
        String appId = baseConfig.get(CategoryNames.OP_CHANNEL, "appId");
        String redirect_uri = baseConfig.get(CategoryNames.OP_CHANNEL, "redirect_uri");
        try {
            redirect_uri = URLEncoder.encode(redirect_uri, "UTF-8");
        } catch ( UnsupportedEncodingException e) {
            throw new BaseException(e.getMessage()+"");
        }
        // 防止csrf攻击
        String state = ToolUtils.makeUUID().replaceAll("-", "");
        System.out.println("sessionId"+session.getId());
        ScanLoginCache.set(session.getId(), state);
//        String tt = ScanLoginCache.get(session.getId());
//        System.out.println("ttt==="+tt);
        Map<String,String> psMap = new HashMap<String,String>();
        psMap.put("self_redirect","false");
        psMap.put("appid",appId);
        psMap.put("scope","snsapi_login");
        psMap.put("redirect_uri",redirect_uri);
        psMap.put("state",state);
        return psMap;
    }



    /**
     * 扫码登录
     *
     * @author fzr
     * @param code 编码
     * @param state 标识
     * @param terminal 终端
     * @param session 会话
     */
    public LoginVo scanLogin(String code, String state, Integer terminal, HttpSession session,String inviteCode) {
        System.out.println("session.getId()"+session.getId());
        System.out.println("state"+state);
        if (!ScanLoginCache.get(session.getId()).equals(state)) {
            throw new BaseException("二维码已失效或不存在,请重新操作");
        }

        // 得到配置和授权临时票据code
        String appId = baseConfig.get(CategoryNames.OP_CHANNEL, "appId");
        String appSecret = baseConfig.get(CategoryNames.OP_CHANNEL, "appSecret");

        //向认证服务器发送请求换取access_token
        String baseAccessTokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token" +
            "?appid=%s" +
            "&secret=%s" +
            "&code=%s" +
            "&grant_type=authorization_code";

        Map<String, String> resultMap;
        try {
            String accessTokenUrl = String.format(baseAccessTokenUrl, appId, appSecret, code);
            String result = HttpUtils.sendGet(accessTokenUrl);
//            resultMap = MapstructUtils.convert(result, HashMap.class);
            resultMap = MapUtils.jsonToMap(result);
        } catch (Exception e) {
            throw new BaseException("获取access_token失败:"+e.getMessage());
        }

        // 访问微信获取用户信息 (openid,unionId,昵称,头像等)
        String accessToken = resultMap.get("access_token");
        String openid1 = resultMap.get("openid");
        String baseUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s";
        String userInfoUrl = String.format(baseUserInfoUrl, accessToken, openid1);
        Map<String, String> userinfoMap;
        try {
            String resultUserInfo = HttpUtils.sendGet(userInfoUrl);
//            userinfoMap = MapstructUtils.convert(resultUserInfo,HashMap.class);
            userinfoMap = MapUtils.jsonToMap(resultUserInfo);
        } catch (Exception e) {
            throw new BaseException("获取用户信息失败:"+e.getMessage());
        }
        log.info("微信扫码登录，{}",userinfoMap);

//        String openId  = userinfoMap.get("openid");
        String openid  = userinfoMap.get("openid");
        String uniId   = userinfoMap.get("unionid");
        String unionId = uniId == null ? "0" : uniId;
        String avatar   = userinfoMap.getOrDefault("headimgurl", "");
        String nickname = userinfoMap.getOrDefault("nickname", "");

        return this.__wxLoginHandle(openid, unionId, avatar, nickname, terminal, inviteCode);
    }


    public Object ticket(String key) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("user", Collections.EMPTY_LIST);
        data.put("status", 0); // 未扫码=1,未登录=2,登录信息异常=3,登录成功=4

        // 读取缓存
        Object keyObj = RedisUtils.getCacheObject(key);
        if (ObjectUtil.isEmpty(keyObj)) {
            throw new BaseException("登录信息不存在!");
        }

        // 取出缓存
//        Map<String, String> keyInfo = MapstructUtils.convert(keyObj.toString(), HashMap.class);
        Map<String, String> keyInfo = MapUtils.jsonToMap(keyObj.toString());
        Integer isScan = Integer.parseInt(keyInfo.get("isScan"));
        Integer status = Integer.parseInt(keyInfo.get("status"));

        // 未登录
        if (!isScan.equals(1)) {
            data.put("status", 1);
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("data", data);
            response.put("msg", "未扫码");
            response.put("code", 200);
            return response;
        }

        // 已扫码
        if (!status.equals(1)) {
            data.put("status", 2);
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("data", data);
            response.put("msg", "已扫码未登录");
            response.put("code", 200);
            return response;
        }

        // 返回token值
        data.put("user", JSONObject.parse(keyInfo.get("user")));
        data.put("status", 4);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("data", data);
        response.put("msg", "登录成功");
        response.put("code", 200);
        return response;
    }

    /**
     * 登录二维码
     *
     * @param inviteCode 编码
     * @return LoginQrCodeVo
     * @throws Exception 异常
     */
    public LoginQrCodeVo qrcode(String inviteCode) throws Exception {
        String uuid    = ToolUtils.makeUUID();
        String ip      = IpUtils.getIpAddress();
        String tempKey = ToolUtils.makeMd5(ip + uuid);
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("key", tempKey);
        map.put("status", 0);
        map.put("salt", uuid);
        map.put("ip", ip);
        map.put("isScan", 0);
        map.put("isLogin", 0);
        map.put("inviteCode", inviteCode);
        RedisUtils.setKey(tempKey, JSON.toJSONString(map), 120);

        // 暂时注释掉微信公众号二维码生成，因为依赖的方法不可用
        /*
        WxMpService wxMpService = wxMnpDriver.oa();
        WxMpQrCodeTicket ticket = wxMpService.getQrcodeService().qrCodeCreateTmpTicket(tempKey, 120);

        LoginQrCodeVo vo = new LoginQrCodeVo();
        vo.setExpireSeconds(120);
        vo.setUrl("https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=" + ticket.getTicket());
        vo.setKey(tempKey);
        vo.setTicket(ticket.getTicket());
        return vo;
        */

        // 临时解决方案：返回一个简单的二维码信息
        LoginQrCodeVo vo = new LoginQrCodeVo();
        vo.setExpireSeconds(120);
        vo.setUrl("https://example.com/qrcode/" + tempKey);  // 临时URL
        vo.setKey(tempKey);
        vo.setTicket(tempKey);  // 使用tempKey作为临时ticket
        return vo;
    }

    /**
     * 退出登录
     */
    public void logout() {
        try {
            LoginUser loginUser = LoginHelper.getLoginUser();
            if (TenantHelper.isEnable() && LoginHelper.isSuperAdmin()) {
                // 超级管理员 登出清除动态租户
                TenantHelper.clearDynamic();
            }
            StpUtil.logout();
            recordLogininfor(loginUser.getTenantId(), loginUser.getUserName(), Constants.LOGOUT, MessageUtils.message("user.logout.success"));
        } catch (NotLoginException ignored) {
        }
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
        logininforEvent.setRequest(ServletUtils.getRequest());
        SpringUtils.context().publishEvent(logininforEvent);
    }

    /**
     * 校验短信验证码
     */
    public boolean validateSmsCode(String tenantId, String phonenumber, String smsCode) {
        String code = RedisUtils.getCacheObject(GlobalConstants.CAPTCHA_CODE_KEY + phonenumber);
        if (StringUtils.isBlank(code)) {
            recordLogininfor(tenantId, phonenumber, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.expire"));
            throw new CaptchaExpireException();
        }
        return code.equals(smsCode);
    }

    /**
     * 校验邮箱验证码
     */
    public boolean validateEmailCode(String tenantId, String email, String emailCode) {
        String code = RedisUtils.getCacheObject(GlobalConstants.CAPTCHA_CODE_KEY + email);
        if (StringUtils.isBlank(code)) {
            recordLogininfor(tenantId, email, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.expire"));
            throw new CaptchaExpireException();
        }
        return code.equals(emailCode);
    }

    /**
     * 校验验证码
     *
     * @param userName 用户名
     * @param code     验证码
     * @param uuid     唯一标识
     */
    public void validateCaptcha(String tenantId, String userName, String code, String uuid) {
        String verifyKey = GlobalConstants.CAPTCHA_CODE_KEY + StringUtils.defaultString(uuid, "");
        String captcha = RedisUtils.getCacheObject(verifyKey);
        RedisUtils.deleteObject(verifyKey);
        if (captcha == null) {
            recordLogininfor(tenantId, userName, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.expire"));
            throw new CaptchaExpireException();
        }
        if (!code.equalsIgnoreCase(captcha)) {
            recordLogininfor(tenantId, userName, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.error"));
            throw new CaptchaException();
        }
    }

    /**
     * 处理微信扫码登录
     *
     * @param openid   (openid)
     * @param unionId  (unionId)
     * @param terminal (terminal)
     * @param avatar   (用户头像)
     * @param nickname (用户昵称)
     * @return LoginTokenVo
     */
    private LoginVo __wxLoginHandle(String openid, String unionId, String avatar, String nickname, Integer terminal,String inviteCode) {
        //扫码登录
        SysUserVo user = new SysUserVo();
        user =   userService.selectUserByUnionid(unionId);
        if(ObjectUtils.isEmpty(user)){
            user = userService.selectUserByOpenId(openid);
        }

        VisitorLoginUser loginUser = null;
        if(ObjectUtils.isEmpty(user)||user.getUserId()==null)  {
            user = new SysUserVo();
            System.out.println(avatar);
            user.setAvatar(avatar);
            user.setNickName(nickname);
        }

        log.info("微信扫码登录获取的openid:{} ,以及unionid:{}",openid,unionId);

        loginUser = handleUserLogin(user,openid,unionId, inviteCode);

        // 生成token
        LoginHelper.loginByDevice(loginUser, DeviceType.PC);
        recordLogininfor(loginUser.getTenantId(), loginUser.getUserName(), Constants.LOGIN_SUCCESS, MessageUtils.message("user.login.success"));
        LoginVo loginVo = new LoginVo();
        // 生成令牌
        loginVo.setToken(StpUtil.getTokenValue());
        loginVo.setUserInfo(loginUser);
        return loginVo;
//        return this.__loginToken(user.getId(), user.getMobile(), user.getIsNew(), String.valueOf(terminal));


    }



    private SysUserVo loadUserByUserName(String tenantId, String userName) {

        SysUser user = userMapper.selectOne(new LambdaQueryWrapper<SysUser>()
            .select(SysUser::getUserName, SysUser::getStatus)
            .eq(TenantHelper.isEnable(), SysUser::getTenantId, tenantId)
            .eq(SysUser::getUserName, userName));
        if (ObjectUtil.isNull(user)) {
            log.info("登录用户：{} 不存在.", userName);
            throw new UserException("user.not.exists", userName);
        } else if (UserStatus.DISABLE.getCode().equals(user.getStatus())) {
            log.info("登录用户：{} 已被停用.", userName);
            throw new UserException("user.blocked", userName);
        }
        if (TenantHelper.isEnable()) {
            return userMapper.selectTenantUserByUserName(userName, tenantId);
        }
        return userMapper.selectUserByUserName(userName);
    }

    /**
     * 不存在则创建账号在登录
     * @param tenantId
     * @param phonenumber
     * @return
     */
    private SysUserVo loadUserByPhonenumber(String tenantId, String phonenumber,String inviteCode) {
        SysUser user = userMapper.selectOne(new LambdaQueryWrapper<SysUser>()
            .select(SysUser::getPhonenumber, SysUser::getStatus)
            .eq(TenantHelper.isEnable(), SysUser::getTenantId, tenantId)
            .eq(SysUser::getPhonenumber, phonenumber));
        if (ObjectUtil.isNull(user)) {
            log.info("登录用户：{} 不存在.创建用户.", phonenumber);
//            throw new UserException("user.not.exists", phonenumber);

//            throw new UserException("user.not.exists", email);
            SysUserVo sysUser =new SysUserVo();
            sysUser.setUserName(phonenumber);
            sysUser.setPhonenumber(phonenumber);
            VisitorLoginUser loginUser = handleUserLogin(sysUser,null,null, inviteCode);
            copyNonNullProperties(loginUser,sysUser);
            return sysUser;

        } else if (UserStatus.DISABLE.getCode().equals(user.getStatus())) {
            log.info("登录用户：{} 已被停用.", phonenumber);
            throw new UserException("user.blocked", phonenumber);
        }
        if (TenantHelper.isEnable()) {
            return userMapper.selectTenantUserByPhonenumber(phonenumber, tenantId);
        }
        return userMapper.selectUserByPhonenumber(phonenumber);
    }
    /**
     * 不存在则创建账号在登录
     * @param tenantId
     * @param email
     * @return
     */
    private SysUserVo loadUserByEmail(String tenantId, String email,String inviteCode) {
        SysUser user = userMapper.selectOne(new LambdaQueryWrapper<SysUser>()
            .select(SysUser::getPhonenumber, SysUser::getStatus)
            .eq(TenantHelper.isEnable(), SysUser::getTenantId, tenantId)
            .eq(SysUser::getEmail, email));
        if (ObjectUtil.isNull(user)) {
            log.info("登录用户：{} 不存在，创建用户.", email);
//            throw new UserException("user.not.exists", email);
            SysUserVo sysUser =new SysUserVo();
            sysUser.setUserName(email);
            sysUser.setEmail(email);
            VisitorLoginUser loginUser = handleUserLogin(sysUser,null,null, inviteCode);
             copyNonNullProperties(loginUser,sysUser);
            return sysUser;

        } else if (UserStatus.DISABLE.getCode().equals(user.getStatus())) {
            log.info("登录用户：{} 已被停用.", email);
            throw new UserException("user.blocked", email);
        }
        if (TenantHelper.isEnable()) {
            return userMapper.selectTenantUserByEmail(email, tenantId);
        }
        return userMapper.selectUserByEmail(email);
    }

    /**
     * 构建登录用户
     */
    private LoginUser buildLoginUser(SysUserVo user) {
        LoginUser loginUser = new LoginUser();
        loginUser.setTenantId(user.getTenantId());
        loginUser.setUserId(user.getUserId());
        loginUser.setDeptId(user.getDeptId());
        loginUser.setUserName(user.getUserName());
        loginUser.setNickName(user.getNickName());
        loginUser.setPhonenumber(user.getPhonenumber());
        loginUser.setEmail(user.getEmail());
        loginUser.setAvatar(user.getAvatar());
        loginUser.setUserType(user.getUserType());
        loginUser.setMenuPermission(permissionService.getMenuPermission(user.getUserId()));
        loginUser.setRolePermission(permissionService.getRolePermission(user.getUserId()));
        loginUser.setSubMsgCount(user.getSubMsgCount());
        log.info("输入用户点数:{}",user.getPoints());
        loginUser.setPoints(user.getPoints());
        loginUser.setUserGrade(user.getUserGrade());
        loginUser.setVipExpiryDate(user.getVipExpiryDate());
        loginUser.setOpenid(user.getOpenid());

        loginUser.setInviteCode(user.getInviteCode());
        loginUser.setUnionid(user.getUnionid());

        loginUser.setDeptName(ObjectUtil.isNull(user.getDept()) ? "" : user.getDept().getDeptName());
        List<RoleDTO> roles = BeanUtil.copyToList(user.getRoles(), RoleDTO.class);
        loginUser.setRoles(roles);
        return loginUser;
    }

    /**
     * 记录登录信息
     *
     * @param userId 用户ID
     */
    public void recordLoginInfo(Long userId) {
        SysUser sysUser = new SysUser();
        sysUser.setUserId(userId);
        sysUser.setLoginIp(ServletUtils.getClientIP());
        sysUser.setLoginDate(DateUtils.getNowDate());
        sysUser.setUpdateBy(userId);
        userMapper.updateById(sysUser);
    }

    /**
     * 登录校验
     */
    private void checkLogin(LoginType loginType, String tenantId, String userName, Supplier<Boolean> supplier) {
        String errorKey = GlobalConstants.PWD_ERR_CNT_KEY + userName;
        String loginFail = Constants.LOGIN_FAIL;

        // 获取用户登录错误次数(可自定义限制策略 例如: key + userName + ip)
        Integer errorNumber = RedisUtils.getCacheObject(errorKey);
        // 锁定时间内登录 则踢出
        if (ObjectUtil.isNotNull(errorNumber) && errorNumber.equals(maxRetryCount)) {
            recordLogininfor(tenantId, userName, loginFail, MessageUtils.message(loginType.getRetryLimitExceed(), maxRetryCount, lockTime));
            throw new UserException(loginType.getRetryLimitExceed(), maxRetryCount, lockTime);
        }

        if (supplier.get()) {
            // 是否第一次
            errorNumber = ObjectUtil.isNull(errorNumber) ? 1 : errorNumber + 1;
            // 达到规定错误次数 则锁定登录
            if (errorNumber.equals(maxRetryCount)) {
                RedisUtils.setCacheObject(errorKey, errorNumber, Duration.ofMinutes(lockTime));
                recordLogininfor(tenantId, userName, loginFail, MessageUtils.message(loginType.getRetryLimitExceed(), maxRetryCount, lockTime));
                throw new UserException(loginType.getRetryLimitExceed(), maxRetryCount, lockTime);
            } else {
                // 未达到规定错误次数 则递增
                RedisUtils.setCacheObject(errorKey, errorNumber);
                recordLogininfor(tenantId, userName, loginFail, MessageUtils.message(loginType.getRetryLimitCount(), errorNumber));
                throw new UserException(loginType.getRetryLimitCount(), errorNumber);
            }
        }

        // 登录成功 清空错误次数
        RedisUtils.deleteObject(errorKey);
    }

    /**
     * 邀请奖励会员处理
     * @param inviteCode
     *   邀请码
     * @param user
     *      被邀请人（注册人）
     *@param type
     *   reg 注册   rech充值
     */
    public void inviteHandler(String inviteCode,SysUser user,String type){
        //邀请者
      SysUserVo  inviteruser =  userService.selectUserByInviteCode(inviteCode);
      if(ObjectUtil.isNull(inviteruser)){
          return;
      }
      String  reward_days = baseConfig.get("invit","reward_days");
      Long endTime =   DateUtils.calculateExpiryTimestampByDay(Integer.valueOf(reward_days));



      if("rech".equals(type)){

          //将注册更新邀请成功
          AiInvitationsBo ib = new AiInvitationsBo();
          ib.setInviteeId(user.getUserId());
          ib.setInviterCode(inviteCode);
          List<AiInvitationsVo> invitationsVoList =   invitationsService.queryList(ib);

          if(invitationsVoList.size()>0){
              AiInvitationsBo invitationsBo = new AiInvitationsBo();
              invitationsBo.setId(invitationsVoList.get(0).getId());
              invitationsBo.setInviteeId(user.getUserId());
              invitationsBo.setInviterCode(inviteCode);
              invitationsBo.setStatus("1");
              invitationsService.updateByBo(invitationsBo);
          }
          //更新成为会员
          String  usageCount = baseConfig.get("usageCount","vip");
//          String  initPoints = baseConfig.get("usageCount","initPoints");
          SysUserBo upUserBo = new SysUserBo();
          upUserBo.setUserId(inviteruser.getUserId());
          upUserBo.setUserGrade("1");
          upUserBo.setSubMsgCount(Integer.valueOf(usageCount));
//          upUserBo.setPoints(Integer.valueOf(initPoints));
          //计算到期时间
          upUserBo.setVipExpiryDate(endTime);
          userService.updateUser(upUserBo);


      }else{
          //邀请记录
          AiInvitationsBo aiInvitations =  new AiInvitationsBo();
          aiInvitations.setInviterId(inviteruser.getUserId());
          aiInvitations.setInviterUserName(inviteruser.getUserName());
          aiInvitations.setInviteeId(user.getUserId());
          aiInvitations.setInviteeUserName(user.getUserName());
          aiInvitations.setInviterCode(inviteCode);
          aiInvitations.setStatus("2");
          aiInvitations.setRewardDays(Integer.valueOf(reward_days));
          invitationsService.insertByBo(aiInvitations);
      }


    }


    /**
     * 获取个人奖励相关信息
     * @param loginUser
     * @return
     */
    public Map<String,Object> getUserInviteInfo(LoginUser loginUser){
         SysUserVo userVo = userService.selectUserById(loginUser.getUserId());
         //邀请码
         String inviteCode = userVo.getInviteCode();
         //我邀请的人
         AiInvitationsBo aiInvitationsBo = new AiInvitationsBo();
         aiInvitationsBo.setInviterId(userVo.getUserId());
         aiInvitationsBo.setStatus("1");
         List<AiInvitationsVo>  invitationsVoList =  invitationsService.queryList(aiInvitationsBo);
         Integer inviteCount = invitationsVoList.size();

        //邀请我的人
        AiInvitationsBo aiInvitationsBo2 = new AiInvitationsBo();
        aiInvitationsBo2.setInviteeId(userVo.getUserId());
        List<AiInvitationsVo>  inviteMyList =  invitationsService.queryList(aiInvitationsBo2);
        String inviteMy = "";
        if(inviteMyList.size()>0){
//            inviteMy = inviteMyList.get(0).getInviterUserName();
            SysUserVo inviteMyUserVo = userService.selectUserById(inviteMyList.get(0).getInviterId());
            inviteMy = inviteMyUserVo.getNickName();
        }

        //累积奖励
        // 计算累积的奖励天数
        // 计算累积的奖励天数，处理可能的 null 值
        int totalRewardDays = invitationsVoList.stream()
            .mapToInt(vo -> vo.getRewardDays() != null ? vo.getRewardDays() : 0)
            .sum();
        Map<String,Object> map = new HashMap<String,Object>();
        //奖励天数
        String  rewardDays = baseConfig.get("invit","reward_days");
        //邀请码
        map.put("inviteCode",inviteCode);
        //邀请人数
        map.put("inviteCount",inviteCount);
        //我的推荐人
        map.put("inviteMy",inviteMy);
        //累积奖励天数
        map.put("totalRewardDays",totalRewardDays);
        //邀请说明 每邀请一个人奖励天数
        map.put("rewardDays",rewardDays);
        return map;


    }

    /**
     * 更新用户信息
     * @param user
     * @return
     */
    public LoginUser updateUser(UpdateUserInfoBody user) {
//        SysUser sysUser = MapstructUtils.convert(user, SysUser.class);
        SysUser sysUser = new SysUser();
            copyNonNullProperties(user,sysUser);
        // 防止错误更新后导致的数据误删除
        int flag = userMapper.updateById(sysUser);
        if (flag < 1) {
            throw new ServiceException("修改用户" + user.getUserName() + "信息失败");
        }
        SysUserVo sysUserVo = userService.selectUserById(user.getUserId());
        LoginUser loginUser = buildLoginUser(sysUserVo);
        return loginUser;
    }

    public static void main(String[] args) {
        UpdateUserInfoBody updateUserInfoBody = new UpdateUserInfoBody();
        updateUserInfoBody.setUserName("测试");
        updateUserInfoBody.setToken("12333333");

        SysUser sysUser = new SysUser();
        sysUser.setEmail("9188");
        copyNonNullProperties(updateUserInfoBody,sysUser);
        System.out.println(sysUser);

    }

    /**
     * 将A中和B中属性名对应且有值的数据拷贝到b
     * @param source
     * @param target
     */
    public static void copyNonNullProperties(Object source, Object target) {
        PropertyDescriptor[] propertyDescriptors = BeanUtils.getPropertyDescriptors(source.getClass());
        for (PropertyDescriptor pd : propertyDescriptors) {
            try {
                // 获取属性的getter方法并调用
                Object value = pd.getReadMethod().invoke(source);
                if (value != null) {
                    // 获取属性的setter方法并调用
                    PropertyDescriptor targetPd = BeanUtils.getPropertyDescriptor(target.getClass(), pd.getName());
                    if (targetPd != null && targetPd.getWriteMethod() != null) {
                        targetPd.getWriteMethod().invoke(target, value);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void checkTenant(String tenantId) {
        if (!TenantHelper.isEnable()) {
            return;
        }
        if (TenantConstants.DEFAULT_TENANT_ID.equals(tenantId)) {
            return;
        }
        SysTenantVo tenant = tenantService.queryByTenantId(tenantId);
        if (ObjectUtil.isNull(tenant)) {
            log.info("登录租户：{} 不存在.", tenantId);
            throw new TenantException("tenant.not.exists");
        } else if (TenantStatus.DISABLE.getCode().equals(tenant.getStatus())) {
            log.info("登录租户：{} 已被停用.", tenantId);
            throw new TenantException("tenant.blocked");
        } else if (ObjectUtil.isNotNull(tenant.getExpireTime())
            && new Date().after(tenant.getExpireTime())) {
            log.info("登录租户：{} 已超过有效期.", tenantId);
            throw new TenantException("tenant.expired");
        }
    }

    private final SsoConfig ssoConfig;

    /**
     * 从 SSO 服务端获取 visit_token
     */
    private String fetchVisitTokenFromRemote() {
        try {
            HttpResponse response = HttpRequest.post(ssoConfig.getVisitTokenUrl())
                .form("client_id", ssoConfig.getClientId())
                .form("client_secret", ssoConfig.getClientSecret())
                .timeout(3000)
                .execute();

            String result = response.body();
            log.debug("获取 visit_token 响应: {}", result);

            JSONObject json = JSONObject.parseObject(result);
            if ("0".equals(json.getString("code"))) {
                return json.getString("visit_token");
            } else {
                log.warn("获取 visit_token 失败: {}", result);
            }
        } catch (Exception e) {
            log.error("获取 visit_token 异常", e);
        }
        return null;
    }

    private volatile String cachedVisitToken = null;
    private volatile long visitTokenExpireTime = 0;

    private String getVisitToken() {
        long now = System.currentTimeMillis();
        if (cachedVisitToken != null && now < visitTokenExpireTime) {
            return cachedVisitToken;
        }
        synchronized (this) {
            // 双重检查
            if (cachedVisitToken != null && now < visitTokenExpireTime) {
                return cachedVisitToken;
            }
            String newToken = fetchVisitTokenFromRemote();
            if (newToken != null) {
                this.cachedVisitToken = newToken;
                this.visitTokenExpireTime = now + 4 * 60 * 1000; // 缓存 4 分钟（留 1 分钟余量）
            }
            return newToken;
        }
    }

    public String validateAndFetchUser(String token) {
        String visitToken = getVisitToken();
        if (visitToken == null) {
            log.error("无法获取 visit_token，SSO 验证中止");
            return null;
        }

        try {
            HttpResponse response = HttpRequest.post(ssoConfig.getValidateUrl())
                .header("visit_token", visitToken)
                .form("token", token)
                .timeout(5000)
                .execute();

            String result = response.body();
            log.info("SSO 用户验证响应: {}", result);

            JSONObject json = JSONObject.parseObject(result);
            if ("0".equals(json.getString("code"))) {
                // 解析: data.user.person.personLoginName
                JSONObject data = json.getJSONObject("data");
                if (data != null) {
                    JSONObject user = data.getJSONObject("user");
                    if (user != null) {
                        JSONObject person = user.getJSONObject("person");
                        if (person != null) {
                            return person.getString("personLoginName");
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("验证用户 SSO token 异常", e);
        }
        return null;
    }

    /**
     * 通过 SSO token 完成登录，返回 LoginVo
     */
    public LoginVo ssoLoginWithToken(String token) {
        // 1. 验证 SSO token，获取用户名
        String username = validateAndFetchUser(token);
        if (username == null) {
            throw new ServiceException("SSO身份验证失败");
        }

        // 2. 查询本地用户（只需查一次）
        SysUserVo user = userMapper.selectUserByUserName(username);
        if (user == null) {
            throw new ServiceException("用户未授权访问本系统: " + username);
        }

        // 3. 执行 Sa-Token 登录（复用已有逻辑）
        LoginUser loginUser = buildLoginUser(user);
        LoginHelper.loginByDevice(loginUser, DeviceType.PC);

        // 4. 记录日志
        recordLogininfor(
            user.getTenantId(),
            username,
            Constants.LOGIN_SUCCESS,
            "SSO单点登录成功"
        );
        recordLoginInfo(user.getUserId());

        // 5. 构造返回对象
        LoginVo loginVo = new LoginVo();
        loginVo.setToken(StpUtil.getTokenValue());
        loginVo.setUserInfo(loginUser); // 或 LoginHelper.getLoginUser()
        return loginVo;
    }
}
