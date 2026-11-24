package com.xmzs.common.core.domain.model;

import lombok.Data;

import java.io.Serial;

/**
 * 游客登录用户身份权限
 *
 * @author Lion Li
 */
@Data
public class VisitorLoginBody {

    @Serial
    private static final long serialVersionUID = 1L;

    private String code;

    /**
     *  PC("1", "PC端用户"),
     *
     *     XCX("2", "小程序用户");
     */
    private String type;

    /**
     * 手机号码
     */
    private String phoneNumer;

    /**
     * 邀请码
     */
    private String inviteCode;

}
