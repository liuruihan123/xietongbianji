package com.xmzs.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.hutool.captcha.AbstractCaptcha;
import cn.hutool.captcha.generator.CodeGenerator;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import com.xmzs.common.core.constant.Constants;
import com.xmzs.common.core.constant.GlobalConstants;
import com.xmzs.common.core.domain.R;
import com.xmzs.common.core.utils.SpringUtils;
import com.xmzs.common.core.utils.StringUtils;
import com.xmzs.common.core.utils.reflect.ReflectUtils;
import com.xmzs.common.mail.utils.MailUtils;
import com.xmzs.common.redis.utils.RedisUtils;
import com.xmzs.common.sms.config.properties.SmsProperties;
import com.xmzs.common.web.config.properties.CaptchaProperties;
import com.xmzs.common.web.enums.CaptchaType;
import com.xmzs.system.config.base.BaseConfig;
import com.xmzs.system.domain.vo.CaptchaVo;
import com.xmzs.system.domain.request.EmailRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * 验证码操作处理
 *
 * @author Lion Li
 */
@SaIgnore
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
public class CaptchaController {

    private final CaptchaProperties captchaProperties;

    @Autowired
    private final SmsProperties smsProperties;

    private final BaseConfig baseConfig;


    /**
     * 短信验证码
     * 使用新的通知服务发送短信验证码
     *
     * @param phonenumber 用户手机号
     */
    @GetMapping("/resource/sms/code")
    public R<Void> smsCode(@NotBlank(message = "{user.phonenumber.not.blank}") String phonenumber) {
        if (!smsProperties.getEnabled()) {
            return R.fail("当前系统没有开启短信功能！");
        }

        // 生成验证码并存储到Redis
        String key = GlobalConstants.CAPTCHA_CODE_KEY + phonenumber;
        String code = RandomUtil.randomNumbers(6);
        RedisUtils.setCacheObject(key, code, Duration.ofMinutes(Constants.CAPTCHA_EXPIRATION));

//        try {
//            // 使用新的通知服务发送短信
//            NotificationService notificationService = notificationServiceFactory.getDefaultService();
//
//            // 构建短信内容
//            String message = String.format("【协同编辑】您的验证码为：%s，有效期为%d分钟，请尽快填写。",
//                    code, Constants.CAPTCHA_EXPIRATION);
//
//            // 构建SMS请求
//            SmsRequest smsRequest = SmsRequest.builder()
//                    .to(phonenumber)
//                    .message(message)
//                    .messageType("transactional")
//                    .validityPeriod(Constants.CAPTCHA_EXPIRATION)
//                    .enableStatusCallback(false)
//                    .build();
//
//            // 发送短信
//            SmsResponse response = notificationService.sendSms(smsRequest);
//
//            if (!response.getSuccess()) {
//                log.error("验证码短信发送失败 => messageId={}, error={}",
//                        response.getMessageId(), response.getErrorMessage());
//                return R.fail(response.getErrorMessage() != null ? response.getErrorMessage() : "短信发送失败");
//            }
//
//            log.info("验证码短信发送成功 => to={}, messageId={}, provider={}",
//                    phonenumber, response.getMessageId(), response.getProvider());
//            return R.ok();
//
//        } catch (NotificationException e) {
//            log.error("验证码短信发送异常 => to={}, error={}", phonenumber, e.getMessage(), e);
//            return R.fail("短信发送失败: " + e.getMessage());
//        } catch (Exception e) {
//            log.error("验证码短信发送系统异常 => to={}", phonenumber, e);
            return R.fail("短信发送系统异常，请稍后重试");
//        }
    }


//    /**
//     * 发送短信Aliyun
//     *
//     * @param phones     电话号
//     * @param templateId 模板ID
//     */
//    @GetMapping("/sendAliyun")
//    public R<Object> sendAliyun(String phones, String templateId) {
//        if (!smsProperties.getEnabled()) {
//            return R.fail("当前系统没有开启短信功能！");
//        }
//        if (!SpringUtils.containsBean("aliyunSmsTemplate")) {
//            return R.fail("阿里云依赖未引入！");
//        }
//        SmsTemplate smsTemplate = SpringUtils.getBean(SmsTemplate.class);
//        Map<String, String> map = new HashMap<>(1);
//        map.put("code", "1234");
//        Object send = smsTemplate.send(phones, templateId, map);
//        return R.ok(send);
//    }

    /**
     * 邮箱验证码
     *
     * @param emailRequest 用户邮箱
     */
    @PostMapping("/resource/email/code")
    public R<Void> emailCode(@RequestBody @Valid EmailRequest emailRequest) {
        String key = GlobalConstants.CAPTCHA_CODE_KEY + emailRequest.getUserName();
        String code = RandomUtil.randomNumbers(6);
        RedisUtils.setCacheObject(key, code, Duration.ofMinutes(Constants.CAPTCHA_EXPIRATION));
        try {
            MailUtils.sendText(emailRequest.getUserName(), "【协同编辑】提醒您", "您本次验证码为：" + code + "，有效性为" + Constants.CAPTCHA_EXPIRATION + "分钟，请尽快填写。");
        } catch (Exception e) {
            log.error("验证码短信发送异常2 => {}", e.getMessage());
            return R.fail(e.getMessage());
        }
        return R.ok();
    }

    /**
     * 生成验证码
     */
    @GetMapping("/code")
    public R<CaptchaVo> getCode() {
        CaptchaVo captchaVo = new CaptchaVo();
        boolean captchaEnabled = captchaProperties.getEnable();
        if (!captchaEnabled) {
            captchaVo.setCaptchaEnabled(false);
            return R.ok(captchaVo);
        }
        // 保存验证码信息
        String uuid = IdUtil.simpleUUID();
        String verifyKey = GlobalConstants.CAPTCHA_CODE_KEY + uuid;
        // 生成验证码
        CaptchaType captchaType = captchaProperties.getType();
        boolean isMath = CaptchaType.MATH == captchaType;
        Integer length = isMath ? captchaProperties.getNumberLength() : captchaProperties.getCharLength();
        CodeGenerator codeGenerator = ReflectUtils.newInstance(captchaType.getClazz(), length);
        AbstractCaptcha captcha = SpringUtils.getBean(captchaProperties.getCategory().getClazz());
        captcha.setGenerator(codeGenerator);
        captcha.createCode();
        String code = captcha.getCode();
        if (isMath) {
            ExpressionParser parser = new SpelExpressionParser();
            Expression exp = parser.parseExpression(StringUtils.remove(code, "="));
            code = exp.getValue(String.class);
        }
        RedisUtils.setCacheObject(verifyKey, code, Duration.ofMinutes(Constants.CAPTCHA_EXPIRATION));
        captchaVo.setUuid(uuid);
        captchaVo.setImg(captcha.getImageBase64());
        return R.ok(captchaVo);
    }

}
