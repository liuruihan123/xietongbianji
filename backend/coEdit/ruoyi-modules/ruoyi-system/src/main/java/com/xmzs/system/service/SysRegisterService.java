package com.xmzs.system.service;

import cn.dev33.satoken.secure.BCrypt;
import cn.hutool.core.util.RandomUtil;
import com.xmzs.common.core.constant.Constants;
import com.xmzs.common.core.constant.GlobalConstants;
import com.xmzs.common.core.domain.R;
import com.xmzs.common.core.domain.model.CheckUserNameBody;
import com.xmzs.common.core.domain.model.RegisterBody;
import com.xmzs.common.core.exception.user.CaptchaException;
import com.xmzs.common.core.exception.user.CaptchaExpireException;
import com.xmzs.common.core.service.ConfigService;
import com.xmzs.common.core.utils.MessageUtils;
import com.xmzs.common.core.utils.ServletUtils;
import com.xmzs.common.core.utils.SpringUtils;
import com.xmzs.common.core.utils.StringUtils;
import com.xmzs.common.log.event.LogininforEvent;
import com.xmzs.common.redis.utils.RedisUtils;
import com.xmzs.system.domain.SysUser;
import com.xmzs.system.domain.bo.SysUserBo;
import com.xmzs.system.domain.vo.SysUserVo;
import com.xmzs.system.mapper.SysUserRoleMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 注册校验方法
 *
 * @author Lion Li
 */
@RequiredArgsConstructor
@Service
public class SysRegisterService {

    private final ISysUserService userService;

    private final SysUserRoleMapper userRoleMapper;

    private final ConfigService configService;

    private final SysLoginService loginService;
    // 邮箱正则表达式
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_\\-.+]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$";
    // 手机号码正则表达式（以中国大陆的手机号码为例）
    private static final String PHONE_REGEX = "^1[3-9]\\d{9}$";



    //检查用户名是否存在 或者该账户是否已经绑定手机或者邮箱
    public Boolean checkUserName(CheckUserNameBody registerBody) {
        String userName = registerBody.getUserName();
        SysUserBo sysUser = new SysUserBo();
        sysUser.setUserName(userName);
        sysUser.setNickName(userName);

        if (!userService.checkUserNameUnique(sysUser)) {
//            throw new UserException("添加用户失败", userName);
            return false;
        }
        //检测邮箱或者手机号码是否已经绑定用户
        if(isEmail(registerBody.getUserName())){
          return   userService.checkPhoneUnique(sysUser);
        } else if (isPhoneNumber(registerBody.getUserName())) {
            return  userService.checkEmailUnique(sysUser);
        }
        return true;
    }
    /**
     * 注册
     */
    public R register(RegisterBody registerBody) {
        String tenantId = Constants.TENANT_ID;
        if(StringUtils.isNotBlank(registerBody.getTenantId())){
            tenantId = registerBody.getTenantId();
        }
        String userName = registerBody.getUserName();
        String password = registerBody.getPassword();
        // 检查验证码是否正确
         R r = validateEmail(userName,registerBody.getCode(),registerBody.getLanguage());
         if(r.getCode()!=200){
             return r;
         }
        SysUserBo sysUser = new SysUserBo();
        sysUser.setUserName(userName);
        sysUser.setNickName(userName);
        sysUser.setPassword(BCrypt.hashpw(password));
        //检查账户是否被绑定
        CheckUserNameBody checkUserNameBody = new CheckUserNameBody();
        checkUserNameBody.setUserName(userName);
        if (!checkUserName(checkUserNameBody)) {
            if("en".equals(registerBody.getLanguage())){
                return R.fail("The user already exists");

            }else{
                return R.fail("该账户已被使用，不可注册");
            }

        }
//        if (!userService.checkUserNameUnique(sysUser)) {
//            return R.fail("The user already exists");
//        }
        String configValue = configService.getConfigValue("mail", "amount");
        // 设置默认余额
        sysUser.setUserBalance(NumberUtils.toDouble(configValue,1));
        sysUser.setInviteCode(RandomUtil.randomString(6));
        //根据账户 设置绑定邮箱手机号码
        if(isEmail(registerBody.getUserName())){
            sysUser.setEmail(registerBody.getUserName());
        } else if (isPhoneNumber(registerBody.getUserName())) {
            sysUser.setPhonenumber(registerBody.getUserName());
        }

        SysUser user = userService.registerUser(sysUser, tenantId,registerBody.getInviteCode());
        //邀请用户奖励处理
        loginService.inviteHandler(registerBody.getInviteCode(),user,"reg");
        if (user == null) {
//            throw new UserException("用户注册失败!");
            if("en".equals(registerBody.getLanguage())){
                return R.fail("Registration failed！");
            }else{
                return R.fail("注册失败");
            }

        }
        // 设置默认角色
//        SysUserRole sysRole = new SysUserRole();
//        sysRole.setUserId(user.getUserId());
//        sysRole.setRoleId(1L);
//        userRoleMapper.insert(sysRole);
        recordLogininfor(tenantId, userName, Constants.REGISTER, MessageUtils.message("user.register.success"));
        return R.ok();
    }

    /**
     * 重置密码
     */
    public R resetPassWord(RegisterBody registerBody) {
        String userName = registerBody.getUserName();
        String password = registerBody.getPassword();
        SysUserVo user = userService.selectUserByUserName(userName);
        if(user == null){
            if("en".equals(registerBody.getLanguage())){
                return R.fail("user ["+userName+"],unregistered!");
            }else{
                return R.fail("用户【"+userName+"】,未注册!");
            }
//            throw new UserException(String.format("用户【%s】,未注册!",userName));
        }
        // 检查验证码是否正确
        R r = validateEmail(userName,registerBody.getCode(),registerBody.getLanguage());
        if(r.getCode()!=200){
            return r;
        }
        userService.resetUserPwd(user.getUserId(),BCrypt.hashpw(password));
        return r;
    }

    /**
     * 校验邮箱验证码
     *
     * @param userName 用户名
     */
    public R validateEmail(String userName,String code,String language) {
        String key = GlobalConstants.CAPTCHA_CODE_KEY + userName;
         String captcha = RedisUtils.getCacheObject(key);
        if(code.equals(captcha)){
            RedisUtils.deleteObject(captcha);
            return R.ok();
        }else {

            if("en".equals(language)){
                return R.fail("Verification code error, please try again!");
            }else{
                return R.fail("验证码错误,请重试！");
            }
//            throw new BaseException("验证码错误,请重试！");
        }
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
            recordLogininfor(tenantId, userName, Constants.REGISTER, MessageUtils.message("user.jcaptcha.expire"));
            throw new CaptchaExpireException();
        }
        if (!code.equalsIgnoreCase(captcha)) {
            recordLogininfor(tenantId, userName, Constants.REGISTER, MessageUtils.message("user.jcaptcha.error"));
            throw new CaptchaException();
        }
    }

    /**
     * 记录登录信息
     *
     * @param tenantId 租户ID
     * @param userName 用户名
     * @param status   状态
     * @param message  消息内容
     * @return
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

//    //检查是否是手机
//    public Boolean checkisPhone(String machstr){
//        return isPhoneNumber(machstr);
//    }
//    //检查是否是邮箱
//    public Boolean checkisEmail(String machstr){
//        return isEmail(machstr);
//    }
    // 判断是否是邮箱
    public static boolean isEmail(String input) {
        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        Matcher matcher = pattern.matcher(input);
        return matcher.matches();
    }

    // 判断是否是手机号码
    public static boolean isPhoneNumber(String input) {
        Pattern pattern = Pattern.compile(PHONE_REGEX);
        Matcher matcher = pattern.matcher(input);
        return matcher.matches();
    }

}
