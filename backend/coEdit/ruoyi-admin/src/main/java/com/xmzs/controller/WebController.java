package com.xmzs.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.xmzs.common.core.domain.R;
import com.xmzs.common.core.domain.model.LoginUser;
import com.xmzs.common.core.validate.AddGroup;
import com.xmzs.common.idempotent.annotation.RepeatSubmit;
import com.xmzs.common.log.annotation.Log;
import com.xmzs.common.log.enums.BusinessType;
import com.xmzs.common.satoken.utils.LoginHelper;
import com.xmzs.system.domain.bo.AiContactusBo;
import com.xmzs.system.domain.bo.SysUserBo;
import com.xmzs.system.service.IAiContactusService;
import com.xmzs.system.service.SysLoginService;
import io.swagger.annotations.ApiOperation;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * packageName com.xmzs.controller
 *
 * @author zhuzi
 * @date 2024/12/4
 * @description 页面常用的小交互
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/web")
public class WebController {
    private final IAiContactusService aiContactusService;
    private final SysLoginService loginService;




    /**
     * 发送留言
     */
    @Log(title = "contactus", businessType = BusinessType.INSERT)
    @PostMapping("/contactus")
    public R<Void> add(@RequestBody AiContactusBo bo) {
        LoginUser loginUser = LoginHelper.getLoginUser();
        bo.setCreatedBy(bo.getCreatedBy());
        bo.setCreatedUserName(loginUser.getUserName());
        aiContactusService.insertByBo(bo);
        return R.ok();
    }

    /**
     * 小程序发送留言
     */
    @PostMapping("/xcxContactus")
    public R<Void> add(@RequestBody AiContactusBo bo,
                       @RequestHeader(value = "Authorization", required = true) String token) {
        LoginUser loginUser = LoginHelper.getLoginUser(token);
        bo.setCreatedBy(bo.getCreatedBy());
        bo.setCreatedUserName(loginUser.getUserName());
        aiContactusService.insertByBo(bo);
        return R.ok();
    }


    @Log(title = "获取邀请信息", businessType = BusinessType.UPDATE)
    @PostMapping("/getInviteInfo")
    public R<Map<String,Object>> editUserInfo() {
        LoginUser loginUser = LoginHelper.getLoginUser();
        Map<String,Object> map =  loginService.getUserInviteInfo(loginUser);
        return R.ok(map);
    }














}
