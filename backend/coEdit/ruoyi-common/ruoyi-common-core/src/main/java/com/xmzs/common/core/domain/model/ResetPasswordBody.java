package com.xmzs.common.core.domain.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 用户登录对象
 *
 * @author Lion Li
 */

@Data
public class ResetPasswordBody {

    private String oldPassword;

    private String newPassword;

    private String lg;

}
