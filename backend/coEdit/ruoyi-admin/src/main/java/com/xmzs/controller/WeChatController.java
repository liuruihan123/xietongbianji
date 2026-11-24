package com.xmzs.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.xmzs.common.core.domain.R;
import com.xmzs.common.core.domain.model.LoginQrCodeVo;
import com.xmzs.system.config.base.BaseConfig;
import com.xmzs.system.domain.vo.LoginVo;
import com.xmzs.system.service.SysLoginService;
import io.swagger.annotations.ApiOperation;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * 个人微信扩展控制器
 *
 * @author WangLe
 */

@SaIgnore
@Validated
@RequiredArgsConstructor
@RestController
@Slf4j
public class WeChatController {

    private final SysLoginService loginService;

    private final BaseConfig baseConfig;


    /**
     * 获取开放平台扫码配置参数
     * @param session
     * @return
     */
    @GetMapping("/auth/getScanCodeConfig")
    @ApiOperation(value="PC扫码链接")
    public R<Map<String,String>> getScanCodeConfig(@Validated @NotNull() @RequestParam("url") String url, HttpSession session) {
//        String qrcodeUrl = loginService.scanCodeUrl(url, session);
        log.info("扫码地址:{}",url);

        Map<String,String> configMap = loginService.getScanCodeConfig( session);
        return R.ok(configMap);
    }

//    @GetMapping("/{id}")
//    public R<AiConversationStartersVo> getInfo(@jakarta.validation.constraints.NotNull(message = "主键不能为空")
//                                               @PathVariable Long id) {
//        return R.ok(aiConversationStartersService.queryById(id));
//    }

    /**
     * 开放平台扫码登录
     * @param session
     * @return
     */
//    @GetMapping("/auth/scanLogin")
    @GetMapping("/scanLogin")
    @ApiOperation(value="PC扫码登录")
//    public R<LoginVo> scanLogin(@Validated @RequestBody LoginScanValidate loginScanValidate, HttpSession session) {
    public R<LoginVo> scanLogin(@Validated @NotNull() @RequestParam("code") String code,@RequestParam("state") String state, @RequestParam(value = "inviteCode", required = false) String inviteCode, HttpSession session) {
        Integer terminal = 1;
//        String code = loginScanValidate.getCode();
//        String state = loginScanValidate.getState();
        log.info("微信扫码登录...");
        LoginVo vo = loginService.scanLogin(code, state, terminal, session,inviteCode);
        return  R.ok(vo);
    }

    /**
     * 公众号
     * @param inviteCode
     * @return
     * @throws Exception
     */
    @GetMapping("/auth/qrcode")
    @ApiOperation("登录二维码")
    public R<Object> qrcode(@RequestParam(defaultValue = "") String inviteCode) throws Exception {
        LoginQrCodeVo vo = loginService.qrcode(inviteCode);
        return R.ok(vo);
    }
    /**
     * 公众号
     * @return
     * @throws Exception
     */
    @PostMapping("/auth/ticket")
    @ApiOperation("扫码验证")
    public R<Object> ticket(@Validated @RequestBody Map<String, String> params) {
        Assert.notNull(params.get("key"), "key参数缺失");
        String key = params.get("key").trim();
        Object object = loginService.ticket(key);
        return R.ok(object);
    }



}
